package dust.qnd.pub;

public abstract class QnDDLogic implements QnDDComponents, QnDDTypes, QnDDServices {

	private QnDDEntity self;
	
	void setSelf(QnDDEntity s) throws Exception {
		release(self);
		this.self = s;
		init(self);
	}
	
	protected void init(QnDDEntity self) throws Exception {
		
	}
	protected void release(QnDDEntity self) throws Exception {
		
	}
	
	protected <AttType> AttType getAttValue(Enum<?> key) {
		return self.getAttValue(key);
	}
	protected <AttType> AttType setAttValue(Enum<?> key, AttType value) {
		return self.setAttValue(key, value);
	}

	protected QnDDEntity getRef(QnDDLinkDef link, Object key) {
		QnDDLinkFinder lf  = new QnDDLinkFinder() {
			@Override
			public void processLink(QnDDLink l) {
				QnDDLink ll;
				if ( null != ( ll = QnDDCompUtils.match(l, self, null, link, key))) {
					this.found = ll;
				}
			}
		};
		self.processRefs(lf);
		return (null == lf.found) ? null : lf.found.getTarget();
	}

	protected QnDDLink chgRef(QnDDLinkCmd cmd, QnDDLinkDef ld, QnDDEntity target, Object key) {
		return self.changeRef(cmd, ld, target, key);
	}

}
