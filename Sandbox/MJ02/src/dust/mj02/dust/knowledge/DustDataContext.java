package dust.mj02.dust.knowledge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings("unchecked")
public class DustDataContext implements DustDataComponents, DustCommComponents, DustMetaComponents,
		DustGenericComponents, DustDataComponents.DustContext {

	// DustUtilsFactory<Object, SimpleEntity> FACT_INFO = new
	// DustUtilsFactory<Object, DustDataContext.SimpleEntity>(
	// false) {
	// @Override
	// protected SimpleEntity create(Object key, Object... hints) {
	// Object storeId = DustToolsGen.resolve(key);
	// if (null == storeId) {
	// storeId = DustKnowledgeGen.resolve(key);
	// }
	// return ctxGetEntity(storeId);
	// }
	// };

	class SimpleEntity implements DustEntity {
		Map<DustEntity, Object> content = new HashMap<>();

		public <RetType> RetType put(DustEntity key, Object value) {
			RetType orig = (RetType) content.put(key, value);
			
			if ( EntityResolver.getEntity(DustDataLinks.EntityPrimaryType) == key ) {
				ctxAccessEntity(DataCommand.setRef, this, EntityResolver.getEntity(DustDataLinks.EntityModels),
						((SimpleRef) value).target, null);
			}

			if (null == orig) {
				SimpleRef pr = ((SimpleEntity) key).get(EntityResolver.getEntity(DustGenericLinks.Owner));
				if (null != pr) {
					ctxAccessEntity(DataCommand.setRef, this, EntityResolver.getEntity(DustDataLinks.EntityModels),
							pr.target, null);
				}
			}

			return orig;
		}

		public <RetType> RetType get(DustEntity key) {
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
			this.source = source;
			this.target = target;
			this.reverse = reverse;
			this.key = key;

			SimpleRef refLDT = linkDef.get(EntityResolver.getEntity(DustMetaAtts.LinkDefType));
			if (null == refLDT) {
				lt = DustMetaValueLinkDefType.LinkDefSingle;
				this.container = null;
			} else {
				lt = EntityResolver.getKey(refLDT.target);

				if (null == orig) {
					this.container = lt.createContainer();
				} else {
					if (null == orig.container) {
						orig.container = lt.createContainer();
					}
					this.container = orig.container;
				}
			}

			switch (lt) {
			case LinkDefArray:
				List<SimpleRef> l = (List<SimpleRef>) container;
				if (null == key) {
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

	DustUtilsFactory<Object, SimpleEntity> entities = new DustUtilsFactory<Object, SimpleEntity>(false) {
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
	public <RetType> RetType ctxAccessEntity(DataCommand cmd, DustEntity e, DustEntity key, Object val, Object collId) {
		SimpleEntity se = (SimpleEntity) e;
		Object retVal = se.get(key);

		switch (cmd) {
		case getValue:
			// nothing, retVal already set
			break;
		case setValue:
			retVal = se.put(key, val);
			break;
		case removeRef:
			break;
		case setRef:
			SimpleRef actRef = (SimpleRef) retVal;

			if ((null != actRef) && (DustMetaValueLinkDefType.LinkDefSet == actRef.lt)) {
				for (SimpleRef er : ((Set<SimpleRef>) actRef.container)) {
					if (er.target == val) {
						return null;
					}
				}
			}
			SimpleRef sr = new SimpleRef((SimpleEntity) key, se, (SimpleEntity) val, null, collId, actRef);

			if (null == actRef) {
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
		for (Object key : entities.keys()) {
			proc.processEntity(key, entities.peek(key));
		}
	}

	@Override
	public void ctxProcessRefs(RefProcessor proc, DustEntity source, DustEntity linkDefId, DustEntity target) {
		SimpleEntity eLD = (null == linkDefId) ? null : ctxGetEntity(linkDefId);
		for (SimpleRef ref : refs) {
			if (DustUtilsJava.isEqualLenient(ref.source, source) && DustUtilsJava.isEqualLenient(ref.linkDef, eLD)
					&& DustUtilsJava.isEqualLenient(ref.target, target)) {
				proc.processRef(ref.source, ref.linkDef, ref.target, ref.key);
			}
		}
	}
}
