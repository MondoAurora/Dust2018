package dust.mj02.sandbox.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;
import java.util.TreeMap;

import dust.mj02.dust.Dust;
import dust.utils.DustUtilsJava;

public class DustJdbcUtils implements DustJdbcComponents {

    public static final void dumpResultSet(ResultSet rs, String... colNames ) throws Exception {
        if ( null != colNames ) {
            colNames = getAllColumnNames(rs);
        }
        
        StringBuilder sb = null;
        for ( String col : colNames ) {
            sb = DustUtilsJava.sbAppend(sb, "\t", true, col);
        }
        System.out.println(sb);
        
        for ( boolean ok = rs.first(); ok; ok = rs.next() ) {
            sb = null;
            
            for ( String col : colNames ) {
                sb = DustUtilsJava.sbAppend(sb, "\t", true, rs.getObject(col));
            }
            System.out.println(sb);
        }
    }

    public static String[] getAllColumnNames(ResultSet rs) throws Exception {
        String[] colNames;
        ResultSetMetaData rsmd = rs.getMetaData();
        int cc = rsmd.getColumnCount();
        colNames = new String[cc];
        for ( int i = 0; i < cc; ++i ) {
            colNames[i] = rsmd.getColumnName(i+1);
        }
        return colNames;
    }

    public static Map<String, Object> mapFromRS(ResultSet rsFrom, String... colNames) {
        Map<String, Object> ret = new TreeMap<String, Object>();
        
        if ( null != colNames ) {
            try {
                colNames = getAllColumnNames(rsFrom);
            } catch (Throwable e) {
                Dust.wrapAndRethrowException("", e);
            }
        }
        
        for ( String col : colNames ) {
            try {
                ret.put(col, rsFrom.getObject(col));
            } catch (Throwable e) {
                ret.put(col, e);
            }
        }
        
        return ret;
    }
}
