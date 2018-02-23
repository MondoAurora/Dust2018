package dust.pub;

import java.io.PrintStream;

public class DustUtilsDev implements DustComponents {
	private static PrintStream out = System.out;

	public static void dump(Object... stuff) {
		boolean hasContent = false;
		for ( Object ob : stuff) {
			if ( null != ob ) {
				out.print(ob);
				out.print(" ");
				hasContent = true;
			}
		}
		
		if ( hasContent ) {
			out.println();
		}
	}

	public static boolean isEmpty(String str) {
		return (null == str) ? true : str.isEmpty();
	}
	
	public static void wrapException(Throwable t, DustEntity errInfo) {
		if ( t instanceof DustException ) {
			throw (DustException) t;
		} else {
			throw new DustException(errInfo, t);
		}
	}

}
