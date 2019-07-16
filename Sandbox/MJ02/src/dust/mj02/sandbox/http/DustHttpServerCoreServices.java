package dust.mj02.sandbox.http;

import java.util.Set;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.sandbox.persistence.DustPersistence;
import dust.mj02.sandbox.persistence.DustPersistentStorageJsonSingle;

public interface DustHttpServerCoreServices extends DustHttpComponents {
    
    public static class HttpServletGetRef implements DustProcComponents.DustProcPocessor {
        
        @Override
        public void processorProcess() throws Exception {
            String modName = DustUtils.getMsgVal(DustCommAtts.RemoteRefModuleName, false);
            String itemID = DustUtils.getMsgVal(DustCommAtts.RemoteRefItemModuleId, false);
            
            Set<DustEntity> refs = DustPersistence.getEntityFromUnit(modName, itemID);
            
            DustPersistentStorageJsonSingle st = new DustPersistentStorageJsonSingle(null);
            st.writer = DustUtils.getMsgVal(DustGenericAtts.StreamWriter, false);
            
            DustPersistence.commit(st, refs);
         }
    }

}
