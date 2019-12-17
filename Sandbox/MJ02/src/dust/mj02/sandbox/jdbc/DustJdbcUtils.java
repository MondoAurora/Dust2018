package dust.mj02.sandbox.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import dust.utils.DustUtilsJava;

public class DustJdbcUtils implements DustJdbcComponents {

    public static final void dumpResultSet(ResultSet rs, String... colNames ) throws Exception {
        if ( null != colNames ) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int cc = rsmd.getColumnCount();
            colNames = new String[cc];
            for ( int i = 0; i < cc; ++i ) {
                colNames[i] = rsmd.getColumnName(i+1);
            }
        }
        
        for ( boolean ok = rs.first(); ok; ok = rs.next() ) {
            StringBuilder sb = null;
            
            for ( String col : colNames ) {
                sb = DustUtilsJava.sbAppend(sb, ", ", true, rs.getObject(col));
            }
            System.out.println(sb);
        }
    }
}
