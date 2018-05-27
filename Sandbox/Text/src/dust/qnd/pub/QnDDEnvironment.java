package dust.qnd.pub;

import dust.qnd.pub.QnDDComponents.QnDDEntity;

public abstract class QnDDEnvironment {
	public abstract QnDDEntity getEntity(String type, String key);
	
	protected void connect(QnDDLogic logic, QnDDEntity entity) throws Exception {
		logic.setSelf(entity);
	}
}