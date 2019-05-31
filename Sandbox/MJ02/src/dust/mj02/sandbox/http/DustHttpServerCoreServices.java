package dust.mj02.sandbox.http;

import java.util.HashSet;
import java.util.Set;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.sandbox.persistence.DustPersistence;
import dust.mj02.sandbox.persistence.DustPersistenceComponents;
import dust.mj02.sandbox.persistence.DustPersistentStorageJsonSingle;
import dust.utils.DustUtilsJava;

public interface DustHttpServerCoreServices extends DustHttpComponents {
    
    public static DustEntity getUnit(Object key) {
        String name = (key instanceof Enum) ? ((Enum<?>) key).name() : (String) key;

        DustEntity eu = DustUtils.accessEntity(DataCommand.getEntity, DustCommTypes.Unit, null, name, new EntityProcessor() {
            @Override
            public void processEntity(DustEntity entity) {
                DustPersistence.update(DustPersistenceComponents.PERS_STORAGE_DEF_MULTI, name);
            }
        });

        return eu;
    }

    
    public static class HttpServletGetRef implements DustProcComponents.DustProcPocessor {
        
        @Override
        public void processorProcess() throws Exception {
            String modName = DustUtils.getMsgVal(DustCommAtts.RemoteRefModuleName, false);
            String itemID = DustUtils.getMsgVal(DustCommAtts.RemoteRefItemModuleId, false);
            
            Set<DustEntity> refs = new HashSet<>();
            
            if ( !DustUtilsJava.isEmpty(modName) && !DustUtilsJava.isEmpty(itemID) ) {
                DustEntity eu = getUnit(modName);
                
                Dust.processRefs(new RefProcessor() {
                    
                    @Override
                    public void processRef(DustRef ref) {
                        DustEntity t = ref.get(RefKey.target);
                        Object ui = DustUtils.accessEntity(DataCommand.getValue, t, DustCommAtts.PersistentEntityId);
                        if ( itemID.equals(ui)) {
                            refs.add(t);
                        }
                    }
                }, eu, EntityResolver.getEntity(DustCommLinks.UnitEntities), null);
            }
            
            DustPersistentStorageJsonSingle st = new DustPersistentStorageJsonSingle(null);
            st.writer = DustUtils.getMsgVal(DustGenericAtts.StreamWriter, false);
            
            DustPersistence.commit(st, refs);
         }
    }

}
