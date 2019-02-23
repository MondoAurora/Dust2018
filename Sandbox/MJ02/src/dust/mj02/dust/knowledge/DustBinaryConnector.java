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
import dust.mj02.dust.knowledge.DustDataContext.SimpleEntity;
import dust.mj02.dust.knowledge.DustDataContext.SimpleRef;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsFactory;

public class DustBinaryConnector
		implements DustComponents, DustGenericComponents, DustProcComponents, DustDataComponents {
	private static DustUtilsFactory<Object, DustEntity> KEYS = new DustUtilsFactory<Object, DustComponents.DustEntity>(
			false) {
		@Override
		protected DustEntity create(Object key, Object... hints) {
			return EntityResolver.getEntity(key);
		}
	};

	DustDataContext ctx;

	class ServiceInfo {
		String id;

		SimpleEntity eSvc;
		Class<?> implClass;
		Set<ServiceInfo> allServices;

		public ServiceInfo(SimpleEntity eAssign, SimpleEntity eSvc) {
			this.eSvc = eSvc;

			id = eSvc.get(KEYS.get(DustGenericAtts.identifiedIdLocal));

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

			SimpleRef ext = eSvc.get(KEYS.get(DustGenericLinks.Extends));
			if (null != ext) {
				ext.processAll(new RefProcessor() {
					@Override
					public void processRef(DustRef ref) {
						allServices.addAll(factServices.get(((SimpleRef) ref).target).allServices);
					}
				});
			}
		}
	}

	class MethodInfo {
		String id;

		ServiceInfo si;
//		Method m;

		public MethodInfo(ServiceInfo svc, String id_) {
			super();
			this.si = svc;
			this.id = si.id.substring(0, 1).toLowerCase() + si.id.substring(1) + id_;

//			try {
//				m = svc.implClass.getMethod(id);
//			} catch (Throwable e) {
//				Dust.wrapAndRethrowException("Failed finding method", e);
//			}
		}
	}

	DustUtilsFactory<SimpleEntity, ServiceInfo> factServices = new DustUtilsFactory<SimpleEntity, ServiceInfo>(false) {
		@SuppressWarnings("unchecked")
		@Override
		protected ServiceInfo create(SimpleEntity key, Object... hints) {
			SimpleRef ec = ctx.mapCtxEntities.get(ContextRef.ctx).get(KEYS.get(DustProcLinks.ContextBinaryAssignments));

			for (SimpleRef refBa : (Collection<SimpleRef>) ec.container) {
				SimpleEntity eBa = refBa.target;

				SimpleRef refImpl = eBa.get(KEYS.get(DustProcLinks.BinaryImplementedServices));

				for (SimpleRef refSvc : (Collection<SimpleRef>) refImpl.container) {
					SimpleEntity eSvc = refSvc.target;

					if (key == eSvc) {
						return new ServiceInfo(eBa, key);
					}
				}
			}

			return new ServiceInfo(null, key);
		}
	};

	DustUtilsFactory<SimpleEntity, MethodInfo> factMethods = new DustUtilsFactory<SimpleEntity, MethodInfo>(false) {
		@Override
		protected MethodInfo create(SimpleEntity key, Object... hints) {
			String cmdId = key.get(KEYS.get(DustGenericAtts.identifiedIdLocal));
			SimpleEntity svc = ((SimpleRef) key.get(KEYS.get(DustGenericLinks.Owner))).target;

			ServiceInfo si = factServices.get(svc);

			return new MethodInfo(si, cmdId);
		}
	};
	
	class MethodFactory extends DustUtilsFactory<SimpleEntity, Method> {
		
		public MethodFactory() {
			super(false);
		}
		
		@Override
		protected Method create(SimpleEntity key, Object... hints) {
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


	public DustBinaryConnector(DustDataContext ctx) {
		this.ctx = ctx;
	}

	public void send(SimpleEntity target, SimpleEntity msg) {
		EnumMap<ContextRef, SimpleEntity> store = new EnumMap<>(ctx.mapCtxEntities);
		Throwable t = null;

		try {
			SimpleEntity cmd = ((SimpleRef) msg.get(KEYS.get(DustDataLinks.MessageCommand))).target;
			MethodInfo mi = factMethods.get(cmd);
			Object o = DustUtils.getBinary(target, mi.si.eSvc);
			
			if ( null == target.factMethods ) {
				target.factMethods = new MethodFactory();
			}
			
			Method m = target.factMethods.get(cmd, o);
			
			ctx.mapCtxEntities.put(ContextRef.self, target);
			ctx.mapCtxEntities.put(ContextRef.msg, msg);
			
			m.invoke(o);
		} catch (Throwable e) {
			t = e;
		} finally {
			ctx.mapCtxEntities.putAll(store);
		}

		if (null != t) {
			Dust.wrapAndRethrowException("Command execution", t);
		}
	}

	public void instSvc(SimpleEntity target, SimpleEntity svc) {
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
			EnumMap<ContextRef, SimpleEntity> store = new EnumMap<>(ctx.mapCtxEntities);

			try {
				ctx.mapCtxEntities.put(ContextRef.self, target);
				ctx.mapCtxEntities.put(ContextRef.msg, null);

				o = si.implClass.newInstance();
			} catch (Throwable e) {
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
