package dust.mj02.sandbox;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustKernelComponents;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings("rawtypes")
public class DustSandboxPersistence implements DustKernelComponents {

    private static final String PATH_PERSISTENCE = "output/persistence";
    private static final String PATH_HISTORY = "history";
    private static final String EXT_JSON = ".json";

    public enum TempUnit {
        /* @formatter:off */
		Meta(DustMetaTypes.class, DustMetaTags.class, DustMetaAttDefTypeValues.class, DustMetaLinkDefTypeValues.class), 
		Data(DustDataTypes.class, DustDataTags.class), 
		Proc(DustProcTypes.class, DustProcServices.class), 
		Comm(DustCommTypes.class, DustCommServices.class, DustCommTags.class), 
		Generic(DustGenericTypes.class), 
		Collection(DustCollectionTypes.class),

		;
		/* @formatter:on */
        private static boolean inited = false;
        private final Class<?>[] keys;

        private TempUnit(Class<?>... keys) {
            this.keys = keys;
        }

        public static void optInit() {
            if (!inited) {
                StringBuilder sb = null;
                for (TempUnit tu : values()) {

                    String un = tu.name();
                    DustEntity eu = getUnit(un);
                    sb = DustUtilsJava.sbAppend(sb, ",", false, un);

                    for (Class<?> c : tu.keys) {
                        for (Object e : c.getEnumConstants()) {
                            DustEntity ee = EntityResolver.getEntity(e);
                            DustUtils.accessEntity(DataCommand.setRef, ee,
                                    DustCommLinks.PersistentContainingUnit, eu);
                        }
                    }
                }

                inited = true;

                try {
                    update(sb.toString());
                } catch (Throwable t) {
                    DustUtilsDev.dump(
                            "Continue after failed to load kernel units - missing files?",
                            t);
                }
            }
        }
    }

    public static DustEntity getUnit(Object key) {
        String name = (key instanceof Enum) ? ((Enum<?>) key).name()
                : (String) key;

        DustEntity eu = DustUtils.accessEntity(DataCommand.getEntity,
                DustCommTypes.Unit, null, name, new EntityProcessor() {
                    @Override
                    public void processEntity(DustEntity entity) {
                        DustUtils.accessEntity(DataCommand.setValue, entity,
                                DustGenericAtts.IdentifiedIdLocal, name);
                        DustUtils.accessEntity(DataCommand.setValue, entity,
                                DustCommAtts.UnitNextEntityId, "0");
                    }
                });

        return eu;
    }

    static DustUtilsFactory<DustEntity, DustMetaLinkDefTypeValues> factLinkTypes = new DustUtilsFactory<DustEntity, DustMetaLinkDefTypeValues>(
            false) {
        @Override
        protected DustMetaLinkDefTypeValues create(DustEntity key,
                Object... hints) {
            if (EntityResolver.getEntity(DustMetaTypes.LinkDef) != DustUtils
                    .getByPath(key, DustDataLinks.EntityPrimaryType)) {
                return null;
            }

            DustMetaLinkDefTypeValues ldt = EntityResolver.getKey(
                    DustUtils.getByPath(key, DustMetaLinks.LinkDefType));
            return (null == ldt) ? DustMetaLinkDefTypeValues.LinkDefSingle
                    : ldt;
        }
    };

    static DustUtilsFactory<DustEntity, Boolean> factNotStoredFlag = new DustUtilsFactory<DustEntity, Boolean>(
            false) {
        @Override
        protected Boolean create(DustEntity key, Object... hints) {
            return DustUtils.tag(key, TagCommand.test,
                    DustCommTags.PersistentNotStored);
        }
    };

    @SuppressWarnings("unchecked")
    enum ContextKeys {
        /* @formatter:off */
        header(null), data(null), refUnits(null), unitCommitId(null), ThisUnit(null),
        
        CommitId(DustCommAtts.PersistentCommitId), 
        EntityUnit(DustCommLinks.PersistentContainingUnit), 
        EntityId(DustCommAtts.PersistentEntityId), 
        LocalId(DustGenericAtts.IdentifiedIdLocal), 
        PrimaryType(DustDataLinks.EntityPrimaryType), 
        NativeId(DustProcAtts.NativeBoundId)
        
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

    static class SaveContext {
        private static final SimpleDateFormat SDF = new SimpleDateFormat(
                "yyyyMMdd_HHmmss_SSS");

        String commitId = SDF.format(new Date());

