package dust.qnd.pub;

public abstract class QnDDEnvironment implements QnDDComponents {
	public abstract QnDDEntity getEntity(String type, String key);

	public abstract QnDDLink changeRef(QnDDLinkCmd cmd, QnDDLinkDef ld, QnDDEntity src, QnDDEntity target, Object key);

	protected void connect(QnDDLogic logic, QnDDEntity entity) throws Exception {
		logic.setSelf(entity);
	}
}