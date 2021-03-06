package dust.mj02.dust;

import java.util.HashMap;
import java.util.Map;

import dust.mj02.dust.java.DustJavaComponents;
import dust.mj02.dust.knowledge.DustKernelComponents;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsJava;

public class DustTempHacks extends DustUtils implements DustKernelComponents {

    public static Map<String, Object> loadAtts(Map<String, Object> mapAtts, DustEntity from, Object... types) {
        if (null == mapAtts) {
            mapAtts = new HashMap<>();
        } else {
            mapAtts.clear();
        }

        if (DustUtilsJava.isEmpty(types)) {
            types = new Object[] {
                    ((DustRef) DustUtils.accessEntity(DataCommand.getValue, from, DustDataLinks.EntityPrimaryType)).get(RefKey.target) };
        }

        Map<String, Object> mt = mapAtts;

        for (int i = 0; i < types.length; ++i) {
            Dust.processRefs(new RefProcessor() {
                @Override
                public void processRef(DustRef ref) {
                    DustEntity eAttDef = ref.get(RefKey.target);
                    mt.put(DustUtils.accessEntity(DataCommand.getValue, eAttDef, DustUtils.optResolve(DustGenericAtts.IdentifiedIdLocal)),
                            DustUtils.accessEntity(DataCommand.getValue, from, eAttDef));
                }
            }, optResolve(types[i]), DustUtils.optResolve(DustMetaLinks.TypeAttDefs), null);
        }

        return mapAtts;
    }

    public static DustEntity loadFromEnum(Object key) {
        DustEntity e = null;

        if (key instanceof DustEntityKey) {
            // so that all enums will have their entity without problem
            String cn = key.getClass().getName();
            String en = ((Enum<?>) key).name();
            String kk = cn + ":" + en;

            e = EntityResolver.register(kk, key);

            DustUtils.accessEntity(DataCommand.setValue, e, DustGenericAtts.IdentifiedIdLocal, en);
            DustUtils.accessEntity(DataCommand.setValue, e, DustProcAtts.NativeBoundId, kk);

            DustMetaTypes mt = DustMetaTypes.getMetaTypeHack(cn);

            if (null != mt) {
                DustUtils.accessEntity(DataCommand.setRef, e, DustDataLinks.EntityPrimaryType, mt);
            }
        }

        return e;
    }

    public static void detectMetaConnections() {
        DustEntity eJava = EntityResolver.getEntity(DustJavaComponents.DustJavaTypes.JavaItem);

        Dust.processEntities(new EntityProcessor() {
            @Override
            public void processEntity(DustEntity entity) {
                if (eJava == DustUtils.toEntity(DustUtils.accessEntity(DataCommand.getValue, entity, DustDataLinks.EntityPrimaryType))) {
                    Class<?> cc = DustUtils.accessEntity(DataCommand.getValue, entity, DustJavaComponents.DustJavaAtts.JavaItemObj);

                    if (DustEntityKey.class.isAssignableFrom(cc)) {
                        DustUtilsDev.dump(cc.getCanonicalName());

                        DustMetaTypes mt = DustMetaTypes.getMetaTypeHack(cc.getName());

                        if (null != mt) {
                            String parentKeyName = null;
                            Object parentLink = null;

                            switch (mt) {
                            case AttDef:
                                parentKeyName = mt.replacePostfix(cc.getName(), DustMetaTypes.Type);
                                parentLink = DustMetaLinks.AttDefParent;
                                break;
                            case LinkDef:
                                parentKeyName = mt.replacePostfix(cc.getName(), DustMetaTypes.Type);
                                parentLink = DustMetaLinks.LinkDefParent;
                                break;
                            case Command:
                                parentKeyName = mt.replacePostfix(cc.getName(), DustMetaTypes.Service);
                                parentLink = DustGenericLinks.ConnectedOwner;
                                break;
                            case Constant:
                                break;
                            case Service:
                                break;
                            case Type:
                                // parentKeyName = mt.replacePostfix(cc.getCanonicalName(), DustMetaTypes.Unit);
                                break;
                            default:
                                break;
                            }

                            Class<?> parentKey;
                            try {
                                parentKey = (null != parentKeyName) ? Class.forName(parentKeyName) : null;
                            } catch (ClassNotFoundException e1) {
                                Dust.wrapAndRethrowException("parent key not found", e1);
                                parentKey = null;
                            }

                            for (Object e : cc.getEnumConstants()) {
                                DustEntity de = EntityResolver.getEntity(e);
                                DustUtils.accessEntity(DataCommand.setRef, de, DustDataLinks.EntityPrimaryType, mt);
                                DustUtils.accessEntity(DataCommand.setRef, de, DustDataLinks.EntityModels, DustGenericTypes.Identified);
                                DustUtils.accessEntity(DataCommand.setRef, de, DustDataLinks.EntityModels, DustDataTypes.Entity);

                                String itemName = e.toString();
                                DustUtils.accessEntity(DataCommand.setValue, de, DustGenericAtts.IdentifiedIdLocal, itemName);

                                if (null != parentKey) {
                                    int parentNameMatchLength = 0;
                                    Object parentEnum = null;
                                    for (Object ep : parentKey.getEnumConstants()) {
                                        String parentName = ep.toString();
                                        int pl = parentName.length();
                                        if (itemName.startsWith(parentName) && (pl > parentNameMatchLength)) {
                                            parentEnum = ep;
                                            parentNameMatchLength = pl;
                                        }
                                    }

                                    if (null == parentEnum) {
                                        DustUtilsDev.dump("missing parent for", e);
                                    } else {
                                        DustUtils.accessEntity(DataCommand.setRef, de, parentLink, parentEnum);                                        
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