        Set<DustEntity> refEntitiesWithNoUnit = new HashSet<>();

        class SaveUnitContext {
            EnumMap<ContextKeys, String> ctxKeys = new EnumMap<>(
                    ContextKeys.class);

            DustEntity myUnit;
            String myUnitId;
            Set<SaveUnitContext> refUnits = new HashSet<>();
            Set<SaveUnitContext> referringUnits = new HashSet<>();

            ArrayList<DustEntity> toSave = new ArrayList<>();

            long nextId;
            long nextRefId;
            String actualCommitId;

            public SaveUnitContext(DustEntity myUnit_) {
                this.myUnit = myUnit_;
                toSave.add(myUnit);

                // myUnitId = factEntityLocalId.get(myUnit);
                myUnitId = DustUtils.accessEntity(DataCommand.getValue, myUnit,
                        DustGenericAtts.IdentifiedIdLocal);
                ctxKeys.put(ContextKeys.ThisUnit, myUnitId);

                String ni = DustUtils.accessEntity(DataCommand.getValue, myUnit,
                        DustCommAtts.UnitNextEntityId);
                nextId = DustUtilsJava.isEmpty(ni) ? 0 : Long.parseLong(ni);
                nextRefId = -1;

                actualCommitId = DustUtils.accessEntity(DataCommand.getValue,
                        myUnit, DustCommAtts.PersistentCommitId);
                ContextKeys.initMap(ctxKeys, factEntityLocalId);
            }

            DustUtilsFactory<String, Map<String, Object>> factResults = new DustUtilsFactory<String, Map<String, Object>>(
                    false) {
                @Override
                protected Map<String, Object> create(String key,
                        Object... hints) {
                    return new HashMap<>();
                }
            };

            DustUtilsFactory<DustEntity, String> factEntityLocalId = new DustUtilsFactory<DustEntity, String>(
                    false) {
                @Override
                protected String create(DustEntity key, Object... hints) {
                    String persId;

                    if (myUnit == whereToSave(key)) {
                        persId = DustUtils.accessEntity(DataCommand.getValue,
                                key, DustCommAtts.PersistentEntityId);
                        if (DustUtilsJava.isEmpty(persId)) {
                            persId = DustUtilsJava.toString(nextId++);
                            DustUtils.accessEntity(DataCommand.setValue, key,
                                    DustCommAtts.PersistentEntityId, persId);
                        }

                        DustUtils.accessEntity(DataCommand.setRef, key,
                                DustCommLinks.PersistentContainingUnit, myUnit);

                        if (DustUtils.tag(key, TagCommand.test,
                                DustDataTags.EntityChanged)) {
                            DustUtils.accessEntity(DataCommand.setValue, key,
                                    DustCommAtts.PersistentCommitId, commitId);
                            setUpdated();
                            DustUtils.tag(key, TagCommand.clear,
                                    DustDataTags.EntityChanged);
                        }

                    } else {
                        persId = DustUtilsJava.toString(nextRefId--);
                    }

                    toSave.add(key);

                    return persId;
                }
            };

            public boolean isUpdated() {
                return DustUtilsJava.isEqual(actualCommitId, commitId);
            }

            void setUpdated() {
                if (!isUpdated()) {
                    actualCommitId = commitId;

                    for (SaveUnitContext suc : referringUnits) {
                        suc.setUpdated();
                    }
                }
            };

            void linkTo(DustEntity unit) {
                SaveUnitContext suc = factUnitCtx.get(unit);
                if (refUnits.add(suc)) {
                    suc.referringUnits.add(this);
                    if (suc.isUpdated()) {
                        setUpdated();
                    }
                    if (isUpdated()) {
                        suc.setUpdated();
                    }
                }
            };

