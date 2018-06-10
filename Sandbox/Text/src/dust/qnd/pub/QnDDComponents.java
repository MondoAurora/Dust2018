package dust.qnd.pub;

import java.util.HashMap;
import java.util.Map;

import dust.utils.DustUtilsComponents;
import dust.utils.DustUtilsJava;

public interface QnDDComponents extends DustUtilsComponents {
	interface QnDDAttDef {
	}

	interface QnDDLinkDef {
	}

	interface QnDDLink {
		QnDDLinkDef getDef();
		<KeyType> KeyType getKey();

		QnDDEntity getSource();
		QnDDEntity getTarget();

		QnDDLink getRevOpt();
	}

	interface QnDDLinkVisitor {
		void processLink(QnDDLink link);
	}

	abstract class QnDDLinkFinder implements QnDDLinkVisitor {
		public QnDDLink found = null;
	}
	
	enum QnDDLinkCmd {
		Add, Update, Remove
	}

	interface QnDDEntity {
		String getType();
		String getKey();

		<AttType> AttType getAttValue(Enum<?> key);
		<AttType> AttType setAttValue(Enum<?> key, AttType value);

		void processRefs(QnDDLinkVisitor lv);
		QnDDLink changeRef(QnDDLinkCmd cmd, QnDDLinkDef ld, QnDDEntity target, Object key);

		/*
		 * This is why quick and dirty: plain old Java function calls instead of messaging
		 */
		<Logic> Logic getLogic(Class<Logic> lc);
	}

	static final class QnDDCompUtils {
		private static final Map<QnDDLinkDef, QnDDLinkDef> REV_PAIRS = new HashMap<>();

		public static void registerRevPair(QnDDLinkDef ld1, QnDDLinkDef ld2) {
			QnDDLinkDef op;
			if (null != (op = REV_PAIRS.put(ld1, ld2))) {
				QnDDException.throwException("Already existing rev pair to", ld1, op);
			}
			if (null != (op = REV_PAIRS.put(ld2, ld1))) {
				QnDDException.throwException("Already existing rev pair to", ld2, op);
			}
		}

		public static QnDDLinkDef getRevPair(QnDDLinkDef ld) {
			return REV_PAIRS.get(ld);
		}

		public static boolean test(QnDDLink link, QnDDEntity eSource, QnDDEntity eTarget, QnDDLinkDef linkDef, Object key) {
			return DustUtilsJava.isEqualLenient(link.getSource(), eSource)
					&& DustUtilsJava.isEqualLenient(link.getTarget(), eTarget)
					&& DustUtilsJava.isEqualLenient(link.getDef(), link)
					&& DustUtilsJava.isEqualLenient(link.getKey(), key);
		}

		public static QnDDLink match(QnDDLink link, QnDDEntity eSource, QnDDEntity eTarget, QnDDLinkDef linkDef, Object key) {
			QnDDLink l;
			return test(link, eSource, eTarget, linkDef, key) ? link
					: (null == (l = link.getRevOpt())) ? null : test(l, eSource, eTarget, linkDef, key) ? l : null;
		}
	}
}
