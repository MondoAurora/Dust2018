package dust.qnd.pub;

public interface QnDDComponents {
	interface QnDDAttDef {
	}
	
	interface QnDDLinkDef {
	}

	interface QnDDLink {
		QnDDLinkDef getDef(); 
		<KeyType> KeyType getKey();
		
		QnDDEntity getSource();
		QnDDEntity getTarget();
		
		boolean match(QnDDEntity eSource, QnDDEntity eTarget, QnDDLinkDef link, Object key);
	}

	interface QnDDLinkVisitor {
		void processLink(QnDDLink link);
	}
	
	abstract class QnDDLinkFinder implements QnDDLinkVisitor {
		public QnDDLink found = null;
	}

	interface QnDDEntity {
		String getType(); 
		String getKey();

		<AttType> AttType getAttValue(Enum<?> key);
		<AttType> AttType setAttValue(Enum<?> key, AttType value);
		
		void processRefs(QnDDLinkVisitor lv);
		
		<Logic> Logic getLogic(Class<Logic> lc);
	}
	
}
