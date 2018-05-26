package dust.qnd.pub;

public interface QnDDComponents {

	interface QnDDEntity {
		String getType(); 
		String getKey();

		<AttType> AttType getAttValue(Enum<?> key);
		<AttType> AttType setAttValue(Enum<?> key, AttType value);
		
		<Logic extends QnDDLogic> Logic getLogic(Class<Logic> lc);
	}
	
	abstract class QnDDEnvironment {
		public abstract QnDDEntity getEntity(String type, String key);
		
		protected void connect(QnDDLogic logic, QnDDEntity entity) throws Exception {
			logic.setSelf(entity);
		}
	}
	
	abstract class QnDDLogic {
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
	}
}
