package dust.pub;

public class DustUtils extends DustUtilsJava implements DustPubComponents {

	public static <RetType> RetType getAttrValueSafe(DustEntity entity, DustAttribute field, Creator<RetType> creator,
			Object... params) {
		RetType ret = Dust.getAttrValue(entity, field);
		if (null == ret) {
			ret = creator.create(params);
			Dust.setAttrValue(entity, field, ret);
		}
		return ret;
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
