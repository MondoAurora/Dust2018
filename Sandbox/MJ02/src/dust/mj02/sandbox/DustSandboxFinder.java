package dust.mj02.sandbox;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;

public class DustSandboxFinder implements DustSandboxComponents, DustProcComponents.DustProcEvaluator {

    DustEntity ee;
    
    @Override
    public Object evaluatorEvaluate() throws Exception {
        DustEntity eToFind = DustUtils.getCtxVal(ContextRef.self, DustSandboxLinks.SandboxFinderEntity, true);
        
        if ( null == eToFind ) {
            DustUtils.RefPathResolver pr = new DustUtils.RefPathResolver();            
            eToFind = pr.resolve(true);
            
            if ( null == eToFind ) {
                eToFind = pr.resolve(ContextRef.msg, true);
            }
        }
        
        DustRef rPath= DustUtils.getCtxVal(ContextRef.self, DustSandboxLinks.SandboxFinderPath, false);
        
        if ( null == rPath ) {
            return true;
        }
        
        ee = DustUtils.accessEntity(DataCommand.getValue, ContextRef.msg);
//        ee = rRoot.get(RefKey.source);
        
        rPath.processAll(new RefProcessor() {
            @Override
            public void processRef(DustRef ref) {
                ee = (null == ee) ? null : DustUtils.getByPath(ee, (DustEntity) ref.get(RefKey.target));
            }
        });

//        return true;
        return ee == eToFind;
    }
    
}
