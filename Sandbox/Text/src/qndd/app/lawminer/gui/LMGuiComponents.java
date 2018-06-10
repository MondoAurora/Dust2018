package qndd.app.lawminer.gui;

import javax.swing.JInternalFrame;

import dust.qnd.pub.QnDDConstants;
import dust.qnd.util.QnDDUtils;

public interface LMGuiComponents extends QnDDConstants {
	int DEFAULT_GAP = 5;
	
	String JOGTAR_SEARCH_PREFIX = "https://net.jogtar.hu/gyorskereso?keyword=";
	String JOGTAR_GET_PREFIX = "https://net.jogtar.hu/jogszabaly?docid=";
	
	String FMT_SEARCH_CACHE = "data/search/{0}.html";
	String FMT_GET_CACHE = "data/tv/{0}.html";
	
	int JOGTAR_TIMEOUT_SEC = 20;
	
	enum LMGuiTexts {
		AppTitle, Search, SearchWords
	}
	
	class LMGuiDataFrame extends JInternalFrame {
		private static final long serialVersionUID = 1L;
		
		protected LMGuiDataFrame(Enum<?> title) {
			super(QnDDUtils.formatEnum(title));
		}
	}
}
