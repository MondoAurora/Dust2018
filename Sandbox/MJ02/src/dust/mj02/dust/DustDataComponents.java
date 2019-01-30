package dust.mj02.dust;

public interface DustDataComponents extends DustComponents {
	public interface DustEntity {};
	
	interface RefProcessor {
		 void processRef(DustEntity source, DustEntity linkDef, DustEntity target, Object key);
	}
	
	interface EntityProcessor {
		 void processEntity(Object key, DustEntity entity);
	}
	
	public interface DustContext {
		DustEntity ctxGetEntity(Object globalId);
		<RetType> RetType ctxAccessEntity(DataCommand cmd, DustEntity e, Object key, Object val, Object collId);
		void processRefs(RefProcessor proc, DustEntity source, Object linkDef, DustEntity target);
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
