package dust.pub;

import java.util.Collection;

import dust.utils.DustUtilsJava;

public class DustUtils extends DustUtilsJava implements DustComponents {

	public static <RetType> RetType getAttrValueSafe(DustEntity entity, DustEntity field, Creator<RetType> creator,
			Object... params) {
		RetType ret = Dust.getAttrValue(entity, field);
		if (null == ret) {
			ret = creator.create(params);
			Dust.setAttrValue(entity, field, ret);
		}
		return ret;
	}
	
	public static void loadRecursive(DustEntity entity, DustEntity link, Collection<DustEntity> known) {
		if ( known.add(entity) ) {
			Dust.processRefs(new DustRefVisitor() {
				@Override
				public boolean dustRefVisit(DustEntity e2) throws Exception {
					loadRecursive(e2, link, known);
					return true;
				}
			}, entity, link);
		}
	}

	@SuppressWarnings("unchecked")
	public static <Content> Content instantiate(String className, Object... arr) {
		try {
			return (Content) instantiate(Class.forName(className), arr);
		} catch (ClassNotFoundException e) {
			DustException.wrapException(e, DustStatusInfoPub.ErrorClassNotFound);
			return null;
		}
	}

	public static <Content> Content instantiate(Class<Content> cc, Object... arr) {
		try {
			return cc.newInstance();
		} catch (Exception e) {
			DustException.wrapException(e, DustStatusInfoPub.ErrorClassInstantiation);
			return null;
		}
	}
}
