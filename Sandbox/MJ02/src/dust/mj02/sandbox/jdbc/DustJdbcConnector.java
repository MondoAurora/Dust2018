package dust.mj02.sandbox.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

public class DustJdbcConnector implements DustJdbcComponents, DustProcComponents.DustProcActive, DustProcComponents.DustProcPocessor,
        DustProcComponents.DustProcEvaluator, DustProcComponents.DustProcListener {

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
        // TODO Auto-generated method stub

    }

    @Override
    public void activeInit() throws Exception {
        DustEntity eMyType = ((DustRef) DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustDataLinks.EntityPrimaryType))
                .get(RefKey.target);
        DustEntity eMyUnit = ((DustRef) DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, DustCommLinks.PersistentContainingUnit))
                .get(RefKey.target);
        DustEntity eTargetType = DustUtils.optResolve(DustMetaTypes.Type);

        System.out.println("Connecting to database..." + eMyType);

        Map<String, Object> mapAtts = new HashMap<>();

        Dust.processRefs(new RefProcessor() {
            @Override
            public void processRef(DustRef ref) {
                DustEntity eAttDef = ref.get(RefKey.target);
                mapAtts.put(DustUtils.accessEntity(DataCommand.getValue, eAttDef, DustUtils.optResolve(DustGenericAtts.IdentifiedIdLocal)),
                        DustUtils.accessEntity(DataCommand.getValue, ContextRef.self, eAttDef));
            }
        }, eMyType, DustUtils.optResolve(DustMetaLinks.TypeAttDefs), null);

        DustUtilsFactory<String, DustEntity> factTables = new DustUtilsFactory<String, DustEntity>(true) {
            @Override
            protected DustEntity create(String key, Object... hints) {
                DustEntity eTblType = DustUtils.accessEntity(DataCommand.getEntity, eTargetType, ContextRef.self, key);

                DustUtils.accessEntity(DataCommand.setValue, eTblType, DustGenericAtts.IdentifiedIdLocal, key);
                DustUtils.accessEntity(DataCommand.setRef, eTblType, DustCommLinks.PersistentContainingUnit, eMyUnit);

                return eTblType;
            }
        };

        Dust.processRefs(new RefProcessor() {
            @Override
            public void processRef(DustRef ref) {
                DustEntity eTableType = ref.get(RefKey.source);
                String tableId = DustUtils.accessEntity(DataCommand.getValue, eTableType, DustUtils.optResolve(DustGenericAtts.IdentifiedIdLocal));
                factTables.put(tableId, eTableType);
            }
        }, null, DustUtils.optResolve(DustGenericLinks.ConnectedOwner), ContextRef.self);

        Class.forName((String) mapAtts.get("driverClass"));

        System.out.println("Connecting to database...");
        Connection conn = null;

        try {
            String dbName = (String) mapAtts.get("dbName");
            String dbUrl = (String) mapAtts.get("dbUrl") + "/" + dbName;
            conn = DriverManager.getConnection(dbUrl, (String) mapAtts.get("userId"), (String) mapAtts.get("password"));

            System.out.println("Connection successful.");

            ResultSet rs;
            ResultSet rsInt;

            DatabaseMetaData dbmd = conn.getMetaData();
            
            rs = dbmd.getCatalogs();
            System.out.println("CATALOGS");
            DustJdbcUtils.dumpResultSet(rs);

            rs = dbmd.getSchemas();
            System.out.println("SCHEMAS");
            DustJdbcUtils.dumpResultSet(rs);

            System.out.println("Getting tables...");

            // rs = conn.getMetaData().getTables("sakila", null, null, new String[]
            // {"TABLE", "VIEW"});
            rs = dbmd.getTables(dbName, null, null, new String[] { "TABLE" });

            // ResultSetMetaData rsmd = rs.getMetaData();

            StringBuilder sb = null;

            for (boolean ok = rs.first(); ok; ok = rs.next()) {
                String tblName = rs.getString("TABLE_NAME");
                String id = dbName + "_" + tblName;

                sb = DustUtilsJava.sbAppend(sb, ", ", false, id, rs.getString("TABLE_SCHEM"), tblName, rs.getString("TABLE_TYPE"));
                sb.append("\n   ");
                
                DustEntity eTbl = factTables.get(id);
                
                rsInt = dbmd.getPrimaryKeys(dbName, null, tblName);
                System.out.println("\nPKeys");
                DustJdbcUtils.dumpResultSet(rsInt);
                
                rsInt = dbmd.getImportedKeys(dbName, null, tblName);
                System.out.println("\nFKeys");
                DustJdbcUtils.dumpResultSet(rsInt);
            }

            System.out.println("Tables are: " + sb);
            
            rs = dbmd.getColumns(dbName, null, null, null);
            System.out.println("COLUMNS");
            DustJdbcUtils.dumpResultSet(rs);
            
        } finally {
            if (null != conn) {
                conn.close();
            }
        }
    }

    @Override
    public void activeRelease() throws Exception {
        // TODO Auto-generated method stub

    }

}
