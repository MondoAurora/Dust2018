package dust.qnd.core;

import dust.qnd.pub.QnDDComponents;
import dust.utils.DustUtilsJava;

class QnDDCoreLink implements QnDDCoreComponents, QnDDComponents.QnDDLink {

	QnDDCoreEntity eSource;
	QnDDCoreEntity eTarget;

	QnDDLinkDef link;
	Object key;

	public QnDDCoreLink(QnDDCoreEntity eSource, QnDDCoreEntity eTarget, QnDDLinkDef link, Object key) {
		this.eSource = eSource;
		this.eTarget = eTarget;
		this.link = link;
		this.key = key;
	}

	public QnDDCoreEntity getSource() {
		return eSource;
	}

	public QnDDCoreEntity getTarget() {
		return eTarget;
	}

	public QnDDLinkDef getLink() {
		return link;
	}

	@SuppressWarnings("unchecked")
	public <KeyType> KeyType  getKey() {
		return (KeyType) key;
	}

	public boolean match(QnDDEntity eSource, QnDDEntity eTarget, QnDDLinkDef link, Object key) {
		return DustUtilsJava.isEqualLenient(this.eSource, eSource)
				&& DustUtilsJava.isEqualLenient(this.eTarget, eTarget) 
				&& DustUtilsJava.isEqualLenient(this.link, link)
				&& DustUtilsJava.isEqualLenient(this.key, key);
	}

	@Override
	public QnDDLinkDef getDef() {
		// TODO Auto-generated method stub
		return null;
	}

}
