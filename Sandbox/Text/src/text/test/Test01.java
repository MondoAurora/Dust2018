package text.test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dust.qnd.core.QnDDCore;
import dust.qnd.pub.QnDDEnvironment;

public class Test01 implements Test01Constants {
	
	public static void main(String[] args) throws IOException {
//		String fname = "input/JogszabSzerk.html";
		String fname = "input/Ptk.html";
//		String fname = "input/61_2009_IRM.html";
//		String fname = "input/61_2009_IRM.html";
		Document doc = Jsoup.parse(new File(fname), CHS_UTF8);
				
		process(doc);
	}

	public static void process(Document doc) throws IOException {

		QnDDEnvironment env = QnDDCore.getKernel();
		QnDDEntity entity;
		
		Elements list;
		Element e;
		String txt;
		Matcher m;
		int l;
		
		list = doc.getElementsByAttribute("data-tid");
		l = list.size();

//		l = 100;

		for (int i = 0; i < l; ++i) {
			e = list.get(i);
			if (!printElementHeader(e)) {
				txt = e.text();
				String id = e.attr("data-tid");
				
				entity = env.getEntity("Text", id);
				entity.setAttValue(Text.dataId, id);
				entity.setAttValue(Text.text, txt);
				
				m = PT_PARA.matcher(txt);
				if ( m.matches() ) {
					dump(">>>", PGrp.para.get(m), "paragrafus", PGrp.text.get(m));
				}
			}
		}

		list = doc.getElementsByClass("lbjvallist");
		l = list.size();
		
		Set<String> tvRefs = new TreeSet<>();

		if (1 == l) {
			dump("==== Footer ====");
			e = list.first();
			String from;
			String to;
			StringBuilder sb = new StringBuilder();
			
			for (Element n : e.children()) {
				txt = n.text();
//				txt = txt.replace("),", ");").replace("§,", "§;");
				from = to = "-";
				
				m = PT_MOD_HAT.matcher(txt);
				if ( m.matches() ) {
					from = PGrp.date.get(m);
					txt = PGrp.text.get(m);
				} else {
					m = PT_MOD_HATKIV.matcher(txt);
					if ( m.matches() ) {
						to = PGrp.date.get(m);
						txt = PGrp.text.get(m);
					}
				}
				
				Change chg = Change.fromText(txt, sb);
				if ( null != chg ) {
					String head = chg.name();
					for ( String ref : sb.toString().split(",")) {
						m = PT_REF.matcher(ref);
						if ( m.matches() ) {
							String law = PGrp.refLaw.get(m).trim();
							tvRefs.add(law);
							dump(head,  "(", law, "->", PGrp.refLoc.get(m), ")", "(", from, "/", to, ")");							
						} else {
							dump(head,  "BAD REF", ref, "(", from, "/", to, ")");														
							dump("dump", n.text());
						}
						head = "  ";
					}
					
				} else {
					dump("cannot process", n.text());
				}
			}
			
			dump("Referred laws");
			for ( String law : tvRefs ) {
				dump("  ", law);
			}
		}
	}

	public static void dump(Object... objects) throws IOException {
		for (Object o : objects) {
			System.out.print(o);
			System.out.print(" ");
		}
		System.out.println();
	}

	public static boolean printElementHeader(Element e) throws IOException {
		if (e.classNames().contains("agc")) {
			String tn = e.tagName();
			if (tn.startsWith("h")) {
				int lvl = Integer.parseInt(tn.substring(1));
				System.out.print(lvl);
				for (int in = 0; in < lvl; ++in) {
					System.out.print(" ");
				}
				String txt = e.text();
				// if ( txt.startsWith("49.")) {
				// printElementDump(e);
				// }
				System.out.println(txt);
				return true;
			}
		}

		return false;
	}

	public static void checkA(Document doc) throws IOException {
		Elements list;
		Element e;
		int l;
		
		list = doc.getElementsByTag("a");
		l = list.size();
		
		for (int i = 0; i < l; ++i) {
			e = list.get(i);
//			if ( !e.classNames().contains("lbjlink") ) 
			{
				dump(e.text());
			}
		}
	}

	public static void readDoc(Document doc) throws IOException {
		Elements nodelist = doc.getElementsByTag("body");

		if (nodelist != null) {
			org.jsoup.nodes.Element body = nodelist.first();
			List<org.jsoup.nodes.Node> bodyChildren = body.childNodes();

			int len = bodyChildren.size();
			for (int i = 0; i < len; i++) {
				String ml = bodyChildren.get(i).toString();
				if (ml != null) {
					System.out.println(ml);
				}
			}
		}
	}
}