            void optSaveEntity(DustEntity e) {
                DustEntity eSaveUnit = whereToSave(e);

                if (null != eSaveUnit) {
                    boolean local = myUnit == eSaveUnit;

                    if (!local) {
                        linkTo(eSaveUnit);

                        if (e == eSaveUnit) {
                            // if (refUnits.contains(e)) {
                            // referred units are saved externally
                            return;
                        }
                    }

                    String localId = (e == myUnit) ? myUnitId
                            : factEntityLocalId.get(e);

                    Map<String, Object> data = factResults.get(localId);

                    if (local) {
                        data.put(ctxKeys.get(ContextKeys.EntityId), localId);

                        Dust.accessEntity(DataCommand.processContent, e, null,
                                new ContentProcessor() {
                                    Object coll = null;

                                    @Override
                                    public void processContent(
                                            DustEntity eOwner, DustEntity eKey,
                                            Object value) {
                                        if (eKey == EntityResolver.getEntity(
                                                DustCommLinks.PersistentContainingUnit)) {
                                            return;
                                        }
                                        DustEntity uKey = whereToSave(eKey);

                                        if (uKey != myUnit) {
                                            linkTo(uKey);
                                        }
                                        String mapKey = factEntityLocalId
                                                .get(eKey);

                                        if (value instanceof DustRef) {
                                            DustMetaLinkDefTypeValues ldt = factLinkTypes
                                                    .get(((DustRef) value).get(
                                                            RefKey.linkDef));

                                            coll = DustUtils.getColl(ldt);

                                            if (null != coll) {
                                                data.put(mapKey, coll);
                                            }

                                            ((DustRef) value).processAll(
                                                    new RefProcessor() {
                                                        @SuppressWarnings("unchecked")
                                                        @Override
                                                        public void processRef(
                                                                DustRef ref) {
                                                            DustEntity eTarget = ref
                                                                    .get(RefKey.target);
                                                            if (factNotStoredFlag
                                                                    .get(eTarget)) {
                                                                DustUtilsDev
                                                                        .dump("Skip saving",
                                                                                e,
                                                                                eKey,
                                                                                eTarget);
                                                                return;
                                                            }

                                                            String refId = factEntityLocalId
                                                                    .get(eTarget);

                                                            switch (ldt) {
                                                            case LinkDefArray:
                                                            case LinkDefSet:
                                                                ((Collection<String>) coll)
                                                                        .add(refId);
                                                                break;
                                                            case LinkDefMap:
                                                                String keyId = factEntityLocalId
                                                                        .get(ref.get(
                                                                                RefKey.key));
                                                                ((Map<String, String>) coll)
                                                                        .put(keyId,
                                                                                refId);
                                                                break;
                                                            case LinkDefSingle:
                                                                data.put(mapKey,
                                                                        refId);
                                                                break;
                                                            default:
                                                                break;

                                                            }
                                                        }
                                                    });
                                        } else {
                                            data.put(mapKey,
                                                    (null == value) ? ""
                                                            : value);
                                        }
                                    }
                                }, null);
                    } else {
                        SaveUnitContext suc = factUnitCtx.get(eSaveUnit);
                        String entityId = suc.factEntityLocalId.get(e);

                        data.put(ctxKeys.get(ContextKeys.EntityId), entityId);
                        data.put(ctxKeys.get(ContextKeys.EntityUnit),
                                suc.myUnitId);
                        data.put(ctxKeys.get(ContextKeys.CommitId),
                                DustUtils.accessEntity(DataCommand.getValue, e,
                                        DustCommAtts.PersistentCommitId));
                    }
                }
            }

            @SuppressWarnings("unchecked")
            private void optSaveInto(Map<String, Map> ret) {
                String uid = DustUtils.accessEntity(DataCommand.getValue,
                        myUnit, DustGenericAtts.IdentifiedIdLocal);

                for (int i = 0; i < toSave.size(); ++i) {
                    optSaveEntity(toSave.get(i));
                }

                if (isUpdated()) {
                    String ni = DustUtilsJava.toString(nextId);
                    DustUtils.accessEntity(DataCommand.setValue, myUnit,
                            DustCommAtts.UnitNextEntityId, ni);
                    factResults.get(myUnitId)
                            .put(factEntityLocalId.get(EntityResolver
                                    .getEntity(DustCommAtts.UnitNextEntityId)),
                                    ni);

                    Map<String, Object> sm = new HashMap<>();
                    ctxKeys.put(ContextKeys.unitCommitId, actualCommitId);
                    DustUtils.accessEntity(DataCommand.setValue, myUnit,
                            DustCommAtts.PersistentCommitId, actualCommitId);

                    ContextKeys.header.put(sm, ctxKeys);
                    ContextKeys.data.put(sm, factResults.copyShallow(null));

                    DustUtilsFactory<String, Map> ru = new DustUtilsFactory.Simple<String, Map>(
                            false, HashMap.class);
                    for (SaveUnitContext suc : refUnits) {
                        Map m = ru.get(suc.myUnitId);
                        m.put(ctxKeys.get(ContextKeys.EntityId), suc.myUnitId);
                        m.put(ctxKeys.get(ContextKeys.CommitId),
                                suc.actualCommitId);
                    }
                    ContextKeys.refUnits.put(sm, ru.copyShallow(null));

                    ret.put(uid, sm);
                }
            }
        }

