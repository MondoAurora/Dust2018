package dust.mj02.dust;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DustDataContext2 implements DustDataComponents {

	@SuppressWarnings("unchecked")
	class DustDataNode {
		DustDataContext2 ctx;
		Map<Object, Object> content = new HashMap<>();

		public DustDataNode() {
			this(null);
		}

		public DustDataNode(DustDataContext2 ctx) {
			this.ctx = ctx;
		}

		public <RetType> RetType get(Object key) {
			Object r = content.get(key);
			return (RetType) ((r instanceof DustDataRef) ? ((DustDataRef)r).target : r);
		}

		public <RetType> RetType put(Object key, Object val) {
			Object orig;
			DustDataRef ref;
			
			if ( null == val ) {
				orig = content.remove(key);
			} else if ( val instanceof DustDataNode ) {
				orig = content.get(key);
				if (orig instanceof DustDataRef) {
					ref = (DustDataRef) orig;
					if ( null != ref.reverse ) {
						
					}
					ref.target = (DustDataNode) val;
					orig = null;
				} else {
					ref = new DustDataRef((DustDataNode) key, this, (DustDataNode) val);
					refs.add(ref);
					content.put(key, ref);
				}
			} else {
				orig = content.put(key, val);
			}
			
			if (orig instanceof DustDataRef) {
				ref = (DustDataRef) orig;
				refs.remove(ref);
				if ( null != ref.reverse ) {
					refs.remove(ref.reverse);
				}
				orig = ref.target;
			}
			
			return (RetType) orig;
		}
	}

	class DustDataRef {
		DustDataNode linkDef;
		DustDataNode source;
		DustDataNode target;
		DustDataRef reverse;
		
		public DustDataRef(DustDataNode linkDef, DustDataNode source, DustDataNode target) {
			super();
			this.linkDef = linkDef;
			this.source = source;
			this.target = target;
		}
	}
	
	enum GlobalEntities implements DustId {
		AttEntityPrimaryType,
		TypeType, TypeAttribute, TypeEntity
	}
	
	static EnumMap<GlobalEntities, DustDataNode> GLOBAL_ENTITIES = new EnumMap<>(GlobalEntities.class);

	DustDataContext2 ctxParent;

	Map<DustId, DustDataNode> entities = new HashMap<>();
	Set<DustDataRef> refs = new HashSet<>();

	public DustDataContext2(DustDataContext2 ctxParent) {
	}

	private void initRoot() {
		GLOBAL_ENTITIES.put(GlobalEntities.AttEntityPrimaryType, new DustDataNode());
		GLOBAL_ENTITIES.put(GlobalEntities.TypeType, new DustDataNode());
		
		DustDataNode eTypeType = new DustDataNode();
		DustDataNode eTypeEntity = new DustDataNode();
		DustDataNode eTypeAttribute = new DustDataNode();
		
		DustDataNode eAttEntityPrimaryType = new DustDataNode();
		
		eTypeType.put(eAttEntityPrimaryType, eTypeType);

	}
}
