package dust.mj02.sandbox.persistence;

import java.util.EnumMap;
import java.util.Map;

import dust.mj02.dust.knowledge.DustKernelComponents;
import dust.utils.DustUtilsFactory;

@SuppressWarnings({"unchecked", "rawtypes"})
public interface DustPersistenceComponents extends DustKernelComponents {
    String EXT_JSON = ".json";

    interface PersistentStorage extends DustProcActive {
        void save(String commitId, Map<String, Map> result) throws Exception;
        Map<String, Map> load(String unitId, String commitId) throws Exception;
    }
    
    PersistentStorage PERS_STORAGE_DEF_MULTI = DustPersistentStorageJsonMulti.DEFAULT;
    PersistentStorage PERS_STORAGE_DEF_SINGLE = DustPersistentStorageJsonSingle.DEFAULT;

    
    enum ContextKeys {
        /* @formatter:off */
        header(null), data(null), refUnits(null), keyTypes(null), unitCommitId(null), ThisUnit(null),
        
        CommitId(DustCommAtts.PersistentCommitId), 
        EntityUnit(DustCommLinks.PersistentContainingUnit), 
        EntityId(DustCommAtts.PersistentEntityId), 
        LocalId(DustGenericAtts.IdentifiedIdLocal), 
        PrimaryType(DustDataLinks.EntityPrimaryType), 
        NativeId(DustProcAtts.NativeBoundId), 
        VariantType(DustDataLinks.VariantValueType), 
        VariantVal(DustDataAtts.VariantValue)
        
        ;
        /* @formatter:on */

        Object key;

        private ContextKeys(Object key) {
            this.key = key;
        }

        public void put(Map m, Object value) {
            m.put(name(), value);
        }

        public <RetType> RetType get(Map m) {
            return (RetType) m.get(name());
        }

        public static void initMap(EnumMap<ContextKeys, String> ctxKeys,
                DustUtilsFactory<DustEntity, String> fact) {
            for (ContextKeys ck : values()) {
                if (null != ck.key) {
                    ctxKeys.put(ck, fact.get(EntityResolver.getEntity(ck.key)));
                }
            }
        }
    }
}