        ArrayList<SaveUnitContext> arrUctx = new ArrayList<>();

        DustUtilsFactory<DustEntity, SaveUnitContext> factUnitCtx = new DustUtilsFactory<DustEntity, SaveUnitContext>(
                false) {
            @Override
            protected SaveUnitContext create(DustEntity key, Object... hints) {
                SaveUnitContext suc = new SaveUnitContext(key);
                arrUctx.add(suc);
                return suc;
            }
        };

        DustEntity whereToSave(DustEntity e) {
            DustEntity tu = EntityResolver.getEntity(DustCommTypes.Unit);

            if (tu == DustUtils.getByPath(e, DustDataLinks.EntityPrimaryType)) {
                return e;
            }

            DustEntity eUnit = DustUtils
                    .toEntity(DustUtils.accessEntity(DataCommand.getValue, e,
                            DustCommLinks.PersistentContainingUnit));

            if (null == eUnit) {
                DustEntity ee = DustUtils.getByPath(e,
                        DustDataLinks.EntityPrimaryType,
                        DustCommLinks.PersistentStoreWith);

                if (null != ee) {
                    eUnit = whereToSave(DustUtils.getByPath(e, ee));
                }
            }

            if (null == eUnit) {
                refEntitiesWithNoUnit.add(e);
            }

            return eUnit;
        };

        public Map<String, Map> doSave() {
            Map<String, Map> ret = new HashMap<>();

            for (int u = 0; u < arrUctx.size(); ++u) {
                SaveUnitContext uctx = arrUctx.get(u);
                uctx.optSaveInto(ret);
            }

            return ret;
        }
    }

    public static void commit() {
        TempUnit.optInit();

        SaveContext svctx = new SaveContext();
        Map<String, Map> result;

        DustEntity tu = EntityResolver.getEntity(DustCommTypes.Unit);

        Dust.processEntities(new EntityProcessor() {
            @Override
            public void processEntity(DustEntity entity) {
                if (tu == DustUtils.getByPath(entity,
                        DustDataLinks.EntityPrimaryType)) {
                    svctx.factUnitCtx.get(entity);
                }
            }
        });

        result = svctx.doSave();

        saveToJson(result, svctx.commitId);
    }

