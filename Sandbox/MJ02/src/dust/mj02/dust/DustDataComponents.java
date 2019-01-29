package dust.mj02.dust;

public interface DustDataComponents extends DustComponents {
	public interface DustDataEntity {};
	
	interface RefProcessor {
		 void processRef(DustDataEntity source, DustDataEntity linkDef, DustDataEntity target, Object key);
	}
	
	interface EntityProcessor {
		 void processEntity(Object key, DustDataEntity entity);
	}
	
	public interface DustDataContext {
		DustDataEntity getEntity(Object globalId);
		<RetType> RetType accessEntity(DataCommand cmd, DustDataEntity e, Object key, Object val, Object collId);
		void processRefs(RefProcessor proc, DustDataEntity source, Object linkDef, DustDataEntity target);
		void processEntities(EntityProcessor proc);
	};

	enum DataCommand {
		getValue(false), setValue(false), setRef(true), removeRef(true), clearRefs(true);
		
		private final boolean ref;
		
		
		
		private DataCommand(boolean ref) {
			this.ref = ref;
		}



		public boolean isRef() {
			return ref;
		}
	}
}
