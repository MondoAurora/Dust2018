package dust.pub;

public class DustUtils extends DustUtilsJava implements DustPubComponents {

	public static <RetType> RetType getAttrValueSafe(DustEntity entity, DustAttribute field,
			Creator<RetType> creator, Object ...params ) {
		RetType ret = Dust.getAttrValue(entity, field);
		if ( null == ret ) {
			ret = creator.create(params);
			Dust.setAttrValue(entity, field, ret);
		}
		return ret;
	}

}