    private static void saveToJson(Map<String, Map> result, String commitId) {
        File dirPers = new File(PATH_PERSISTENCE);
        File dirHistory = new File(dirPers, PATH_HISTORY);
        dirHistory.mkdirs();

        for (Map.Entry<String, Map> r : result.entrySet()) {
            FileWriter fw;
            String key = r.getKey();
            try {
                File file = new File(dirPers, key + EXT_JSON);
                fw = new FileWriter(file);
                JSONObject.writeJSONString(r.getValue(), fw);
                fw.flush();
                fw.close();

                Files.copy(file.toPath(),
                        new File(dirHistory,
                                key + "_" + commitId + EXT_JSON)
                                        .toPath());
            } catch (IOException e) {
                Dust.wrapAndRethrowException("Saving " + key, e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    static class LoadContext {
        class LoadUnitContext {
            String unitName;

            EnumMap<ContextKeys, String> ctxKeys = new EnumMap<>(
                    ContextKeys.class);
            Map inputData;
            Map refUnits;

            DustUtilsFactory<String, DustEntity> factEntities = new DustUtilsFactory<String, DustEntity>(
                    false) {
                @Override
                protected DustEntity create(String key, Object... hints) {
                    Map m = (Map) inputData.get(key);
                    String unitId = (String) m
                            .get(ctxKeys.get(ContextKeys.EntityUnit));
                    String entityId = (String) m
                            .get(ctxKeys.get(ContextKeys.EntityId));

                    boolean local = DustUtilsJava.isEmpty(unitId);
                    DustEntity ret = null;

                    if (DustUtilsJava.isEqual(unitName, key)) {
                        ret = getUnit(key);
                    } else {
                        if (local) {
                            String nativeId = (String) m
                                    .get(ctxKeys.get(ContextKeys.NativeId));
                            if (null == nativeId) {
                                DustEntity eUnit = getUnit(unitName);
                                FinderByAttValue fbav = new FinderByAttValue(
                                        DustCommAtts.PersistentEntityId, key);
                                DustUtils.accessEntity(DataCommand.processRef,
                                        eUnit, DustCommLinks.UnitEntities,
                                        fbav);

                                if (null == (ret = fbav.getFound())) {
                                    String typeRef = (String) m.get(ctxKeys
                                            .get(ContextKeys.PrimaryType));
                                    DustEntity ePT = (null == typeRef) ? null
                                            : factEntities.get(typeRef);
                                    ret = DustUtils.accessEntity(
                                            DataCommand.getEntity, ePT);
                                }
                            } else {
                                ret = Dust.getEntity(nativeId);
                            }
                        } else {
                            Map mapUnitRefInfo = (Map) refUnits.get(unitId);
                            String refUnitName = (String) mapUnitRefInfo
                                    .get(ctxKeys.get(ContextKeys.EntityId));

                            ret = factUctx.get(refUnitName).factEntities
                                    .get(entityId);
                        }
                    }

                    return ret;
                }

                @Override
                protected void initNew(DustEntity item, String key,
                        Object... hints) {
                    Map m = (Map) inputData.get(key);
                    String unitId = (String) m
                            .get(ctxKeys.get(ContextKeys.EntityUnit));
                    boolean local = DustUtilsJava.isEmpty(unitId);

                    if (local) {
                        for (Map.Entry de : (Iterable<Map.Entry>) m
                                .entrySet()) {
                            String keyId = (String) de.getKey();
                            DustEntity eKey = factEntities.get(keyId);

                            Object val = de.getValue();

                            if (val instanceof Collection) {
                                for (Object o : (Collection) val) {
                                    DustEntity eTarget = factEntities
                                            .get(DustUtilsJava.toString(o));
                                    DustUtils.accessEntity(DataCommand.setRef,
                                            item, eKey, eTarget);
                                }
                            } else {
                                DustMetaLinkDefTypeValues ldt = factLinkTypes
                                        .get(eKey);

                                if (null == ldt) {
                                    DustUtils.accessEntity(DataCommand.setValue,
                                            item, eKey, val);
                                } else {
                                    DustEntity eTarget = factEntities
                                            .get(DustUtilsJava.toString(val));
                                    DustUtils.accessEntity(DataCommand.setRef,
                                            item, eKey, eTarget);
                                }
                            }
                        }
                    }
                }
            };

            public LoadUnitContext(String unitName) {
                this.unitName = unitName;

                File uf = new File(PATH_PERSISTENCE, unitName + EXT_JSON);

                try {
                    Map jo = (JSONObject) parser.parse(new FileReader(uf));

                    Map m = ContextKeys.header.get(jo);
                    for (Map.Entry he : (Iterable<Map.Entry>) m.entrySet()) {
                        ctxKeys.put(ContextKeys.valueOf((String) he.getKey()),
                                (String) he.getValue());
                    }

                    inputData = ContextKeys.data.get(jo);
                    refUnits = ContextKeys.refUnits.get(jo);
                } catch (Exception e) {
                    Dust.wrapAndRethrowException("Loading unit " + unitName, e);
                }
            }

            public void load() {
                DustEntity eUnit = getUnit(unitName);
                DustUtils.accessEntity(DataCommand.setValue, eUnit,
                        DustCommAtts.PersistentEntityId,
                        ctxKeys.get(ContextKeys.unitCommitId));

                for (Object key : inputData.keySet()) {
                    DustEntity e = factEntities.get((String) key);
                    DustUtils.tag(e, TagCommand.clear,
                            DustDataTags.EntityChanged);
                }
            }
        }

        JSONParser parser = new JSONParser();
        DustUtilsFactory<String, LoadUnitContext> factUctx = new DustUtilsFactory<String, LoadUnitContext>(
                false) {
            @Override
            protected LoadUnitContext create(String key, Object... hints) {
                LoadUnitContext luc = new LoadUnitContext(key);
                arrCtx.add(luc);
                return luc;
            }
        };

        ArrayList<LoadUnitContext> arrCtx = new ArrayList<>();

        public void load() {
            for (int li = 0; li < arrCtx.size(); ++li) {
                arrCtx.get(li).load();
            }
        }
    }

    public static void update(String unitName) {
        TempUnit.optInit();
        LoadContext lctx = new LoadContext();

        for (String un : unitName.split(",")) {
            lctx.factUctx.get(un.trim());
        }

        lctx.load();
    }

}
