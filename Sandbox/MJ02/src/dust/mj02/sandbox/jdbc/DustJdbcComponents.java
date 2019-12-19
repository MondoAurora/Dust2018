package dust.mj02.sandbox.jdbc;

import dust.mj02.dust.knowledge.DustKernelComponents;

public interface DustJdbcComponents  extends DustKernelComponents {
    
    String JDBC_TABLE_NAME = "TABLE_NAME";
    String JDBC_TABLE = "TABLE";
    String JDBC_TYPE_NAME = "TYPE_NAME";
    String JDBC_PKTABLE_NAME = "PKTABLE_NAME";
    String JDBC_FKCOLUMN_NAME = "FKCOLUMN_NAME";
    String JDBC_COLUMN_NAME = "COLUMN_NAME";

    
    enum DustJdbcTypes implements DustEntityKey {
        JdbcConnector, JdbcDataType, JdbcTable, JdbcColumn, JdbcRecord
    };
    
    enum DustJdbcAtts implements DustEntityKey {
        RecordContent, RecordLastUpdate, RecordOwner
    }
    
    enum DustJdbcLinks implements DustEntityKey {
        DataTypeToDustType, ConnectorDataTypes, ColumnType
    }
    
    enum DustJdbcServices implements DustEntityKey {
        JdbcConnector
    };


}
