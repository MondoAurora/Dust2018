/*
 * Generated by Montru. 
 * Do not edit this file because it might be overwritten.
 */

package dust.mj02.dust.geometry;

import dust.mj02.dust.DustComponents;

public interface DustGeometryComponents extends DustComponents {
	enum DustGeometryTypes implements DustEntityKey { 
		GeometricDimension, ShapePath, ShapeArc, RenderSource, GeometricDataRole, GeometricData, RenderTarget, ShapeComposite, GeometricInclusion, ShapeBox, ShapeRef
	};

	enum DustGeometryAtts implements DustEntityKey { 
		ShapePathClosed
	};

	enum DustGeometryLinks implements DustEntityKey { 
		ShapeArcBegin, ShapeBoxSize, GeometricInclusionTarget, GeometricDataType, GeometricInclusionParameters, ShapeArcEnd, GeometricDataMeasurements
	};

	enum DustGeometryServices implements DustEntityKey { 
		RenderTarget, RenderSourceSimple, RenderSourceComposite
	};

	enum DustGeometryMessages implements DustEntityKey { 
	};

	enum DustGeometryTags implements DustEntityKey { 
	};

	enum DustGeometryValues implements DustEntityKey { 
		GeometricDimensionCartesianZ, GeometricDimensionCartesianY, GeometricDataRoleRotate, GeometricDimensionCartesianX, GeometricDataRoleLocate, GeometricDataRoleScale
	};
}
