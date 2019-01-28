package qndd.app.lawminer.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.Security;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.html.HTML;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dust.qnd.pub.QnDDException;
import dust.qnd.util.QnDDUtils;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;
import text.test.Test01;

public class LMGuiPanelLaw extends JPanel implements LMGuiComponents {
	private static final long serialVersionUID = 1L;
	
	static class EntityTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		
		Enum<?>[] cols;
		ArrayList<QnDDEntity> values;

		public EntityTableModel(ArrayList<QnDDEntity> values, Enum<?>... cols) {
			super();
			this.cols = cols;
			this.values = values;
		}

		@Override
		public int getColumnCount() {
			return cols.length;
		}
		
		@Override
		public String getColumnName(int column) {
			return QnDDUtils.formatEnum(cols[column]);
		}

		@Override
		public int getRowCount() {
			return values.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			return values.get(row).getAttValue(cols[col]);
		}
		
		public void updated() {
			fireTableDataChanged();
		}
	}

	JTextField tfSearch;

	DustUtilsFactory<String, Document> factSearch = new DustUtilsFactory<String, Document>(false) {
		@Override
		protected Document create(String key, Object... hints) {
			try {
				String fName = MessageFormat.format(FMT_SEARCH_CACHE, key);
				File f = new File(fName);

				if (f.exists()) {
					return Jsoup.parse(f, CHS_UTF8);
				} else {
					key = JOGTAR_SEARCH_PREFIX + URLEncoder.encode(key, CHS_UTF8);
					Document doc = Jsoup.parse(new URL(key), 1000 * JOGTAR_TIMEOUT_SEC);
					
					f.getParentFile().mkdirs();

					FileOutputStream o = new FileOutputStream(fName);
					o.write(doc.outerHtml().getBytes(CHS_UTF8));
					o.flush();
					o.close();

					return doc;
				}
			} catch (Exception e) {
				QnDDException.wrapException("Jogtar search", e, key);
			}
			return null;
		}
	};
	DustUtilsFactory<String, Document> factLaw = new DustUtilsFactory<String, Document>(false) {
		@Override
		protected Document create(String key, Object... hints) {
			try {
				String fName = MessageFormat.format(FMT_GET_CACHE, key);
				File f = new File(fName);

				if (f.exists()) {
					return Jsoup.parse(f, CHS_UTF8);
				} else {
					key = JOGTAR_GET_PREFIX + URLEncoder.encode(key, CHS_UTF8);
					Document doc = Jsoup.parse(new URL(key), 1000 * JOGTAR_TIMEOUT_SEC);
					
					f.getParentFile().mkdirs();

					FileOutputStream o = new FileOutputStream(fName);
					o.write(doc.outerHtml().getBytes(CHS_UTF8));
					o.flush();
					o.close();

					return doc;
				}
			} catch (Exception e) {
				QnDDException.wrapException("Jogtar get", e, key);
			}
			return null;
		}
	};

	ArrayList<QnDDEntity> arrFound = new ArrayList<>();
	
	EntityTableModel mdlRes = new EntityTableModel(arrFound, DAttText.dataId, DAttText.text);

	public LMGuiPanelLaw() {
		super(new BorderLayout(DEFAULT_GAP, DEFAULT_GAP));

		JPanel pnlSearch = new JPanel(new BorderLayout(DEFAULT_GAP, DEFAULT_GAP));

		pnlSearch.add(new JLabel(QnDDUtils.formatEnum(LMGuiTexts.SearchWords)), BorderLayout.WEST);
		pnlSearch.add(tfSearch = new JTextField(), BorderLayout.CENTER);

		JButton btnSearch = new JButton(QnDDUtils.formatEnum(LMGuiTexts.Search));
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		pnlSearch.add(btnSearch, BorderLayout.EAST);

		add(pnlSearch, BorderLayout.NORTH);
		
		JTable tblRes = new JTable(mdlRes);
		
		tblRes.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent mouseEvent) {
		        Point point = mouseEvent.getPoint();
		        int row = tblRes.rowAtPoint(point);
		        if (mouseEvent.getClickCount() == 2 && (-1 != row) ) {
		            activateRow(tblRes.convertRowIndexToModel(row));
		        }
		    }
		});
		
		add(new JScrollPane(tblRes), BorderLayout.CENTER);

		doTrustToCertificates();
	}

	public void doTrustToCertificates() {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };

		SSLContext sc;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
						System.out.println("Warning: URL host '" + urlHostName + "' is different to SSLSession host '"
								+ session.getPeerHost() + "'.");
					}
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception e) {
			QnDDException.wrapException("SSL trust override", e);
		}
	}

	protected void search() {
		String src = tfSearch.getText();

		if (!DustUtilsJava.isEmpty(src)) {
			Document doc = factSearch.get(src);
			Element e = doc.getElementById("results");

			arrFound.clear();
			for (Element res : e.getElementsByClass("result")) {
				if (!res.getElementsByClass("operative").isEmpty()) {
					Element a = res.getElementsByTag(HTML.Tag.A.toString()).first();
					QnDDEntity ret = QnDDUtils.getEnv().getEntity(null, null);

					String docid = a.attr(HTML.Attribute.HREF.toString());
					docid = docid.substring(docid.indexOf("?") + 1);

					for (String str : docid.split("&")) {
						String[] ss = str.split("=");
						if ("docid".equals(ss[0])) {
							docid = ss[1];
							break;
						}
					}

					ret.setAttValue(DAttText.dataId, docid);
					ret.setAttValue(DAttText.text, a.text());

					arrFound.add(ret);
				}
			}
			
			mdlRes.updated();
		}
	}
	
	protected void activateRow(int idx) {
		QnDDEntity e = arrFound.get(idx);
		String law = e.getAttValue(DAttText.dataId);
		
		Document dLaw = factLaw.get(law);
		try {
			new Test01().process(dLaw);
		} catch (Exception ex) {
			QnDDException.wrapException("Process law", ex);
		}
	}

}
