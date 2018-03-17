package dust.runtime.simple;

import java.util.HashMap;
import java.util.Map;

import dust.pub.boot.DustBootComponents;
import dust.pub.metaenum.DustMetaEnum;
import dust.utils.DustUtilsFactory;

public class DustSimpleManagerMeta implements DustSimpleRuntimeComponents, DustMetaEnum.DustMetaManager,
		DustBootComponents.DustConfigurable, DustBootComponents.DustShutdownAware {

	private DustUtilsFactory<Enum<?>, SimpleType> factType = new DustUtilsFactory<Enum<?>, SimpleType>(false) {
		@Override
		protected SimpleType create(Enum<?> key, Object... hints) {
			return new SimpleType(key);
		}
	};

	private Map<DustAttribute, SimpleAttDef> mapAttributes = new HashMap<>();
	private Map<DustLink, SimpleLinkDef> mapLinkDefs = new HashMap<>();

	DustUtilsFactory<Enum<?>, SimpleEntity> constants = new DustUtilsFactory<Enum<?>, SimpleEntity>(false) {
		@Override
		protected SimpleEntity create(Enum<?> key, Object... hints) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	SimpleAttDef getSimpleAttDef(DustAttribute att) {
		return mapAttributes.get(att);
	}

	SimpleLinkDef getSimpleLinkDef(DustLink link) {
		return mapLinkDefs.get(link);
	}

	SimpleEntity optResolveEntity(DustEntity entity) {
		return (entity instanceof Enum) ? constants.get((Enum<?>) entity) : null;
	}

	@Override
	public void init(DustConfig config) throws Exception {
	}

	@Override
	public void shutdown() throws Exception {
		factType.clear();
	}

	@Override
	public void registerUnit(Class<? extends Enum<?>> types, Class<? extends Enum<?>> services) {
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

}
