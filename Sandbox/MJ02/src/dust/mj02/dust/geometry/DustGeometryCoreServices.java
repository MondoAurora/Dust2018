package dust.mj02.dust.geometry;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
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

    DustGeometryValues ACCESS[] = { DustGeometryValues.GeometricDimensionCartesianX, DustGeometryValues.GeometricDimensionCartesianY,
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

            Map<DustGeometryValues, Double> values = new HashMap<>();

            public Transformation(DustGeometryValues action, DustEntity node) {
                this.action = action;
                this.node = node;
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
        }

        Map<DustEntity, Shape> mapShapes;
        DustUtilsFactory<DustEntity, GraphNode> factNodes = new DustUtilsFactory<DustEntity, GraphNode> ( false ) {
            @Override
            protected GraphNode create(DustEntity key, Object... hints) {
                return new GraphNode(key);
            }
        };
        
        DustEntity axisX = EntityResolver.getEntity(DustGeometryValues.GeometricDimensionCartesianX);
        DustEntity axisY = EntityResolver.getEntity(DustGeometryValues.GeometricDimensionCartesianY);

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
                        Double x = DustGeometryUtils.getMeasurement(point, axisX);
                        Double y = DustGeometryUtils.getMeasurement(point, axisY);

                        if ((null != x) && (null != y)) {
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
