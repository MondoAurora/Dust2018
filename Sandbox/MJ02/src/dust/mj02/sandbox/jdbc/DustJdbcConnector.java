package dust.mj02.sandbox.jdbc;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.text.DustTextComponents;
import dust.utils.DustUtilsFactory;

public class DustJdbcConnector implements DustJdbcComponents, DustProcComponents.DustProcActive, DustProcComponents.DustProcPocessor,
        DustProcComponents.DustProcEvaluator, DustProcComponents.DustProcListener {

    public static String[] COL_DEF_COLS;

    class ColumnInfo {
        final String colName;
        final DustEntity eColumn;

        Map<String, Object> colData;

        boolean primaryKey;
        Map<String, Object> fKeyInfo;

        public ColumnInfo(String colName, DustEntity eColumn) {
            this.colName = colName;
            this.eColumn = eColumn;
        };

        public ColumnInfo(String colName, ResultSet rsFrom, DustEntity eTable) {
            colData = DustJdbcUtils.mapFromRS(rsFrom, COL_DEF_COLS);
            this.colName = colName;

            eColumn = DustUtils.accessEntity(DataCommand.getEntity, DustMetaTypes.AttDef);

            DustUtils.accessEntity(DataCommand.setValue, eColumn, DustGenericAtts.IdentifiedIdLocal, colName);
            DustUtils.accessEntity(DataCommand.setRef, eColumn, DustMetaLinks.AttDefParent, eTable);
            DustUtils.accessEntity(DataCommand.setRef, eColumn, DustMetaLinks.AttDefType, DustMetaAttDefTypeValues.AttDefIdentifier);
        }
    }

    class TableInfo {
        final String tblName;
        final DustEntity eTable;

        boolean dbVerified;

        DustUtilsFactory<String, ColumnInfo> columns = new DustUtilsFactory<String, ColumnInfo>(true) {
            @Override
            protected ColumnInfo create(String key, Object... hints) {
                return new ColumnInfo(key, (ResultSet) hints[0], eTable);
            }
        };

        DustUtilsFactory<String, DustEntity> factData = new DustUtilsFactory<String, DustEntity>(true) {
            @Override
            protected DustEntity create(String key, Object... hints) {
                DustEntity e = DustUtils.accessEntity(DataCommand.getEntity, eTable);

                DustUtils.accessEntity(DataCommand.setValue, e, DustGenericAtts.IdentifiedIdLocal, key);
                DustUtils.accessEntity(DataCommand.setRef, e, DustGenericLinks.ConnectedOwner, eTable);
                DustUtils.accessEntity(DataCommand.setRef, e, DustCommLinks.PersistentContainingUnit, eMyUnit);

                return e;
            }
        };

        public TableInfo(String tblName, DustEntity eTblType) {
            this.tblName = tblName;
            this.eTable = eTblType;

            Dust.processRefs(new RefProcessor() {
                @Override
                public void processRef(DustRef ref) {
                    DustEntity eAttDef = ref.get(RefKey.source);
                    String attId = DustUtils.accessEntity(DataCommand.getValue, eAttDef, DustUtils.optResolve(DustGenericAtts.IdentifiedIdLocal));
                    columns.put(attId, new ColumnInfo(attId, eAttDef));
                }
            }, null, DustUtils.optResolve(DustMetaLinks.AttDefParent), eTable);

            Dust.processRefs(new RefProcessor() {
                @Override
                public void processRef(DustRef ref) {
                    DustEntity eLinkDef = ref.get(RefKey.source);
                    String linkId = DustUtils.accessEntity(DataCommand.getValue, eLinkDef, DustUtils.optResolve(DustGenericAtts.IdentifiedIdLocal));
                    columns.put(linkId, new ColumnInfo(linkId, eLinkDef));
                }
            }, null, DustUtils.optResolve(DustMetaLinks.LinkDefParent), eTable);

            Dust.processRefs(new RefProcessor() {
                @Override
                public void processRef(DustRef ref) {
                    DustEntity eRecord = ref.get(RefKey.source);
                    String recId = DustUtils.accessEntity(DataCommand.getValue, eRecord, DustGenericAtts.IdentifiedIdLocal);
                    factData.put(recId, eRecord);
                }
            }, null, DustUtils.optResolve(DustGenericLinks.ConnectedOwner), eTable);

        };

        public TableInfo(String tblName) {
            this.tblName = tblName;

            eTable = DustUtils.accessEntity(DataCommand.getEntity, DustMetaTypes.Type, ContextRef.self);

            DustUtils.accessEntity(DataCommand.setValue, eTable, DustGenericAtts.IdentifiedIdLocal, tblName);
            DustUtils.accessEntity(DataCommand.setRef, eTable, DustCommLinks.PersistentContainingUnit, eMyUnit);
        };

        private void loadDbMeta() throws Exception {
            ResultSet rs = dbMetaData.getPrimaryKeys(dbName, null, tblName);
            String colName;
            for (boolean ok = rs.first(); ok; ok = rs.next()) {
                colName = rs.getString("COLUMN_NAME");
                columns.get(colName).primaryKey = true;
            }

            rs = dbMetaData.getImportedKeys(dbName, null, tblName);
            DustJdbcUtils.dumpResultSet(rs);
            for (boolean ok = rs.first(); ok; ok = rs.next()) {
                colName = rs.getString("FKCOLUMN_NAME");
                ColumnInfo columnInfo = columns.get(colName);
                columnInfo.fKeyInfo = DustJdbcUtils.mapFromRS(rs);
                DustEntity eCol = columnInfo.eColumn;

                if (null == DustUtils.accessEntity(DataCommand.getValue, eCol, DustMetaLinks.LinkDefParent)) {
                    DustUtils.accessEntity(DataCommand.dropEntity, eCol);

                    eCol = DustUtils.accessEntity(DataCommand.getEntity, DustMetaTypes.LinkDef);

                    DustUtils.accessEntity(DataCommand.setValue, eCol, DustGenericAtts.IdentifiedIdLocal, colName);
                    DustUtils.accessEntity(DataCommand.setRef, eCol, DustMetaLinks.LinkDefParent, eTable);
                    DustUtils.accessEntity(DataCommand.setRef, eCol, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSingle);
                }
            }
        }

        public void registerColumn(ResultSet rs) throws Exception {
            String colName = rs.getString("COLUMN_NAME");
            columns.get(colName, rs);
        }
    }

    Map<String, Object> mapAtts = new HashMap<>();

    String dbName;
    DustEntity eMyUnit;

    DustUtilsFactory<String, TableInfo> factTableInfos = new DustUtilsFactory<String, TableInfo>(true) {
        @Override
        protected TableInfo create(String key, Object... hints) {
            return new TableInfo(key);
        }
    };

    Connection conn = null;
    DatabaseMetaData dbMetaData;

    private static Set<WeakReference<Connection>> CONNECTIONS = new HashSet<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                for (WeakReference<Connection> wrc : CONNECTIONS) {
                    Connection c = wrc.get();
                    if (null != c) {
                        try {
                            releaseConn(c, null);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void updateParams() throws Exception {
        eMyUnit = ((DustRef) DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustCommLinks.PersistentContainingUnit))
                .get(RefKey.target);

        mapAtts.clear();

        DustEntity eMyType = ((DustRef) DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustDataLinks.EntityPrimaryType))
                .get(RefKey.target);

        Dust.processRefs(new RefProcessor() {
            @Override
            public void processRef(DustRef ref) {
                DustEntity eAttDef = ref.get(RefKey.target);
                mapAtts.put(DustUtils.accessEntity(DataCommand.getValue, eAttDef, DustUtils.optResolve(DustGenericAtts.IdentifiedIdLocal)),
                        DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, eAttDef));
            }
        }, eMyType, DustUtils.optResolve(DustMetaLinks.TypeAttDefs), null);

        Class.forName((String) mapAtts.get("driverClass"));

    }

    private void optCreateConn() throws Exception {
        if ((null == conn) || conn.isClosed()) {
            System.out.println("Connecting to database...");

            try {
                dbName = (String) mapAtts.get("dbName");
                String dbUrl = (String) mapAtts.get("dbUrl") + "/" + dbName;
                conn = DriverManager.getConnection(dbUrl, (String) mapAtts.get("userId"), (String) mapAtts.get("password"));

                CONNECTIONS.add(new WeakReference<Connection>(conn));

                dbMetaData = conn.getMetaData();

                System.out.println("Connection successful.");

                ResultSet rs;

                rs = dbMetaData.getCatalogs();
                System.out.println("CATALOGS");
                DustJdbcUtils.dumpResultSet(rs);

                rs = dbMetaData.getSchemas();
                System.out.println("SCHEMAS");
                DustJdbcUtils.dumpResultSet(rs);

                Dust.processRefs(new RefProcessor() {
                    @Override
                    public void processRef(DustRef ref) {
                        DustEntity eTableType = ref.get(RefKey.source);
                        String tableId = DustUtils.accessEntity(DataCommand.getValue, eTableType,
                                DustUtils.optResolve(DustGenericAtts.IdentifiedIdLocal));
                        factTableInfos.put(tableId, new TableInfo(tableId, eTableType));
                    }
                }, null, DustUtils.optResolve(DustGenericLinks.ConnectedOwner), ContextRef.self);

            } catch (Throwable e) {
                releaseConn(conn, e);
                conn = null;
                dbMetaData = null;
            }
        }
    }

    private static void releaseConn(Connection conn, Throwable cause) throws Exception {
        if (null != conn) {
            if (!conn.isClosed()) {
                if (!conn.getAutoCommit()) {
                    if (null == cause) {
                        conn.commit();
                    } else {
                        conn.rollback();
                    }
                }
                conn.close();
                System.out.println("DB connection closed.");
            }
            conn = null;
        }
    }

    @Override
    public void activeInit() throws Exception {
        updateParams();

        optCreateConn();

        try {
            ResultSet rs;

            System.out.println("Getting tables...");

            rs = dbMetaData.getTables(dbName, null, null, new String[] { "TABLE" });

            DustJdbcUtils.dumpResultSet(rs);

            for (TableInfo ti : factTableInfos.values()) {
                ti.dbVerified = false;
            }

            for (boolean ok = rs.first(); ok; ok = rs.next()) {
                String tblName = rs.getString("TABLE_NAME");
                factTableInfos.get(tblName).dbVerified = true;
            }

            rs = dbMetaData.getColumns(dbName, null, null, null);

            if (null == COL_DEF_COLS) {
                COL_DEF_COLS = DustJdbcUtils.getAllColumnNames(rs);
            }

            System.out.println("COLUMNS");
            for (boolean ok = rs.first(); ok; ok = rs.next()) {
                String tblName = rs.getString("TABLE_NAME");
                TableInfo ti = factTableInfos.peek(tblName);
                if (null != ti) {
                    ti.registerColumn(rs);
                }
            }

            for (TableInfo ti : factTableInfos.values()) {
                if (ti.dbVerified) {
                    ti.loadDbMeta();
                }
            }

        } catch (Throwable e) {
            releaseConn(conn, e);
        }
    }

    @Override
    public void activeRelease() throws Exception {
        releaseConn(conn, null);
        conn = null;
        dbMetaData = null;
    }

    @Override
    public void dustProcListenerProcessChange() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public Object evaluatorEvaluate() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void processorProcess() throws Exception {
        String query = DustUtils.getMsgVal(DustTextComponents.DustTextAtts.TextSpanString, false);

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        DustJdbcUtils.dumpResultSet(rs);

        ResultSetMetaData rsmd = rs.getMetaData();
        String tblName = rsmd.getTableName(1);

        TableInfo ti = factTableInfos.peek(tblName);
        int cc = rsmd.getColumnCount();

        DustUtilsFactory<DustEntity, DustMetaAttDefTypeValues> attTypes = new DustUtilsFactory<DustEntity, DustMetaAttDefTypeValues>(false) {
            @Override
            protected DustMetaAttDefTypeValues create(DustEntity key, Object... hints) {
                Object value = hints[0];

                DustMetaAttDefTypeValues valType = DustMetaAttDefTypeValues.AttDefRaw;

                if (String.class.isInstance(value)) {
                    valType = DustMetaAttDefTypeValues.AttDefIdentifier;
                } else if (Number.class.isInstance(value)) {
                    if (Double.class.isInstance(value) || Float.class.isInstance(value)) {
                        valType = DustMetaAttDefTypeValues.AttDefDouble;
                    } else {
                        valType = DustMetaAttDefTypeValues.AttDefLong;
                    }
                } else if (Boolean.class.isInstance(value)) {
                    valType = DustMetaAttDefTypeValues.AttDefBool;
                }

                DustUtils.accessEntity(DataCommand.setRef, key, DustMetaLinks.AttDefType, valType);

                return valType;
            }
        };

        DustEntity[] cols = new DustEntity[cc];
        int pKeyIdx = 0;
        Map<DustEntity, TableInfo> mapLinks = new HashMap<>();

        for (int i = 0; i < cc; ++i) {
            int colIdx = i + 1;
            String colName = rsmd.getColumnName(colIdx);
            ColumnInfo colInfo = ti.columns.peek(colName);
            DustEntity eColDef = colInfo.eColumn;
            cols[i] = eColDef;

            if (colInfo.primaryKey) {
                pKeyIdx = colIdx;
            }

            if (null == colInfo.fKeyInfo) {
                DustUtils.accessEntity(DataCommand.setRef, eColDef, DustMetaLinks.AttDefType, null);
            } else {
                mapLinks.put(eColDef, factTableInfos.peek((String) colInfo.fKeyInfo.get("PKTABLE_NAME")));
            }
        }

        for (boolean ok = rs.first(); ok; ok = rs.next()) {
            String id = rs.getObject(pKeyIdx).toString();

            DustEntity e = ti.factData.get(id);

            for (int i = 0; i < cc; ++i) {
                DustEntity eColDef = cols[i];
                Object value = rs.getObject(i + 1);
                TableInfo tblRef = mapLinks.get(eColDef);

                if (null == tblRef) {
                    if (null != value) {
                        DustMetaAttDefTypeValues valType = attTypes.get(eColDef, value);
                        switch (valType) {
                        case AttDefBool:
                            break;
                        case AttDefDouble:
                            value = ((Number) value).doubleValue();
                            break;
                        case AttDefIdentifier:
                            break;
                        case AttDefLong:
                            value = ((Number) value).longValue();
                            break;
                        case AttDefRaw:
                            value = value.toString();
                            break;
                        }
                    }
                    DustUtils.accessEntity(DataCommand.setValue, e, eColDef, value);
                } else {
                    DustEntity eRef = tblRef.factData.get(value.toString());
                    DustUtils.accessEntity(DataCommand.setRef, e, eColDef, eRef);                    
                }
            }
        }
    }

}
