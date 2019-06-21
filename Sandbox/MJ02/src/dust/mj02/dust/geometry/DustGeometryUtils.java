package dust.mj02.dust.geometry;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustKernelComponents;

public class DustGeometryUtils implements DustGeometryComponents, DustKernelComponents {
    public static Double getMeasurement(DustEntity from, DustEntity axis) {
        Object val = DustUtils.getByPath(from, DustGeometryLinks.GeometricDataMeasurements, axis, DustDataAtts.VariantValue);

        return ( null == val ) ? null : Double.parseDouble((String)val);
    }
}
