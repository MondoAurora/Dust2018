package dust.qnd.util;

import java.io.File;

import javax.swing.text.html.HTML;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dust.qnd.pub.QnDDComponents;
import dust.qnd.pub.QnDDLogic;
import dust.qnd.pub.QnDDServices;
import dust.utils.DustUtilsJava;
import text.test.Test01;

public class QnDDLogicJsoup extends QnDDLogic implements QnDDComponents, QnDDServices.QnDDProcessor {
	
	interface DocumentReader {
		void readDoc(Document doc) throws Exception;
	}

	@Override
	public void process() throws Exception {
		String fname = getAttValue(QnDDAttStreamProc.uri);
		Document doc = Jsoup.parse(new File(fname), "UTF-8");

		String sel = getAttValue(QnDDAttSelector.selCondition);
		String mapKey = null;

		if (!DustUtilsJava.isEmpty(sel)) {
			Elements meta = doc.head().getElementsByTag(HTML.Tag.META.toString());
			for ( Element e : meta ) {
				if ( DustUtilsJava.isEqual(sel, e.attr(HTML.Attribute.NAME.toString())) ) {
					mapKey = e.attr(HTML.Attribute.CONTENT.toString());
					break;
				}
			}
		}
		
		QnDDEntity n = getRef(QnDDLinkChain.Next, mapKey);
		
		DocumentReader reader  = n.getLogic(DocumentReader.class);
		
		reader.readDoc(doc);	

		Test01.process(doc);
	}
}
