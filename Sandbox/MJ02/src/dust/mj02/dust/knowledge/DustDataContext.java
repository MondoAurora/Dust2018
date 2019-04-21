package dust.mj02.dust.knowledge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dust.mj02.dust.Dust;
import dust.mj02.dust.Dust.DustContext;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings("unchecked")
public class DustDataContext implements DustDataComponents, DustCommComponents, DustMetaComponents, DustProcComponents,
		DustGenericComponents, Dust.DustContext {

	class SimpleEntity implements DustEntity {
		Map<DustEntity, Object> content = new HashMap<>();
		DustEntity ePT;
		DustUtilsFactory<SimpleEntity, Method> factMethods;
		boolean justCreated = true;
		boolean internal;
		
		public SimpleEntity(boolean internal) {
			this.internal = internal;
			
			if ( !internal ) {
				allEntities.add(this);
			}
		}

		public <RetType> RetType put(DustEntity key, Object value) {
			RetType orig = (RetType) content.put(key, value);

			if (!internal) {
				if (EntityResolver.getEntity(DustDataLinks.EntityPrimaryType) == key) {
					ePT = ((SimpleRef) value).target;
					ctxAccessEntity(DataCommand.setRef, this, EntityResolver.getEntity(DustDataLinks.EntityModels), ePT,
							null);
				}

				if (null == orig) {
					SimpleEntity keyModel = ((SimpleEntity)key).getFirstRef(DustMetaLinks.LinkDefParent, DustMetaLinks.AttDefParent);//, DustGenericLinks.ConnectedOwner);
					if ( null != keyModel ) {
						ctxAccessEntity(DataCommand.setRef, this, EntityResolver.getEntity(DustDataLinks.EntityModels),
								keyModel, null);						
					}
				}
			}

			return orig;
		}
		
		public <RetType> RetType get(DustEntity key) {
			return (RetType) content.get(key);
		}

		public <RetType> RetType get(DustEntityKey key) {
			return (RetType) content.get(EntityResolver.getEntity(key));
		}

		public SimpleEntity getSingleRef(DustEntityKey key) {
			SimpleRef r = get(EntityResolver.getEntity(key));
			return (null == r) ? null : r.target;
		}

		public SimpleEntity getFirstRef(DustEntityKey... keys) {
			SimpleEntity ret = null;
			for ( DustEntityKey k : keys ) {
				ret = getSingleRef(k);
				if ( null != ret ) {
					break;
				}
			}
			return ret;
		}

		public <RetType> RetType put(DustEntityKey key, Object value) {
			return (RetType) put(EntityResolver.getEntity(key), value);
		}

		@Override
		public String toString() {
			String id = get(EntityResolver.getEntity(DustGenericAtts.IdentifiedIdLocal));

			String type = (null == ePT) ? "?"
					: (ePT == this) ? id
							: ((SimpleEntity) ePT).get(EntityResolver.getEntity(DustGenericAtts.IdentifiedIdLocal));
			return type + ": " + id;
		}

		public void putLocalRef(DustEntityKey link, DustEntityKey target) {
			put(link, new SimpleRef(link, this, (SimpleEntity) EntityResolver.getEntity(target)));
		}

		public void putLocalRef(DustEntityKey link, SimpleEntity target) {
			put(link, new SimpleRef(link, this, target));
		}
	}

	class SimpleRef implements DustRef {
		final SimpleEntity linkDef;
		final SimpleEntity source;
		final SimpleEntity target;
		final Object key;

		SimpleRef reverse;

		DustMetaLinkDefTypeValues lt;
		Object container;

		public SimpleRef(SimpleEntity linkDef, SimpleEntity source, SimpleEntity target, Object key,
				SimpleRef orig) {
			this.linkDef = linkDef;
			this.source = source;
			this.target = target;
			this.key = key;

			initContainer(orig);

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
				SimpleRef old = ((Map<Object, SimpleRef>) container).put(key, this);
				if (null != old) {
					refs.remove(old);
				}
				break;
			case LinkDefSingle:
				break;
			}
		}

		private SimpleRef(DustEntityKey linkDef, SimpleEntity source, SimpleEntity target) {
			this.linkDef = (SimpleEntity) EntityResolver.getEntity(linkDef);
			this.source = source;
			this.target = target;
			this.reverse = null;
			this.key = null;
		}

		void initContainer(SimpleRef orig) {
			Object o = linkDef.get(EntityResolver.getEntity(DustMetaLinks.LinkDefType));
			SimpleRef refLDT;
			if (o instanceof SimpleRef) {
				refLDT = (SimpleRef) o;
			} else {
				refLDT = null;
			}
			lt = (null == refLDT) ? DustMetaLinkDefTypeValues.LinkDefSingle : EntityResolver.getKey(refLDT.target);

			if ((null == orig) || (null == orig.container)) {
				switch (lt) {
				case LinkDefArray:
					container = new ArrayList<SimpleRef>();
					break;
				case LinkDefSet:
					container = new HashSet<SimpleRef>();
					break;
				case LinkDefMap:
					container = new HashMap<Object, SimpleRef>();
					break;
				case LinkDefSingle:
					container = null;
					return;
				}

				if (null != orig) {
					if (DustMetaLinkDefTypeValues.LinkDefMap == lt) {
						((HashMap<Object, SimpleRef>) container).put(orig.key, orig);
					} else {
						if (orig.target == target) {
							refs.remove(orig);
						} else {
							((Collection<SimpleRef>) container).add(orig);
						}
					}
					orig.container = container;
					orig.lt = lt;
				}
			} else {
				container = orig.container;
			}
		}

		@Override
		public boolean contains(DustEntity entity) {
			switch (lt) {
			case LinkDefArray:
			case LinkDefSet:
				for (Object r : (Collection<?>) container) {
					if(((SimpleRef) r).target == entity) {
						return true;
					};
				}
				break;
			case LinkDefMap:
				for (Object r : ((Map<Object, SimpleRef>) container).values()) {
					if(((SimpleRef) r).target == entity) {
						return true;
					};
				}
				break;
			case LinkDefSingle:
				return target == entity;
			}
			
			return false;
		}

		@Override
		public void processAll(RefProcessor proc) {
			switch (lt) {
			case LinkDefArray:
			case LinkDefSet:
				for (Object r : ((Collection<?>) container).toArray()) {
					proc.processRef((SimpleRef) r);
				}
				break;
			case LinkDefMap:
				for (Object r : ((Map<Object, SimpleRef>) container).values().toArray()) {
					proc.processRef((SimpleRef) r);
				}
				break;
			case LinkDefSingle:
				proc.processRef(this);
				return;
			}
		}

		public SimpleRef select(DustEntity target) {
			switch (lt) {
			case LinkDefArray:
			case LinkDefSet:
				for (Object r : ((Collection<?>) container).toArray()) {
					SimpleRef ref = (SimpleRef) r;
					if (ref.target == target) {
						return ref;
					};
				}
				return null;
			case LinkDefMap:
				for (Object r : ((Map<Object, SimpleRef>) container).values().toArray()) {
					SimpleRef ref = (SimpleRef) r;
					if (ref.target == target) {
						return ref;
					};
				}
				return null;
			case LinkDefSingle:
				return this;
			}
			
			return null;
		}

		boolean removeByTarget(SimpleEntity target) {
			SimpleRef toDel = select(target);
			
			if ( null != toDel ) {
				toDel.remove(false, true);
				return true;
			}
			
			return false;
		}

		void remove(boolean all, boolean handleReverse) {
			boolean clear = true;
			
			switch (lt) {
			case LinkDefArray:
			case LinkDefSet:
				Collection<SimpleRef> coll = (Collection<SimpleRef>) container;
				if (all) {
					for (Object r : coll.toArray()) {
						((SimpleRef)r).remove(false, handleReverse);
					}
				} else {
					coll.remove(this);
					clear = coll.isEmpty();
					if (!clear && (this == source.get(linkDef))) {
						source.put(linkDef, coll.iterator().next());
					}
				}
				break;
			case LinkDefMap:
				Map<Object, SimpleRef> map = (Map<Object, SimpleRef>) container;
				if (all) {
					for (SimpleRef r : map.values()) {
						r.remove(false, handleReverse);
					}
				} else {
					map.remove(key);
					clear = map.isEmpty();
					if (!map.isEmpty() && (this == source.get(linkDef))) {
						source.put(linkDef, map.entrySet().iterator().next());
					}
				}
				break;
			case LinkDefSingle:
				break;
			}

			if (clear) {
				source.content.remove(linkDef);
//				source.put(linkDef, null);
			}

			refs.remove(this);
			notifyListeners(DataCommand.removeRef, source, linkDef, null, this);

			if (handleReverse && (null != reverse)) {
				reverse.remove(false, false);
			}
		}

		@Override
		public <InfoType> InfoType get(RefKey ref) {
			switch (ref) {
			case key:
				return (InfoType) key;
			case linkDef:
				return (InfoType) linkDef;
			case source:
				return (InfoType) source;
			case target:
				return (InfoType) target;
			}
			return null;
		}

		@Override
		public String toString() {
			StringBuilder sb = null;

			switch (lt) {
			case LinkDefSingle:
				sb = new StringBuilder(DustUtilsJava.toString(target));
				break;
			case LinkDefArray:
			case LinkDefSet:
				if (null != container) {
					for (SimpleRef sr : (Iterable<SimpleRef>) container) {
						sb = DustUtilsJava.sbAppend(sb, ", ", false, sr.target);
					}
				}
				break;
			case LinkDefMap:
				if (null != container) {
					for (Entry<Object, SimpleRef> e : ((Map<Object, SimpleRef>) container).entrySet()) {
						sb = DustUtilsJava.sbAppend(sb, ", ", false, e.getKey() + "=" + e.getValue().target);
					}
				}
				break;
			}

			return sb.insert(0, lt.sepStart).append(lt.sepEnd).toString();
		}
	}

	DustContext ctxParent;

	DustUtilsFactory<Object, SimpleEntity> entities = new DustUtilsFactory<Object, SimpleEntity>(false) {
		@Override
		protected SimpleEntity create(Object key, Object... hints) {
			SimpleEntity se = new SimpleEntity(false);
			return se;
		}
	};
	Set<SimpleEntity> allEntities = new HashSet<>();
	Set<SimpleRef> refs = new HashSet<>();

	EnumMap<ContextRef, SimpleEntity> mapCtxEntities = new EnumMap<>(ContextRef.class);

	DustBinaryConnector binConn = new DustBinaryConnector(this);
	SimpleEntity ctxSelf = new SimpleEntity(true);

	public DustDataContext(DustContext ctxParent) {
		this.ctxParent = ctxParent;
		mapCtxEntities.put(ContextRef.ctx, ctxSelf);
	}

	private SimpleEntity optResolveCtxEntity(Object e) {
		if (null == e) {
			return null;
		} else if (e instanceof SimpleEntity) {
			return (SimpleEntity) e;
		} else if (e instanceof DustEntityKey) {
			return (SimpleEntity) EntityResolver.getEntity(e);
		} else {
			ContextRef cr = (ContextRef) e;
			SimpleEntity se = mapCtxEntities.get(cr);
			return se;
		}

	}

	@Override
	public SimpleEntity ctxGetEntity(Object globalId) {
		return (null == globalId) ? new SimpleEntity(false) : entities.get(globalId);
	}

	@Override
	public <RetType> RetType ctxAccessEntity(DataCommand cmd, DustEntity e, DustEntity key, Object val, Object hint) {
		SimpleEntity se = optResolveCtxEntity(e);

		Object retVal = (null == se) ? null : (null == key) ? se : se.get(key);

		switch (cmd) {
		case getEntity:
			retVal = invokeEntity(se, optResolveCtxEntity(key), val, (EntityProcessor) hint);
			break;
		case cloneEntity:
			retVal = cloneEntity(se);
			break;
		case dropEntity:
			if ( val instanceof Collection<?> ) {
				for ( SimpleEntity sde : ((Collection<SimpleEntity>)val) ) {
					dropEntity(sde);
				}
			} else {
				dropEntity(se);
			}
			break;
		case tempSend:
			binConn.send(se, (SimpleEntity) key);
			break;
		case getValue:
			// nothing, retVal already set
			break;
		case setValue:
			retVal = se.put(key, val);

			if (!DustUtilsJava.isEqual(retVal, val)) {
				notifyListeners(cmd, se, key, val, retVal);
			}
			break;
		case processContent:
			ContentProcessor cp = (ContentProcessor) val;
			for ( Map.Entry<DustEntity, Object> ee : se.content.entrySet() ) {
				cp.processContent(se, ee.getKey(), ee.getValue());
			}
			break;
		case processRef:
			if (null != retVal) {
				((SimpleRef) retVal).processAll((RefProcessor) val);
			}
			break;
		default:
			retVal = changeRef(true, cmd, se, key, (SimpleRef) retVal, optResolveCtxEntity(val), hint);
			break;
		}
		return (RetType) retVal;
	}

	private SimpleEntity invokeEntity(SimpleEntity type, SimpleEntity owner, Object id, EntityProcessor initializer) {
		String gid = (String) id;
		SimpleEntity ce = ctxGetEntity(gid);

		if (ce.justCreated) {
//			if ( null != gid ) {
//				DustUtils.accessEntity(DataCommand.setValue, ce, DustCommAtts.PersistentEntityId, gid);
//			}
			if (null != type) {
				ctxAccessEntity(DataCommand.setRef, ce, EntityResolver.getEntity(DustDataLinks.EntityPrimaryType), type,
						null);
			}
			if (null != owner) {
				ctxAccessEntity(DataCommand.setRef, ce, EntityResolver.getEntity(DustGenericLinks.ConnectedOwner), owner, null);
			}
			
			if (null != initializer) {
				initializer.processEntity(ce);
			}
			
			if (null != type) {
				SimpleRef r = type.get(DustMetaLinks.TypeLinkedServices);
				if ( null != r) {
					ctxAccessEntity(DataCommand.setRef, ce, EntityResolver.getEntity(DustDataLinks.EntityServices), r.target, null);
				}
			}
			
			ce.justCreated = false;
			notifyListeners(DataCommand.getEntity, ce, null, id, null);
		}
		return ce;
	}

	private SimpleEntity cloneEntity(SimpleEntity source) {
		SimpleRef refPt = source.get(DustDataLinks.EntityPrimaryType);
		SimpleEntity ret = invokeEntity((null == refPt) ? null : refPt.get(RefKey.target), null, null, null);
		
		for ( Map.Entry<DustEntity, Object> se : source.content.entrySet() ) {
			DustEntity key = se.getKey();
			if ( DustUtils.tag(key, TagCommand.test, DustMetaTags.NotCloned)) {
//			if ( EntityResolver.getEntity(DustDataLinks.EntityModels) == key ) {
				continue;
			}
			Object val = se.getValue();
			
			if ( val instanceof SimpleRef ) {
				SimpleRef rr = (SimpleRef) val;
				
				if ( (null != rr.reverse ) && (rr.reverse.lt == DustMetaLinkDefTypeValues.LinkDefSingle)) {
					DustUtilsDev.dump("In clone, skipping", key, "because the reverse link is single.");
					continue;
				}
				rr.processAll(new RefProcessor() {
					SimpleRef lastRef = null;
					
					@Override
					public void processRef(DustRef ref) {
						lastRef = changeRef(true, DataCommand.setRef, ret, key, lastRef, rr.target, rr.key);
					}
				});
			} else {
				ret.put(key, val);
			}
		}
		
		return ret;
	}

	private void dropEntity(SimpleEntity entity) {
		entities.drop(entity);
		allEntities.remove(entity);
		notifyListeners(DataCommand.dropEntity, entity, null, null, entity);
		
		Set<SimpleRef> toDel = new HashSet<>();
		for ( SimpleRef sr : refs ) {
			if ( (entity == sr.target) || (entity == sr.source) ) {
				toDel.add(sr);
			}
		}
		for ( SimpleRef sr : toDel ) {
			if ( refs.contains(sr) ) {
				sr.remove((entity == sr.source), true);
			}
		}
	}

	public SimpleRef changeRef(boolean handleReverse, DataCommand cmd, SimpleEntity se, DustEntity key,
			SimpleRef actRef, Object val, Object collId) {
		SimpleRef sr = null;

		switch (cmd) {
		case removeRef:
			if (null != actRef) {
				Collection<SimpleEntity> mdls = null;

				if ( val instanceof Collection<?>) {
					mdls = (Collection<SimpleEntity>) val;
					for ( SimpleEntity sde : mdls ) {
						actRef.removeByTarget(sde);
					}
				} else {
					actRef.removeByTarget((SimpleEntity) val);
				}
				
				if (EntityResolver.getEntity(DustDataLinks.EntityModels) == key) {
					if (null == mdls) {
						mdls = new HashSet<>();
						mdls.add((SimpleEntity) val);
					}
					
					Map<SimpleEntity, Object> toDel = new HashMap<>();
					
					for (Map.Entry<DustEntity, Object> ee : se.content.entrySet()) {
						SimpleEntity eKey = (SimpleEntity) ee.getKey();
						Object eval = ee.getValue();

						boolean isRef = (eval instanceof SimpleRef);
						SimpleEntity pM = eKey.getSingleRef(
								isRef ? DustMetaLinks.LinkDefParent : DustMetaLinks.AttDefParent);

						if (mdls.contains(pM)) {
							toDel.put(eKey, eval);
						}
					}
					for (Map.Entry<SimpleEntity, Object> ee : toDel.entrySet()) {
						SimpleEntity eKey = ee.getKey();
						Object eval = se.content.get(eKey);

						boolean isRef = (eval instanceof SimpleRef);
						if (isRef) {
							((SimpleRef) eval).remove(true, true);
						} else {
							notifyListeners(DataCommand.setValue, se, eKey, null, eval);
							se.content.remove(eKey);
						}
					}
				}
//				SimpleRef toDel = actRef.select((DustEntity) val);
//				if ( null != toDel ) {
//					toDel.remove(false, true);
//				}
			}
			break;
		case setRef:
			SimpleEntity eTarget = optResolveCtxEntity(val);

			if ((null != actRef) && (DustMetaLinkDefTypeValues.LinkDefSet == actRef.lt)) {
				for (SimpleRef er : ((Set<SimpleRef>) actRef.container)) {
					if (er.target == eTarget) {
						return er;
					}
				}
			}
			sr = new SimpleRef((SimpleEntity) key, se, (SimpleEntity) eTarget, collId, actRef);

			if ((null != actRef) && (DustMetaLinkDefTypeValues.LinkDefSingle == sr.lt)) {
				if (DustUtilsJava.isEqual(eTarget, actRef.target)) {
					return actRef;
				}

				actRef.remove(false, true);
				se.put(key, sr);
				notifyListeners(cmd, se, key, sr, actRef);
			} else {
				if (null == actRef) {
					se.put(key, sr);
				}
				notifyListeners(cmd, se, key, sr, actRef);
			}

			refs.add(sr);
			
			if ( EntityResolver.getEntity(DustDataLinks.EntityModels) == key ) {
				SimpleRef r = eTarget.get(DustGenericLinks.ConnectedRequires);
				if ( null != r ) {
					r.processAll(new RefProcessor() {
						@Override
						public void processRef(DustRef ref) {
							ctxAccessEntity(DataCommand.setRef, se, key, ref.get(RefKey.target), null);
						}
					});
				}
			}


			if (handleReverse) {
				SimpleRef rr = sr.linkDef.get(DustMetaLinks.LinkDefReverse);
				if (null != rr) {
					SimpleEntity revLink = rr.target;
					SimpleRef rev = changeRef(false, cmd, eTarget, revLink, eTarget.get(revLink), se, collId);

					sr.reverse = rev;
					rev.reverse = sr;
				}
			}

			break;
		case clearRefs:
			if (null != actRef) {
				actRef.remove(true, true);
			}

			break;
		default:
			throw new DustException("Should not get here!");
		}

		return sr;
	}

	private void notifyListeners(DataCommand cmd, SimpleEntity entity, DustEntity key, 
			Object newVal, Object oldVal) {
		SimpleRef listeners = ctxSelf.get(DustProcLinks.ContextChangeListeners);

		if ((DataCommand.setRef == cmd) && (null != newVal)
				&& (key == EntityResolver.getEntity(DustDataLinks.EntityServices))) {
			SimpleEntity svc = ((SimpleRef) newVal).target;
			binConn.instSvc(entity, svc);
			if (DustUtils.isTrue(svc, DustProcAtts.BinaryAutoInit)) {
				SimpleEntity init = new SimpleEntity(true);

				init.putLocalRef(DustDataLinks.MessageCommand, DustProcMessages.ActiveInit);

				binConn.send(entity, init);
			}
		}

		if (null != listeners) {
			listeners.processAll(new RefProcessor() {
				SimpleEntity chg = null;

				@Override
				public void processRef(DustRef ref) {
					SimpleEntity listener = ((SimpleRef) ref).target;

					if (DustUtilsJava.isEqualLenient(cmd, listener.getSingleRef(DustProcLinks.ChangeCmd))
							&& DustUtilsJava.isEqualLenient(entity, listener.getSingleRef(DustProcLinks.ChangeEntity))
							&& DustUtilsJava.isEqualLenient(key, listener.getSingleRef(DustProcLinks.ChangeKey))) {

						if (null == chg) {
							chg = new SimpleEntity(true);

							chg.putLocalRef(DustDataLinks.MessageCommand, DustProcMessages.ListenerProcessChange);

							chg.putLocalRef(DustProcLinks.ChangeCmd, cmd);
							chg.putLocalRef(DustProcLinks.ChangeEntity, entity);
							chg.putLocalRef(DustProcLinks.ChangeKey, (SimpleEntity) key);

							chg.put(DustProcAtts.ChangeOldValue, oldVal);
							chg.put(DustProcAtts.ChangeNewValue, newVal);
						}

						binConn.send(listener, chg);
					}
				}
			});
		}
		
		DustEntity eEchg = EntityResolver.getEntity(DustDataTags.EntityChanged);
		boolean ect = ((oldVal instanceof DustRef) && (eEchg == ((DustRef)oldVal).get(RefKey.target)))
				|| ((newVal instanceof DustRef) && (eEchg == ((DustRef)newVal).get(RefKey.target)));
		
		if ( !ect ) {
			DustUtils.tag(entity, TagCommand.set, DustDataTags.EntityChanged);
		}
	}

	@Override
	public void ctxProcessEntities(EntityProcessor proc) {
		for (Object e : allEntities.toArray() ) {
			proc.processEntity((DustEntity) e);
		}
	}

	@Override
	public void ctxProcessRefs(RefProcessor proc, DustEntity source, DustEntity linkDef, DustEntity target) {
		source = optResolveCtxEntity(source);
		target = optResolveCtxEntity(target);

		for (Object o : refs.toArray()) {
			SimpleRef ref = (SimpleRef) o;

			if (DustUtilsJava.isEqualLenient(ref.source, source) && DustUtilsJava.isEqualLenient(ref.linkDef, linkDef)
					&& DustUtilsJava.isEqualLenient(ref.target, target)) {
				proc.processRef(ref);
				// } else {
				// DustUtilsDev.dump("skip");
			}
		}
	}
}
