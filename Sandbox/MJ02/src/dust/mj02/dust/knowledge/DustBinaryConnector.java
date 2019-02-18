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
			String cn = eAssign.get(KEYS.get(DustProcAtts.BinaryObjectName));

			try {
				implClass = Class.forName(cn);
			} catch (Throwable e) {
				Dust.wrapAndRethrowException("Failed to create ServiceInfo", e);
			}

			allServices = new HashSet<>();
			allServices.add(this);
		}
	}

	class MethodInfo {
		String id;

		ServiceInfo si;
		Method m;

		public MethodInfo(ServiceInfo svc, String id_) {
			super();
			this.si = svc;
			this.id = si.id.substring(0, 1).toLowerCase() + si.id.substring(1) + id_;

			try {
				m = svc.implClass.getMethod(id);
			} catch (Throwable e) {
				Dust.wrapAndRethrowException("Failed finding method", e);
			}
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
						return new ServiceInfo(eBa, eSvc);
					}
				}
			}
			return null;
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

	public DustBinaryConnector(DustDataContext ctx) {
		this.ctx = ctx;
	}

	public void send(SimpleEntity target, SimpleEntity msg) {
		EnumMap<ContextRef, SimpleEntity> store = new EnumMap<>(ctx.mapCtxEntities);
		Throwable t = null;

		try {
			Object o = null;
			Map<DustEntity, Object> bo = target.binObjs;

			SimpleEntity cmd = ((SimpleRef) msg.get(KEYS.get(DustDataLinks.MessageCommand))).target;
			MethodInfo mi = factMethods.get(cmd);

			if (null == bo) {
				bo = target.binObjs = new HashMap<>();
				o = null;
			} else {
				o = bo.get(mi.si.eSvc);
			}

			if (null == o) {
				ServiceInfo si = factServices.get(mi.si.eSvc);
				o = si.implClass.newInstance();

				for (ServiceInfo as : si.allServices) {
					bo.put(as.eSvc, o);
				}
			}

			ctx.mapCtxEntities.put(ContextRef.self, target);
			ctx.mapCtxEntities.put(ContextRef.msg, msg);

			mi.m.invoke(o);
		} catch (Throwable e) {
			t = e;
		} finally {
			ctx.mapCtxEntities.putAll(store);
		}

		if (null != t) {
			Dust.wrapAndRethrowException("Command execution", t);
		}
	}

}
