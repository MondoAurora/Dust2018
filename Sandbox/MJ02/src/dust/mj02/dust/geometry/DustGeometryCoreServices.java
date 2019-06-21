package dust.mj02.dust.geometry;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustKernelComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsFactory;

public interface DustGeometryCoreServices extends DustGeometryComponents, DustKernelComponents {
    DustGeometryValues EXEC_ORDER[] = { DustGeometryValues.GeometricDataRoleScale, DustGeometryValues.GeometricDataRoleRotate,
            DustGeometryValues.GeometricDataRoleLocate, };

    DustGeometryValues DIMS[] = { DustGeometryValues.GeometricDimensionCartesianX, DustGeometryValues.GeometricDimensionCartesianY,
            DustGeometryValues.GeometricDimensionCartesianZ, };

    public class DustRenderSourceSimple implements DustProcComponents.DustProcPocessor {
        @Override
        public void processorProcess() throws Exception {
            DustEntity eTarget = DustUtils.getCtxVal(ContextRef.msg, DustGenericLinks.ContextAwareEntity, true);
            DustEntity msg = DustUtils.getCtxVal(ContextRef.msg, DustGenericLinks.CallbackMessage, true);

            DustEntity self = DustUtils.getCtxVal(ContextRef.self, null, true);
            DustUtils.accessEntity(DataCommand.setRef, msg, DustGenericLinks.ContextAwareEntity, self);

            DustUtils.accessEntity(DataCommand.tempSend, eTarget, msg);
        }
    }

    public class DustSimplePoint3D {
        
        EnumMap<DustGeometryValues, Double> vals = new EnumMap<>(DustGeometryValues.class);

        DustSimplePoint3D(DustEntity node) {
            for ( DustGeometryValues v : DIMS ) {
                vals.put(v, DustGeometryUtils.getMeasurement(node, EntityResolver.getEntity(v)));
            }
        }
        
        Double get(DustGeometryValues dim) {
            return vals.get(dim);
        }
        
        boolean isSet(DustGeometryValues... dims) {
            for ( DustGeometryValues d : dims ) {
                if ( !vals.containsKey(d)) {
                    return false;
                }
            }
            return true;
        }
        
        void apply(DustGeometryValues action, DustSimplePoint3D param) {
            switch (action) {
            case GeometricDataRoleLocate:
                for ( DustGeometryValues v : DIMS ) {
                    Double orig = vals.get(v);
                    Double diff = param.vals.get(v);
                    if ( null != diff ) {
                        vals.put(v, (null == orig) ? diff : orig + diff);
                    }
                }

                break;
            case GeometricDataRoleScale:
                for ( DustGeometryValues v : DIMS ) {
                    Double orig = vals.get(v);
                    Double diff = param.vals.get(v);
                    if ( (null != diff) && (null != orig) ) {
                        vals.put(v, orig * diff);
                    }
                }
                break;
            case GeometricDataRoleRotate:
                Double simpleRot = param.vals.get(DustGeometryValues.GeometricDimensionCartesianZ);
                if ( null != simpleRot ) {
                    simpleRot = simpleRot / 180 * Math.PI;
                    Double sX = vals.get(DustGeometryValues.GeometricDimensionCartesianX);
                    Double sY = vals.get(DustGeometryValues.GeometricDimensionCartesianY);
                    
                    Double rSin = Math.sin(simpleRot);
                    Double rCos = Math.cos(simpleRot);

                    Double nX = sX * rCos - sY * rSin;
                    Double nY = sX * rSin + sY * rCos;
                    
                    vals.put(DustGeometryValues.GeometricDimensionCartesianX, nX);
                    vals.put(DustGeometryValues.GeometricDimensionCartesianY, nY);
                }
                break;

            default:
                break;
            }
        }
        
        @Override
        public String toString() {
            return vals.toString();
        }
    }

    public class DustRenderSourceComposite implements DustProcComponents.DustProcPocessor {

        @Override
        public void processorProcess() throws Exception {
            DustEntity eTarget = DustUtils.getCtxVal(ContextRef.msg, DustGenericLinks.ContextAwareEntity, true);

            DustUtils.LazyMsgContainer lmc = new DustUtils.LazyMsgContainer() {
                @Override
                protected DustEntity createMsg() {
                    DustEntity eMsg = DustUtils.accessEntity(DataCommand.getEntity, DustDataTypes.Message);
                    return eMsg;
                }
            };

            DustEntity eSrc = DustUtils.getCtxVal(ContextRef.self, null, true);
            DustUtils.accessEntity(DataCommand.processRef, eSrc, DustCollectionLinks.SequenceMembers, new RefProcessor() {
                @Override
                public void processRef(DustRef ref) {
                    DustEntity incl = ref.get(RefKey.target);

                    DustEntity msgAct = lmc.getMsg();
                    DustUtils.accessEntity(DataCommand.setRef, msgAct, DustGenericLinks.ContextAwareEntity, incl);

                    DustUtils.accessEntity(DataCommand.setRef, msgAct, DustDataLinks.MessageCommand, DustProcMessages.ActiveInit);
                    DustUtils.accessEntity(DataCommand.tempSend, eTarget, msgAct);

                    DustEntity shape = DustUtils.getByPath(incl, DustGeometryLinks.GeometricInclusionTarget);
                    DustEntity eMsg = DustUtils.getCtxVal(ContextRef.msg, null, true);
                    DustUtils.accessEntity(DataCommand.tempSend, shape, eMsg);

                    DustUtils.accessEntity(DataCommand.setRef, msgAct, DustDataLinks.MessageCommand, DustProcMessages.ActiveRelease);
                    DustUtils.accessEntity(DataCommand.tempSend, eTarget, msgAct);
                }
            });
        }
    }

