package dust.runtime.simple;

import java.util.HashMap;
import java.util.Map;

import dust.gen.dust.meta.DustMetaServices;
import dust.pub.DustUtils;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public class DustSimpleManagerMeta implements DustSimpleRuntimeComponents, DustMetaServices.DustMetaManager,
		DustBootComponents.DustConfigurable, DustBootComponents.DustShutdownAware {

	private DustUtilsFactory<Enum<?>, SimpleType> factType = new DustUtilsFactory<Enum<?>, SimpleType>(false) {
		@Override
		protected SimpleType create(Enum<?> key, Object... hints) {
			return new SimpleType(key);
		}
	};

	private Map<DustAttribute, SimpleAttDef> mapAttributes = new HashMap<>();
	private Map<DustLink, SimpleLinkDef> mapLinkDefs = new HashMap<>();

	DustUtilsFactory<Enum<?>, SimpleEntity> factConstants = new DustUtilsFactory<Enum<?>, SimpleEntity>(false) {
		@Override
		protected SimpleEntity create(Enum<?> key, Object... hints) {
			SimpleEntity se = new SimpleEntity(null, getSimpleType(DustBaseTypes.ConstValue));
			return se;
		}
	};

	SimpleType getSimpleType(Enum<?> type) {
		return factType.get(type);
	}

	SimpleAttDef getSimpleAttDef(DustAttribute att) {
		return mapAttributes.get(att);
	}

	SimpleLinkDef getSimpleLinkDef(DustLink link) {
		return mapLinkDefs.get(link);
	}

	SimpleEntity optResolveEntity(DustEntity entity) {
		return (entity instanceof Enum) ? factConstants.get((Enum<?>) entity) : null;
	}

	@Override
	public void init(DustConfig config) throws Exception {
	}

	@Override
	public void shutdown() throws Exception {
		factType.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerUnit(String typeClass, String serviceClass) throws Exception {
		if (!DustUtils.isEmpty(typeClass)) {
			Class<? extends Enum<?>> types = (Class<? extends Enum<?>>) Class.forName(typeClass);
			for (Enum<?> t : types.getEnumConstants()) {
				DustMetaTypeDescriptor md = (DustMetaTypeDescriptor) t;
				SimpleType st = factType.get(t, md);

				Class<? extends Enum<?>> ae = md.getAttribEnum();
				if (null != ae) {
					for (Enum<?> a : ae.getEnumConstants()) {
						DustAttribute att = (DustAttribute) a;
						mapAttributes.put(att, st.getAttDef(att));
					}
				}

				Class<? extends Enum<?>> le = md.getLinkEnum();
				if (null != le) {
					for (Enum<?> l : le.getEnumConstants()) {
						DustLink link = (DustLink) l;
						mapLinkDefs.put(link, st.getLinkDef(link));
					}
				}
			}
		}
		if (!DustUtils.isEmpty(serviceClass)) {
			Class<? extends Enum<?>> services = (Class<? extends Enum<?>>) Class.forName(serviceClass);
			for (Enum<?> t : services.getEnumConstants()) {
			}
		}
	}

}
