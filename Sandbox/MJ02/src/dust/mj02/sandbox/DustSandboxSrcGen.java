package dust.mj02.sandbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.MessageFormat;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.sandbox.persistence.DustPersistence;
import dust.utils.DustUtilsDev;

public class DustSandboxSrcGen implements DustSandboxComponents, DustProcComponents.DustProcPocessor {

    private String targetDir;
    private DustEntity genRenderer;
    private String fmtFileName;

    class RefUnitProcessor implements RefProcessor {
        private DustEntity msgRenderEval;
        DustRef refSrcUnit;

        public RefUnitProcessor(DustUtils.RefPathResolver rpr, DustEntity msgRenderEval) {
            super();
            this.msgRenderEval = msgRenderEval;
            refSrcUnit = rpr.resolve(ContextRef.self, false);
        }

        @Override
        public void processRef(DustRef ref) {
            DustEntity eT = ref.get(RefKey.target);

            String un = DustUtils.accessEntity(DataCommand.getValue, eT, DustGenericAtts.IdentifiedIdLocal);
            String fName = targetDir + "\\" + MessageFormat.format(fmtFileName, un);

            DustUtilsDev.dump("Generating source:", fName);

            refSrcUnit.hackUpdate(eT);
            DustUtils.accessEntity(DataCommand.tempSend, genRenderer, msgRenderEval);

            String txt = DustUtils.accessEntity(DataCommand.getValue, msgRenderEval, DustDataAtts.MessageReturn);

            Writer fw;
            try {
                fw = new OutputStreamWriter(new FileOutputStream(fName), CHARSET_UTF8);
                fw.write(txt);
                fw.flush();
                fw.close();
            } catch (Exception e) {
                Dust.wrapAndRethrowException("EXporting " + fName, e);
            }
        }
    };

    class ModuleProcessor implements RefProcessor {
        private DustEntity msgRenderEval;
        DustRef refSrcUnit;

        public ModuleProcessor(DustUtils.RefPathResolver rpr, DustEntity msgRenderEval) {
            this.msgRenderEval = msgRenderEval;
            refSrcUnit = rpr.resolve(ContextRef.self, false);
        }

        @Override
        public void processRef(DustRef ref) {
            DustEntity eT = ref.get(RefKey.target);
            
            DustEntity ldLangCont = DustPersistence.getEntityFromUnit("PlatformBase", "2").iterator().next();
//            DustEntity ldTargetUnit = DustPersistence.getEntityFromUnit("PlatformBase", "0").iterator().next();

            String un = eT.toString();
            String path = DustUtils.getByPath(eT, ldLangCont, DustGenericAtts.IdentifiedIdLocal);
            String fName = targetDir + "\\" + path.replace('.', '\\') + "\\" + MessageFormat.format(fmtFileName, un);

            DustUtilsDev.dump("Generating source:", fName);

            refSrcUnit.hackUpdate(eT);
            DustUtils.accessEntity(DataCommand.tempSend, genRenderer, msgRenderEval);

            String txt = DustUtils.accessEntity(DataCommand.getValue, msgRenderEval, DustDataAtts.MessageReturn);

            Writer fw;
            try {
                File f = new File(fName);
                if ( !f.exists() ) {
                    File d = f.getParentFile();
                    if ( !d.exists() ) {
                        d.mkdirs();
                    }
                }
                fw = new OutputStreamWriter(new FileOutputStream(fName), CHARSET_UTF8);
                fw.write(txt);
                fw.flush();
                fw.close();
            } catch (Exception e) {
                Dust.wrapAndRethrowException("EXporting " + fName, e);
            }
        }
    };

    @Override
    public void processorProcess() throws Exception {
        DustUtilsDev.dump("Now generating source codes...");

        targetDir = DustUtils.getCtxVal(ContextRef.self, DustGenericAtts.StreamFileName, false);
        fmtFileName = DustUtils.getCtxVal(ContextRef.self, DustSandboxAtts.SandboxSrcGenFileNameTemplate, false);
        genRenderer = DustUtils.getByPath(ContextRef.self, DustSandboxLinks.SandboxSrcGenRenderer);

        DustUtils.RefPathResolver rpr = new DustUtils.RefPathResolver();

        DustEntity msgRenderEval = DustUtils.accessEntity(DataCommand.getEntity, DustDataTypes.Message);
        DustUtils.accessEntity(DataCommand.setRef, msgRenderEval, DustDataLinks.MessageCommand, DustProcMessages.EvaluatorEvaluate);

        try {
            DustUtils.accessEntity(DataCommand.setValue, ContextRef.session, DustProcAtts.SessionChangeMute, true);

            DustRef refModules = DustUtils.getCtxVal(ContextRef.self, DustSandboxLinks.SandboxSrcGenModules, false);
            if ((null != refModules) && (0 < refModules.count())) {
                ModuleProcessor procModules = new ModuleProcessor(rpr, msgRenderEval);
                DustEntity ldModUnits = DustPersistence.getEntityFromUnit("PlatformBase", "5").iterator().next();

                refModules.processAll(new RefProcessor() {
                    @Override
                    public void processRef(DustRef ref) {
                        DustRef refModUnits = DustUtils.accessEntity(DataCommand.getValue, ref.get(RefKey.target), ldModUnits);
                        refModUnits.processAll(procModules);
                    }
                });
            } else {
                DustRef refUnits = DustUtils.getCtxVal(ContextRef.self, DustSandboxLinks.SandboxSrcGenUnits, false);
                if ((null != refUnits) && (0 < refUnits.count())) {
                    RefUnitProcessor procRefUnits = new RefUnitProcessor(rpr, msgRenderEval);
                    refUnits.processAll(procRefUnits);
                }
            }

            DustUtilsDev.dump("Source code generation success.");

        } finally {
            DustUtils.accessEntity(DataCommand.setValue, ContextRef.session, DustProcAtts.SessionChangeMute, false);
        }
    }

}
