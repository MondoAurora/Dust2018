package dust.mj02.dust.knowledge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings("unchecked")
public class DustDataContext implements DustDataComponents, DustCommComponents, DustMetaComponents, DustDataComponents.DustContext {

	class SimpleEntity implements DustEntity {
		Map<Object, Object> content = new HashMap<>();
		
		public <RetType> RetType put(Object key, Object value) {
			return (RetType) content.put(key, value);
		}

		public <RetType> RetType  get(Object key) {
			return (RetType) content.get(key);
		}
	}
	
	class SimpleRef {
		SimpleEntity linkDef;
		DustMetaValueLinkDefType lt;
		
		SimpleEntity source;
		SimpleEntity target;
		SimpleRef reverse;
		
		Object key;
		Object container;
		
		public SimpleRef(SimpleEntity linkDef, SimpleEntity source, SimpleEntity target, SimpleRef reverse, Object key,
				SimpleRef orig) {
			this.linkDef = linkDef;
			lt = linkDef.get(DustKnowledgeGen.resolve(DustMetaAtts.LinkDefType));
			if ( null == lt ) {
				lt = DustMetaValueLinkDefType.LinkDefSingle;
			}

			this.source = source;
			this.target = target;
			this.reverse = reverse;
			this.key = key;
			
			this.container = (null == orig) ? lt.createContainer() : orig.container;
			
			switch ( lt ) {
			case LinkDefArray:
				List<SimpleRef> l = (List<SimpleRef>) container;
				if ( null == key ) {
					l.add(this);
				} else {
					l.add((int) key, this);
				}
				break;
			case LinkDefSet:
				((Set<SimpleRef>) container).add(this);
				break;
			case LinkDefMap:
				((Map<Object, SimpleRef>) container).put(key, this);
				break;
			case LinkDefSingle:
				break;			
			}
			
			refs.add(this);
		}
	}

	DustContext ctxParent;

	DustUtilsFactory<Object, SimpleEntity> entities = new DustUtilsFactory<Object, SimpleEntity>(true) {
		@Override
		protected SimpleEntity create(Object key, Object... hints) {
			DustUtilsDev.dump("Creating entity", key);
			return new SimpleEntity();
		}
	};
	Set<SimpleRef> refs = new HashSet<>();

	public DustDataContext(DustContext ctxParent) {
		this.ctxParent = ctxParent;
	}

	@Override
	public SimpleEntity ctxGetEntity(Object globalId) {
		return entities.get(globalId);
	}

	@Override
	public <RetType> RetType ctxAccessEntity(DataCommand cmd, DustEntity e, Object key, Object val, Object collId) {
		SimpleEntity se = (SimpleEntity) e;
		Object retVal = se.content.get(key);
		
		SimpleEntity eLinkDef = cmd.isRef() ? ctxGetEntity(key) : null;

		switch (cmd) {
		case getValue:
			// nothing, retVal already set
			break;
		case setValue:
			retVal = se.content.put(key, val);
			break;
		case removeRef:
			break;
		case setRef:
			SimpleRef actRef = (SimpleRef) retVal;
			
			if ( (null != actRef) && (DustMetaValueLinkDefType.LinkDefSet == actRef.lt) ) {
				for ( SimpleRef er : ((Set<SimpleRef>)actRef.container) ) {
					if ( er.target == val ) {
						return null;
					}
				}
			}
			SimpleRef sr = new SimpleRef(eLinkDef, se, (SimpleEntity) val, null, collId, actRef);
			
			if ( null == actRef ) {
				se.put(key, sr);
			}
			
			break;
		case clearRefs:
			
			break;
		}
		return (RetType) retVal;
	}
	
	@Override
	public void ctxProcessEntities(EntityProcessor proc) {
		for ( Object key : entities.keys()) {
			proc.processEntity(key, entities.peek(key));
		}
	}

	@Override
	public void ctxProcessRefs(RefProcessor proc, DustEntity source, Object linkDefId, DustEntity target) {
		SimpleEntity eLD = (null == linkDefId) ? null : ctxGetEntity(linkDefId);
		for ( SimpleRef ref : refs ) {
			if ( DustUtilsJava.isEqualLenient(ref.source, source) 
					&& DustUtilsJava.isEqualLenient(ref.linkDef, eLD) 
					&& DustUtilsJava.isEqualLenient(ref.target, target) ) {
				proc.processRef(ref.source, ref.linkDef, ref.target, ref.key);
			}
		}
	}
}
