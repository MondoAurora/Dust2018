package text.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dust.qnd.pub.QnDDComponents;

public interface Test01Constants extends QnDDComponents {
	enum Text {
		dataId, text
	}
	
	enum PGrp {
		para, text, date,
		year, month, day,
		refLaw, refLoc;
		
		public String toString() {
			return "?<" + name() + ">";
		}
		
		public String get(Matcher m) {
			return m.group(name());
		}
	}
	
	enum Change {
		Create("Megállapította: "), Change("Módosította: "), Remove("Hatályon kívül helyezte: "), Insert("Beiktatta: "), 
		;
		
		private final String header;

		private Change(String header) {
			this.header = header;
		}
		
		public static Change fromText(String str, StringBuilder content) {
			for ( Change c : values()) {
				if ( str.startsWith(c.header)) {
					content.replace(0, content.length(), str.substring(c.header.length()));
					return c;
				}
			}
			content.replace(0, content.length(), str);
			return null;
		}
		
	}

	public static String ptGroup(String grp) {
		return "";
	}
	
	Pattern PT_PARA = Pattern.compile("^(" + PGrp.para + "(\\d*\\.)*)\\s*§\\s*(" + PGrp.text + ".*)");
	Pattern PT_DATE = Pattern.compile("(" + PGrp.year + "\\d{4})\\.\\s*(" + PGrp.month + "[XIV]*).\\s*(" + PGrp.day + "\\d*)\\.*");
	Pattern PT_REF = Pattern.compile("(" + PGrp.refLaw + ".*)\\s+(" + PGrp.refLoc + "(\\d+\\.)+\\s*§\\s*.*)");
	
	Pattern PT_MOD_HAT = Pattern.compile("^(" + PGrp.text + ".*)\\s*Hatályos:\\s*(" + PGrp.date + ".*)-.*");
	Pattern PT_MOD_HATKIV = Pattern.compile("^(" + PGrp.text + ".*)\\s*Hatálytalan:\\s*(" + PGrp.date + ".*)-.*");

}
