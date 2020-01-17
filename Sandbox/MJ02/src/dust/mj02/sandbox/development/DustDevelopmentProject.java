package dust.mj02.sandbox.development;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustTempHacks;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.text.DustTextComponents;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsJava;

public class DustDevelopmentProject implements DustDevelopmentComponents, DustProcComponents.DustProcPocessor, DustTextComponents {

    @Override
    public void processorProcess() throws Exception {
        DustUtilsDev.dump("Hello, world!");

        String prjName = DustUtils.getByPath(ContextRef.self, DustGenericAtts.IdentifiedIdLocal);

        Map<String, Object> mapAtts = new HashMap<>();
        DustTempHacks.loadAtts(mapAtts, ContextRef.self);

        DustEntity dev = DustUtils.getByPath(ContextRef.self, DustGenericLinks.ConnectedOwner);

        Map<String, Object> mapAtts2 = new HashMap<>();
        DustTempHacks.loadAtts(mapAtts2, dev);

        String devRoot = (String) mapAtts2.get("devRoot");
        String prjRoot = (String) mapAtts.get("projectRoot");

        String path = devRoot + "/" + prjRoot + "/" + prjName;

        File prjDir = DustUtilsJava.safeGetDir(path);

        createSubdir(prjDir, DEV_SRCDIR_MANUAL);

        File genDir = createSubdir(prjDir, DEV_SRCDIR_GENERATED);

        Dust.processRefs(new RefProcessor() {
            @Override
            public void processRef(DustRef ref) {
                DustEntity eLD = ref.get(RefKey.linkDef);
                String id = DustUtils.getByPath(eLD, DustGenericAtts.IdentifiedIdLocal);

                mapAtts.put(id, ref);
            }
        }, ContextRef.self, null, null);

        DustRef files = (DustRef) mapAtts.get("ProjectFiles");

        DustEntity msgRenderEval = DustUtils.accessEntity(DataCommand.getEntity, DustDataTypes.Message);
        DustUtils.accessEntity(DataCommand.setValue, msgRenderEval, DustGenericAtts.IdentifiedIdLocal, "EvalMsg");
        DustUtils.accessEntity(DataCommand.setRef, msgRenderEval, DustDataLinks.MessageCommand, DustProcMessages.EvaluatorEvaluate);

        DustEntity eRenderer = DustUtils.accessEntity(DataCommand.getEntity, DustTextTypes.TextRenderer);
        DustUtils.accessEntity(DataCommand.setValue, eRenderer, DustGenericAtts.IdentifiedIdLocal, "Renderer");
        DustUtils.accessEntity(DataCommand.setRef, eRenderer, DustGenericLinks.ContextAwareEntity, ContextRef.self);

        files.processAll(new RefProcessor() {
            @Override
            public void processRef(DustRef ref) {
                DustEntity ft = ref.get(RefKey.target);

                DustUtils.accessEntity(DataCommand.setRef, eRenderer, DustTextLinks.TextRendererRoot, ft);
                DustUtils.accessEntity(DataCommand.tempSend, eRenderer, msgRenderEval);
                String txt = DustUtils.accessEntity(DataCommand.getValue, msgRenderEval, DustDataAtts.MessageReturn);

                // String txt = DustUtils.getByPath(ft,
                // DustTextComponents.DustTextAtts.TextSpanString);

                String fn = DustUtils.getByPath(ft, DustGenericAtts.IdentifiedIdLocal);
                DustUtils.writeToFile(prjDir, fn, txt);
            }
        });

        DustRef packages = (DustRef) mapAtts.get("ProjectPackages");
        if (null != packages) {
            DustEntity packClass = ((DustRef) mapAtts.get("ProjectPackageClass")).get(RefKey.target);
            Map<DustEntity, DustEntity> mapParents = new HashMap<>();

            Dust.processRefs(new RefProcessor() {
                @Override
                public void processRef(DustRef ref) {
                    mapParents.put(ref.get(RefKey.source), null);
                }
            }, null, EntityResolver.getEntity(DustGenericLinks.TagClass), packClass);

            Dust.processRefs(new RefProcessor() {
                @Override
                public void processRef(DustRef ref) {
                    DustEntity src = ref.get(RefKey.source);
                    DustEntity trg = ref.get(RefKey.target);
                    if (mapParents.containsKey(src) && mapParents.containsKey(trg)) {
                        mapParents.put(src, trg);
                    }
                }
            }, null, EntityResolver.getEntity(DustDataLinks.EntityTags), null);

            packages.processAll(new RefProcessor() {
                @Override
                public void processRef(DustRef ref) {
                    StringBuilder sb = null;
                    for (DustEntity ePkg = ref.get(RefKey.target); null != ePkg; ePkg = mapParents.get(ePkg)) {
                        String fn = DustUtils.getByPath(ePkg, DustGenericAtts.IdentifiedIdLocal);
                        if ( null == sb ) {
                            sb = new StringBuilder(fn);
                        } else {
                            sb.insert(0, File.separator);
                            sb.insert(0, fn);
                        }
                    }
                    createSubdir(genDir, sb.toString());
                }
            });
        }
    }

    public static File createSubdir(File parent, String dir) {
        File genDir = DustUtilsJava.safeGetDir(parent, dir);
        if (DustUtilsJava.isEmpty(genDir.list())) {
            DustUtils.writeToFile(genDir, "placeholder.txt", "Created on " + new Date());
        }
        return genDir;
    }
}
