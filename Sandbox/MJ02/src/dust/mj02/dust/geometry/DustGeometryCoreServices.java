package dust.mj02.dust.geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustKernelComponents;
import dust.mj02.dust.knowledge.DustProcComponents;

public interface DustGeometryCoreServices extends DustGeometryComponents, DustKernelComponents {
    DustGeometryValues EXEC_ORDER[] = { DustGeometryValues.GeometricDataRoleScale, DustGeometryValues.GeometricDataRoleRotate,
            DustGeometryValues.GeometricDataRoleLocate, };

    DustGeometryValues ACCESS[] = { DustGeometryValues.GeometricDimensionCartesianX, DustGeometryValues.GeometricDimensionCartesianY,
            DustGeometryValues.GeometricDimensionCartesianZ, };

    public class DustRenderSource implements DustProcComponents.DustProcPocessor {

        @Override
        public void processorProcess() throws Exception {
            DustEntity eSrc = DustUtils.getCtxVal(ContextRef.self, null, true);
            DustEntity pt = DustUtils.getCtxVal(ContextRef.self, DustDataLinks.EntityPrimaryType, true);
            DustGeometryTypes dgt = EntityResolver.getKey(pt);
            
            if ( DustGeometryTypes.ShapeRef == dgt ) {
                DustUtils.RefPathResolver rp = new DustUtils.RefPathResolver();
                eSrc = rp.resolve(true);
                pt = DustUtils.getByPath(eSrc, DustDataLinks.EntityPrimaryType);
                dgt = EntityResolver.getKey(pt);
            }
            
            DustEntity eTarget = DustUtils.getCtxVal(ContextRef.msg, DustGenericLinks.ContextAwareEntity, true);


            switch (dgt) {
            case ShapePath:
                DustUtils.accessEntity(DataCommand.setRef, eTarget, DustGeometryLinks.RenderTargetAllShapes, eSrc);
                break;
            case ShapeArc:
                break;
            case ShapeBox:
                break;
            case ShapeComposite:
                break;
            default:
                Dust.wrapAndRethrowException("Invalid item type " + dgt, null);
                break;
            }

        }
    }

    public class DustRenderTarget
            implements DustProcComponents.DustProcEvaluator, DustProcComponents.DustProcActive, DustProcComponents.DustProcPocessor {

        class Transformation {
            DustGeometryValues action;
            DustEntity node;

            Map<DustGeometryValues, Double> values = new HashMap<>();

            public Transformation(DustGeometryValues action, DustEntity node) {
                this.action = action;
                this.node = node;
                //
                // DustRef ref = DustUtils.accessEntity(DataCommand.getValue, node, )
                //
                // for ( DustGeometryValues gv : ACCESS ) {
                //
                // values.put(gv, )
                // }
            }

        }

        class CollectedTransformations {
            ArrayList<Transformation> transformations = new ArrayList<>();

            public void add(DustEntity node) {
                for (DustGeometryValues action : EXEC_ORDER) {
                    DustEntity ea = DustUtils.getByPath(node, DustGenericLinks.ContextAwareEntity, DustGeometryLinks.GeometricInclusionParameters,
                            action);
                    if (null != ea) {
                        transformations.add(new Transformation(action, ea));
                    }
                }
            }

            public void remove(DustEntity node) {
                for (Iterator<Transformation> it = transformations.iterator(); it.hasNext();) {
                    if (it.next().node == node) {
                        it.remove();
                    }
                }
            }
        }

        @Override
        public void processorProcess() throws Exception {
            // TODO Auto-generated method stub

        }

        @Override
        public void activeInit() throws Exception {
            DustEntity eIncl = DustUtils.getMsgVal(DustGenericLinks.ContextAwareEntity, true);

            DustEntity eNode = DustUtils.accessEntity(DataCommand.getEntity, DustGeometryTypes.RenderNode);
            DustUtils.accessEntity(DataCommand.setRef, eNode, DustGenericLinks.ContextAwareEntity, eIncl);

            DustEntity eParentNode = DustUtils.accessEntity(DataCommand.getValue, ContextRef.msg, DustGeometryLinks.RenderTargetNodeStack, 0);
            if (null != eParentNode) {
                DustUtils.accessEntity(DataCommand.setRef, eNode, DustGenericLinks.ConnectedOwner, eParentNode);
            }

            CollectedTransformations ct = DustUtils.accessEntity(DataCommand.getValue, ContextRef.msg,
                    DustGeometryAtts.RenderTargetTransformColl);
            if (null == ct) {
                ct = new CollectedTransformations();
                DustUtils.accessEntity(DataCommand.setValue, ContextRef.msg, DustGeometryAtts.RenderTargetTransformColl, ct);
            }

            ct.add(eNode);

            DustUtils.accessEntity(DataCommand.setValue, ContextRef.msg, DustGeometryLinks.RenderTargetNodeStack, eNode, 0);
        }

        @Override
        public void activeRelease() throws Exception {
            DustEntity eNode = DustUtils.accessEntity(DataCommand.getValue, ContextRef.msg, DustGeometryLinks.RenderTargetNodeStack, 0);
            CollectedTransformations ct = DustUtils.accessEntity(DataCommand.getValue, ContextRef.msg,
                    DustGeometryAtts.RenderTargetTransformColl);
            ct.remove(eNode);

            DustUtils.accessEntity(DataCommand.removeRef, ContextRef.msg, DustGeometryLinks.RenderTargetNodeStack, eNode);
        }

        @Override
        public Object evaluatorEvaluate() throws Exception {
            // TODO Auto-generated method stub
            return null;
        }
    }

}
