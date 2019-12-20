package dust.mj02.dust.knowledge;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustComponents;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsFactory;

public class DustProcBinaryConnector
		implements DustComponents, DustGenericComponents, DustProcComponents, DustDataComponents {
	private static DustUtilsFactory<Object, DustEntity> KEYS = new DustUtilsFactory<Object, DustComponents.DustEntity>(
			false) {
		@Override
		protected DustEntity create(Object key, Object... hints) {
			return EntityResolver.getEntity(key);
		}
	};

	DustProcSession ctx;

	class ServiceInfo {
		String id;

		DustDataEntity eSvc;
		Class<?> implClass;
		Set<ServiceInfo> allServices;

		public ServiceInfo(DustDataEntity eAssign, DustDataEntity eSvc) {
			this.eSvc = eSvc;

			id = eSvc.get(KEYS.get(DustGenericAtts.IdentifiedIdLocal));

			if (null != eAssign) {
				String cn = eAssign.get(KEYS.get(DustProcAtts.BinaryObjectName));

				try {
					implClass = Class.forName(cn);
				} catch (Throwable e) {
					Dust.wrapAndRethrowException("Failed to create ServiceInfo", e);
				}
			}

			allServices = new HashSet<>();
			allServices.add(this);

			DustDataRef ext = eSvc.get(KEYS.get(DustGenericLinks.ConnectedExtends));
			if (null != ext) {
				ext.processAll(new RefProcessor() {
					@Override
					public void processRef(DustRef ref) {
						allServices.addAll(factServices.get(((DustDataRef) ref).target).allServices);
					}
				});
			}
		}
	}

	class MethodInfo {
		String id;

		ServiceInfo si;

		public MethodInfo(ServiceInfo svc, String id_) {
			super();
			this.si = svc;
			
			String methodName = id_;
			if ( !methodName.startsWith(svc.id) ) {
			    methodName = si.id + id_;
			}
			
			this.id = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
		}
	}

	DustUtilsFactory<DustDataEntity, ServiceInfo> factServices = new DustUtilsFactory<DustDataEntity, ServiceInfo>(false) {
		@SuppressWarnings("unchecked")
		@Override
		protected ServiceInfo create(DustDataEntity key, Object... hints) {
			DustDataRef ec = ctx.mapCtxEntities.get(ContextRef.session).get(KEYS.get(DustProcLinks.SessionBinaryAssignments));

			for (DustDataRef refBa : (Collection<DustDataRef>) ec.container) {
				DustDataEntity eBa = refBa.target;

				DustDataRef refImpl = eBa.get(KEYS.get(DustProcLinks.BinaryImplementedServices));

				for (DustDataRef refSvc : (Collection<DustDataRef>) refImpl.container) {
					DustDataEntity eSvc = refSvc.target;

					if (key == eSvc) {
						return new ServiceInfo(eBa, key);
					}
				}
			}

			return new ServiceInfo(null, key);
		}
	};

	DustUtilsFactory<DustDataEntity, MethodInfo> factMethods = new DustUtilsFactory<DustDataEntity, MethodInfo>(false) {
		@Override
		protected MethodInfo create(DustDataEntity key, Object... hints) {
			String cmdId = key.get(KEYS.get(DustGenericAtts.IdentifiedIdLocal));
			DustDataEntity svc = ((DustDataRef) key.get(KEYS.get(DustGenericLinks.ConnectedOwner))).target;

			ServiceInfo si = factServices.get(svc);

			return new MethodInfo(si, cmdId);
		}
	};
	
	class MethodFactory extends DustUtilsFactory<DustDataEntity, Method> {
		
		public MethodFactory() {
			super(false);
		}
		
		@Override
		protected Method create(DustDataEntity key, Object... hints) {
			MethodInfo mi = factMethods.get(key);
			Object o = hints[0];
			Method m = null;
			
			try {
				m = o.getClass().getMethod(mi.id);
			} catch (Throwable e) {
				Dust.wrapAndRethrowException("Command finding", e);
			}

			return m;
		}
	};


	public DustProcBinaryConnector(DustProcSession ctx) {
		this.ctx = ctx;
	}

	public Object send(DustDataEntity target, DustDataEntity msg) {
		EnumMap<ContextRef, DustDataEntity> store = new EnumMap<>(ctx.mapCtxEntities);
		Throwable t = null;
		Object ret = null;

		try {
			DustDataEntity cmd = ((DustDataRef) msg.get(KEYS.get(DustDataLinks.MessageCommand))).target;
			
			if ( !ctx.accCtrl.isCommandAllowed(ctx.mapCtxEntities.get(ContextRef.self), target, msg)) {
			    throw new DustException("Access denied");
			}
			
			MethodInfo mi = factMethods.get(cmd);
			Object o = DustUtils.getBinary(target, mi.si.eSvc);
			
			if ( null == target.factMethods ) {
				target.factMethods = new MethodFactory();
			}
			
			Method m = target.factMethods.get(cmd, o);
			
			ctx.mapCtxEntities.put(ContextRef.self, target);
			ctx.mapCtxEntities.put(ContextRef.msg, msg);
			
			ret = m.invoke(o);
			msg.put((DustDataEntity)KEYS.get(DustDataAtts.MessageReturn), ret);
		} catch (Throwable e) {
			t = e;
		} finally {
			ctx.mapCtxEntities.putAll(store);
		}

		if (null != t) {
			Dust.wrapAndRethrowException("Command execution", t);
		}
		
		return ret;
	}

	public void instSvc(DustDataEntity target, DustDataEntity svc) {
		Map<DustEntity, Object> bo = target.get(DustDataAtts.EntityBinaries);
		Object o;

		if (null == bo) {
			bo = new HashMap<>();
			target.put(DustDataAtts.EntityBinaries, bo);
			o = null;
		} else {
			o = bo.get(svc);
		}

		if (null == o) {
			ServiceInfo si = factServices.get(svc);
			EnumMap<ContextRef, DustDataEntity> store = new EnumMap<>(ctx.mapCtxEntities);

			try {
				ctx.mapCtxEntities.put(ContextRef.self, target);
				ctx.mapCtxEntities.put(ContextRef.msg, null);

				o = si.implClass.newInstance();
			} catch (Throwable e) {
			    if ( null != si ) {
			        factServices.drop(si);
			    }
				Dust.wrapAndRethrowException("Initializing service", e);
			} finally {
				ctx.mapCtxEntities.putAll(store);
			}

			for (ServiceInfo as : si.allServices) {
				bo.put(as.eSvc, o);
			}
		}
	}
}
