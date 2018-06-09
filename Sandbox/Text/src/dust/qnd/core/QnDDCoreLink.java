package dust.qnd.core;

import dust.qnd.pub.QnDDComponents;

@SuppressWarnings("unchecked")
class QnDDCoreLink implements QnDDCoreComponents, QnDDComponents.QnDDLink {

	class RevReader implements QnDDLink {
		QnDDLinkDef linkRev;
		
		public RevReader(QnDDLinkDef linkRev) {
			this.linkRev = linkRev;
		}

		@Override
		public QnDDLinkDef getDef() {
			return linkRev;
		}

		@Override
		public <KeyType> KeyType getKey() {
			return (KeyType) keyRev;
		}

		@Override
		public QnDDEntity getSource() {
			return eTarget;
		}

		@Override
		public QnDDEntity getTarget() {
			return eSource;
		}
		
		@Override
		public QnDDLink getRevOpt() {
			return QnDDCoreLink.this;
		}
	}

	QnDDLinkDef link;

	QnDDCoreEntity eSource;
	QnDDCoreEntity eTarget;

	Object key;
	Object keyRev;
	
	RevReader revReader;

	public QnDDCoreLink(QnDDLinkDef link, QnDDCoreEntity eSource, QnDDCoreEntity eTarget, Object key) {
		this.eSource = eSource;
		this.eTarget = eTarget;
		this.link = link;
		this.key = key;
		
		QnDDLinkDef linkRev;
		if ( null != (linkRev = QnDDCompUtils.getRevPair(link)) ) {
			revReader = new RevReader(linkRev);
		}
	}

	@Override
	public QnDDLinkDef getDef() {
		return link;
	}

	@Override
	public QnDDCoreEntity getSource() {
		return eSource;
	}

	@Override
	public QnDDCoreEntity getTarget() {
		return eTarget;
	}

	@Override
	public <KeyType> KeyType  getKey() {
		return (KeyType) key;
	}
	
	@Override
	public QnDDLink getRevOpt() {
		return revReader;
	}
}