    public class DustRenderTargetAwtGeom
            implements DustProcComponents.DustProcEvaluator, DustProcComponents.DustProcActive, DustProcComponents.DustProcPocessor {

        class GraphNode {
            DustEntity incl;

            public GraphNode(DustEntity incl) {
                this.incl = incl;
            }
        }

        class Transformation {
            DustGeometryValues action;
            DustEntity node;    
            DustSimplePoint3D pt;

            public Transformation(DustGeometryValues action, DustEntity node) {
                this.action = action;
                this.node = node;

                pt = new DustSimplePoint3D(node);
            }

            public void apply(DustSimplePoint3D ptTarget) {
                ptTarget.apply(action, pt);
            }
        }

        public class CollectedTransformations {
            ArrayList<Transformation> transformations = new ArrayList<>();

            public void add(DustEntity node) {
                for (DustGeometryValues action : EXEC_ORDER) {
                    DustEntity ea = DustUtils.getByPath(node, DustGeometryLinks.GeometricInclusionParameters, action);
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

            public void apply(DustSimplePoint3D node) {
                for (Transformation t : transformations) {
                    t.apply(node);
                }
            }
        }

        Map<DustEntity, Shape> mapShapes;
        DustUtilsFactory<DustEntity, GraphNode> factNodes = new DustUtilsFactory<DustEntity, GraphNode>(false) {
            @Override
            protected GraphNode create(DustEntity key, Object... hints) {
                return new GraphNode(key);
            }
        };

//        DustEntity axisX = EntityResolver.getEntity(DustGeometryValues.GeometricDimensionCartesianX);
//        DustEntity axisY = EntityResolver.getEntity(DustGeometryValues.GeometricDimensionCartesianY);

        CollectedTransformations ct = new CollectedTransformations();

        @Override
        public void processorProcess() throws Exception {
            DustEntity eShape = DustUtils.getCtxVal(ContextRef.msg, DustGenericLinks.ContextAwareEntity, true);

            DustEntity pt = DustUtils.getByPath(eShape, DustDataLinks.EntityPrimaryType);
            DustGeometryTypes dgt = EntityResolver.getKey(pt);

            switch (dgt) {
            case ShapePath:
                GeneralPath path = new GeneralPath();

                DustUtils.accessEntity(DataCommand.processRef, eShape, DustCollectionLinks.SequenceMembers, new RefProcessor() {
                    @Override
                    public void processRef(DustRef ref) {
                        DustEntity point = ref.get(RefKey.target);
                        DustSimplePoint3D pt = new DustSimplePoint3D(point);

                        if (pt.isSet(DustGeometryValues.GeometricDimensionCartesianX, DustGeometryValues.GeometricDimensionCartesianY)) {
                            ct.apply(pt);

                            Double x = pt.get(DustGeometryValues.GeometricDimensionCartesianX);
                            Double y = pt.get(DustGeometryValues.GeometricDimensionCartesianY);
                            if (null == path.getCurrentPoint()) {
                                path.moveTo(x, y);
                            } else {
                                path.lineTo(x, y);
                            }
                        }
                    }
                });

                if (null != path.getCurrentPoint()) {
                    if (DustUtils.isTrue(eShape, DustGeometryAtts.ShapePathClosed)) {
                        path.closePath();
                    }
                    mapShapes.put(eShape, path);
                }
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

        @Override
        public void activeInit() throws Exception {
            DustEntity eIncl = DustUtils.getMsgVal(DustGenericLinks.ContextAwareEntity, true);
            factNodes.get(eIncl);
            ct.add(eIncl);
        }

        @Override
        public void activeRelease() throws Exception {
            DustEntity eIncl = DustUtils.getMsgVal(DustGenericLinks.ContextAwareEntity, true);
            ct.remove(eIncl);
        }

        @Override
        public Object evaluatorEvaluate() throws Exception {
            ct = new CollectedTransformations();
            mapShapes = new HashMap<>();

            DustEntity self = DustUtils.getCtxVal(ContextRef.self, null, true);

            DustEntity msgProc = DustUtils.accessEntity(DataCommand.cloneEntity, ContextRef.msg);
            DustUtils.accessEntity(DataCommand.setRef, msgProc, DustDataLinks.MessageCommand, DustProcMessages.ProcessorProcess);
            DustUtils.accessEntity(DataCommand.setRef, msgProc, DustGenericLinks.ContextAwareEntity, self);

            DustEntity msgCB = DustUtils.accessEntity(DataCommand.cloneEntity, ContextRef.msg);
            DustUtils.accessEntity(DataCommand.setRef, msgCB, DustDataLinks.MessageCommand, DustProcMessages.ProcessorProcess);
            DustUtils.accessEntity(DataCommand.setRef, msgProc, DustGenericLinks.CallbackMessage, msgCB);

            DustEntity eTarget = DustUtils.getCtxVal(ContextRef.msg, DustGenericLinks.ContextAwareEntity, true);

            DustUtils.accessEntity(DataCommand.tempSend, eTarget, msgProc);

            return mapShapes;
        }
    }

}
