package dust.mj02.sandbox.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustPopulatedFactory;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.text.DustTextComponents;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;
import dust.utils.DustUtilsMuteManager;

public class DustJdbcConnector implements DustJdbcComponents, DustProcComponents.DustProcActive, DustProcComponents.DustProcPocessor,
        DustProcComponents.DustProcEvaluator, DustProcComponents.DustProcListener {

    class ColumnInfo {
        final String colName;
        final DustEntity eColumn;

        Map<String, Object> colData;

        boolean primaryKey;
        Map<String, Object> keyData;
        TableInfo foreignTableInfo;

        DustMetaAttDefTypeValues valType;

        public ColumnInfo(String colName, DustEntity eColumn) {
            this.colName = colName;
            this.eColumn = eColumn;
        };

        public ColumnInfo(String colName, ResultSet rsFrom, TableInfo ti) {
            colData = DustJdbcUtils.mapFromRS(rsFrom);
            this.colName = colName;

            boolean isRef = initKeyData(ti);

            eColumn = DustUtils.accessEntity(DataCommand.getEntity, isRef ? DustMetaTypes.LinkDef : DustMetaTypes.AttDef);

            DustUtils.accessEntity(DataCommand.setValue, eColumn, DustGenericAtts.IdentifiedIdLocal, colName);
            String typeName = (String) colData.get(JDBC_TYPE_NAME);
            DustEntity eDbType = factTypeInfos.get(typeName);
            DustUtils.accessEntity(DataCommand.setRef, eColumn, DustJdbcLinks.ColumnType, eDbType);

            if (isRef) {
                DustUtils.accessEntity(DataCommand.setRef, eColumn, DustMetaLinks.LinkDefParent, ti.eTable);

                DustUtils.accessEntity(DataCommand.setRef, eColumn, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSingle);
                DustUtils.accessEntity(DataCommand.setRef, eColumn, DustMetaLinks.LinkDefItemTypePrimary, foreignTableInfo.eTable);
            } else {
                DustUtils.accessEntity(DataCommand.setRef, eColumn, DustMetaLinks.AttDefParent, ti.eTable);

                optSetAttType(eDbType);
            }
        }

        public boolean initKeyData(TableInfo ti) {
            boolean isRef = false;

            keyData = ti.pKeyInfo.get(colName);
            primaryKey = null != keyData;

            if (!primaryKey) {
                keyData = ti.fKeyInfo.get(colName);
                if (null != keyData) {
                    isRef = true;
                    String pkTbl = (String) keyData.get(JDBC_PKTABLE_NAME);
                    foreignTableInfo = factTableInfos.get(pkTbl);
                }
            }

            return isRef;
        }

        public void optSetAttType(DustEntity eDbType) {
            if (null == valType) {
                DustEntity eAttType = null;
                DustRef refAttType = DustUtils.accessEntity(DataCommand.getValue, eColumn, DustMetaLinks.AttDefType);
                if (null == refAttType) {
                    if (null == eDbType) {
                        eDbType = ((DustRef) DustUtils.accessEntity(DataCommand.getValue, eColumn, DustJdbcLinks.ColumnType)).get(RefKey.target);
                    }
                    DustRef refDustType = (DustRef) DustUtils.accessEntity(DataCommand.getValue, eDbType, DustJdbcLinks.DataTypeToDustType);
                    if (null != refDustType) {
                        eAttType = refDustType.get(RefKey.target);
                        DustUtils.accessEntity(DataCommand.setRef, eColumn, DustMetaLinks.AttDefType, eAttType);
                    }
                } else {
                    eAttType = refAttType.get(RefKey.target);
                }
                valType = EntityResolver.getKey(eAttType);
            }
        }
    }

    class TableInfo {
        final String tblName;
        final DustEntity eTable;

        private boolean dbVerified;

        Map<String, Map<String, Object>> pKeyInfo = new TreeMap<>();
        Map<String, Map<String, Object>> fKeyInfo = new TreeMap<>();

        DustUtilsFactory<String, ColumnInfo> columns = new DustUtilsFactory<String, ColumnInfo>(true) {
            @Override
            protected ColumnInfo create(String key, Object... hints) {
                return new ColumnInfo(key, (ResultSet) hints[0], TableInfo.this);
            }
        };

        DustPopulatedFactory.Entity pfData;

        // DustUtilsFactory<String, DustEntity> factData = new DustUtilsFactory<String,
        // DustEntity>(true) {
        // @Override
        // protected DustEntity create(String key, Object... hints) {
        // DustEntity e = DustUtils.accessEntity(DataCommand.getEntity, eTable);
        //
        // DustUtils.accessEntity(DataCommand.setValue, e,
        // DustGenericAtts.IdentifiedIdLocal, key);
        // DustUtils.accessEntity(DataCommand.setRef, e,
        // DustGenericLinks.ConnectedOwner, eTable);
        // DustUtils.accessEntity(DataCommand.setRef, e,
        // DustCommLinks.PersistentContainingUnit, eMyUnit);
        //
        // return e;
        // }
        // };

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

            // Dust.processRefs(new RefProcessor() {
            // @Override
            // public void processRef(DustRef ref) {
            // DustEntity eRecord = ref.get(RefKey.source);
            // String recId = DustUtils.accessEntity(DataCommand.getValue, eRecord,
            // DustGenericAtts.IdentifiedIdLocal);
            // factData.put(recId, eRecord);
            // }
            // }, null, DustUtils.optResolve(DustGenericLinks.ConnectedOwner), eTable);

            initData();
        };

        public TableInfo(String tblName) {
            this.tblName = tblName;

            eTable = DustUtils.accessEntity(DataCommand.getEntity, DustMetaTypes.Type, ContextRef.self);

            DustUtils.accessEntity(DataCommand.setValue, eTable, DustGenericAtts.IdentifiedIdLocal, tblName);
            DustUtils.accessEntity(DataCommand.setRef, eTable, DustCommLinks.PersistentContainingUnit, eMyUnit);

            initData();
        };

        private void initData() {
            pfData = new DustPopulatedFactory.Entity(eTable, DustGenericLinks.ConnectedOwner, false, eTable);
            pfData.setUnit(eMyUnit);
        }

        public void setDbVerified(boolean verified) throws Exception {
            this.dbVerified = verified;

            if (dbVerified) {
                String colName;

                ResultSet rs = dbMetaData.getPrimaryKeys(dbName, null, tblName);
                for (boolean ok = rs.first(); ok; ok = rs.next()) {
                    colName = rs.getString(JDBC_COLUMN_NAME);
                    pKeyInfo.put(colName, DustJdbcUtils.mapFromRS(rs));
                }

                rs = dbMetaData.getImportedKeys(dbName, null, tblName);
                for (boolean ok = rs.first(); ok; ok = rs.next()) {
                    colName = rs.getString(JDBC_FKCOLUMN_NAME);
                    fKeyInfo.put(colName, DustJdbcUtils.mapFromRS(rs));
                }
            }
        }

        public void registerColumn(ResultSet rs) throws Exception {
            String colName = rs.getString(JDBC_COLUMN_NAME);
            ColumnInfo colInfo = columns.get(colName, rs);
            colInfo.initKeyData(this);
        }

        public void clean() {
            for (DustEntity e : pfData.values()) {
                DustUtils.accessEntity(DataCommand.dropEntity, e);
            }
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

    DustUtilsFactory<String, DustEntity> factTypeInfos = new DustUtilsFactory<String, DustEntity>(true) {
        @Override
        protected DustEntity create(String key, Object... hints) {
            DustEntity e = DustUtils.accessEntity(DataCommand.getEntity, DustJdbcTypes.JdbcDataType);

            DustUtils.accessEntity(DataCommand.setValue, e, DustGenericAtts.IdentifiedIdLocal, key);
            DustUtils.accessEntity(DataCommand.setRef, ContextRef.self, DustJdbcLinks.ConnectorDataTypes, e);
            DustUtils.accessEntity(DataCommand.setRef, e, DustCommLinks.PersistentContainingUnit, eMyUnit);

            return e;
        }
    };

    Connection conn = null;
    DatabaseMetaData dbMetaData;

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

                DustJdbcUtils.addConn(conn);

                dbMetaData = conn.getMetaData();

                System.out.println("Connection successful.");

                Dust.processRefs(new RefProcessor() {
                    @Override
                    public void processRef(DustRef ref) {
                        DustEntity eTableType = ref.get(RefKey.source);
                        String tableId = DustUtils.accessEntity(DataCommand.getValue, eTableType,
                                DustUtils.optResolve(DustGenericAtts.IdentifiedIdLocal));
                        factTableInfos.put(tableId, new TableInfo(tableId, eTableType));
                    }
                }, null, DustUtils.optResolve(DustGenericLinks.ConnectedOwner), ContextRef.self);

                Dust.processRefs(new RefProcessor() {
                    @Override
                    public void processRef(DustRef ref) {
                        DustEntity e = ref.get(RefKey.target);
                        String id = DustUtils.accessEntity(DataCommand.getValue, e, DustUtils.optResolve(DustGenericAtts.IdentifiedIdLocal));
                        factTypeInfos.put(id, e);
                    }
                }, ContextRef.self, DustUtils.optResolve(DustJdbcLinks.ConnectorDataTypes), null);

            } catch (Throwable e) {
                releaseConn(conn, e);
                conn = null;
                dbMetaData = null;
            }
        }
    }

    public static void releaseConn(Connection conn, Throwable cause) throws Exception {
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

            rs = dbMetaData.getTables(dbName, null, null, new String[] { JDBC_TABLE });

            DustJdbcUtils.dumpResultSet(rs);

            for (TableInfo ti : factTableInfos.values()) {
                ti.setDbVerified(false);
            }

            for (boolean ok = rs.first(); ok; ok = rs.next()) {
                String tblName = rs.getString(JDBC_TABLE_NAME);
                factTableInfos.get(tblName).setDbVerified(true);
            }

            rs = dbMetaData.getColumns(dbName, null, null, null);

            for (boolean ok = rs.first(); ok; ok = rs.next()) {
                String tblName = rs.getString(JDBC_TABLE_NAME);
                TableInfo ti = factTableInfos.peek(tblName);
                if (null != ti) {
                    ti.registerColumn(rs);
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
        try {
            DustUtilsMuteManager.mute(DustUtilsMuteManager.MutableModule.GUI, true);
            String query = DustUtils.getMsgVal(DustTextComponents.DustTextAtts.TextSpanString, false);

            DustUtils.accessEntity(DataCommand.clearRefs, ContextRef.msg, DustGenericLinks.ConnectedExtends);
            DustUtils.accessEntity(DataCommand.clearRefs, ContextRef.msg, DustGenericLinks.ConnectedRequires);

            if (DustUtilsJava.isEmpty(query)) {
                for (TableInfo ti : factTableInfos.values()) {
                    ti.clean();
                }
                return;
            }

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            DustJdbcUtils.dumpResultSet(rs);

            ResultSetMetaData rsmd = rs.getMetaData();
            String tblName = rsmd.getTableName(1);

            TableInfo ti = factTableInfos.peek(tblName);
            int cc = rsmd.getColumnCount();

            ColumnInfo[] cols = new ColumnInfo[cc];
            int pKeyIdx = 0;
            Map<DustEntity, TableInfo> mapLinks = new HashMap<>();

            for (int i = 0; i < cc; ++i) {
                int colIdx = i + 1;
                String colName = rsmd.getColumnName(colIdx);
                ColumnInfo colInfo = ti.columns.peek(colName);
                DustEntity eColDef = colInfo.eColumn;
                cols[i] = colInfo;

                if (colInfo.primaryKey) {
                    pKeyIdx = colIdx;
                    colInfo.optSetAttType(null);
                } else {
                    if (null != colInfo.foreignTableInfo) {
                        mapLinks.put(eColDef, colInfo.foreignTableInfo);
                    } else {
                        colInfo.optSetAttType(null);
                    }
                }
            }

            for (boolean ok = rs.first(); ok; ok = rs.next()) {
                String id = rs.getObject(pKeyIdx).toString();

                DustEntity e = ti.pfData.get(id);

                for (int i = 0; i < cc; ++i) {
                    ColumnInfo colInfo = cols[i];
                    Object value = rs.getObject(i + 1);

                    if (null == colInfo.foreignTableInfo) {
                        if (null != value) {
                            DustMetaAttDefTypeValues valType = colInfo.valType;
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
                        DustUtils.accessEntity(DataCommand.setValue, e, colInfo.eColumn, value);
                    } else {
                        DustEntity eRef = colInfo.foreignTableInfo.pfData.get(value.toString());
                        DustUtils.accessEntity(DataCommand.setRef, e, colInfo.eColumn, eRef);
                        DustUtils.accessEntity(DataCommand.setRef, ContextRef.msg, DustGenericLinks.ConnectedRequires, eRef);
                    }
                }

                DustUtils.accessEntity(DataCommand.setRef, ContextRef.msg, DustGenericLinks.ConnectedExtends, e);
            }
        } finally {
            DustUtilsMuteManager.mute(DustUtilsMuteManager.MutableModule.GUI, false);
        }
    }

}
