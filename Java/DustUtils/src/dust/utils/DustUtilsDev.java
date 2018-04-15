package dust.utils;

import java.io.PrintStream;

public class DustUtilsDev {
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
}
