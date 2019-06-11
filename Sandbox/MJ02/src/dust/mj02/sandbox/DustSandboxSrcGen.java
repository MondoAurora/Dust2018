package dust.mj02.sandbox;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsDev;

public class DustSandboxSrcGen implements DustSandboxComponents, DustProcComponents.DustProcPocessor {

    @Override
    public void processorProcess() throws Exception {
        String targetDir = DustUtils.getCtxVal(ContextRef.self, DustGenericAtts.StreamFileName, false);
        DustRef refUnits = DustUtils.getCtxVal(ContextRef.self, DustSandboxLinks.SandboxSrcGenUnits, false);
        
        DustEntity genRenderer = DustUtils.getByPath(ContextRef.self, DustSandboxLinks.SandboxSrcGenRenderer);
        
        DustUtils.RefPathResolver rpr = new DustUtils.RefPathResolver();
        
        DustRef refSrcUnit = rpr.resolve(ContextRef.self, false);
        
        DustEntity msg = DustUtils.accessEntity(DataCommand.getEntity, DustDataTypes.Message);
        DustUtils.accessEntity(DataCommand.setRef, msg, DustDataLinks.MessageCommand, DustProcMessages.EvaluatorEvaluate);

        try {
            DustUtils.accessEntity(DataCommand.setValue, ContextRef.session, DustProcAtts.SessionChangeMute, true);

        
        refUnits.processAll(new RefProcessor() {
            
            @Override
            public void processRef(DustRef ref) {
                DustEntity eT = ref.get(RefKey.target);
                
                String un = DustUtils.accessEntity(DataCommand.getValue, eT, DustGenericAtts.IdentifiedIdLocal);
                String fName = targetDir + "\\" + un + ".cs";
                
                DustUtilsDev.dump("Generating source:", fName);

                refSrcUnit.hackUpdate(eT);
                DustUtils.accessEntity(DataCommand.tempSend, genRenderer, msg);
                
                String txt = DustUtils.accessEntity(DataCommand.getValue, msg, DustDataAtts.MessageReturn);

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
        });
        
        } finally {
            DustUtils.accessEntity(DataCommand.setValue, ContextRef.session, DustProcAtts.SessionChangeMute, false);

        }
        DustUtilsDev.dump("Now generating source codes...");
    }

}
