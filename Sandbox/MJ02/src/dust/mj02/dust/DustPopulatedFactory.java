package dust.mj02.dust;

import dust.mj02.dust.knowledge.DustCommComponents.DustCommLinks;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsFactory;

public abstract class DustPopulatedFactory<ValType> extends DustUtilsFactory<String, ValType> implements DustComponents, DustGenericComponents {
    protected final DustEntity center;
    protected final DustEntity linkType;
    protected final boolean leftCenter;

    protected final RefKey refKey;

    public DustPopulatedFactory(DustEntity center, DustEntity linkType, boolean leftCenter) {
        super(true);

        this.leftCenter = leftCenter;
        this.center = center;
        this.linkType = linkType;
        this.refKey = leftCenter ? RefKey.target : RefKey.source;

        DustEntity eSrc = leftCenter ? center : null;
        DustEntity eTrg = leftCenter ? null : center;

        Dust.processRefs(new RefProcessor() {
            @Override
            public void processRef(DustRef ref) {
                DustEntity e = ref.get(refKey);
                String id = DustUtils.accessEntity(DataCommand.getValue, e, DustUtils.optResolve(DustGenericAtts.IdentifiedIdLocal));
                put(id, createForRef(id, e));
            }
        }, eSrc, linkType, eTrg);
    }
    
    public DustPopulatedFactory(DustEntity center, Object linkType, boolean leftCenter) {
        this(center, DustUtils.optResolve(linkType), leftCenter);
    }


    protected abstract ValType createForRef(String id, DustEntity other);

    public static class Entity extends DustPopulatedFactory<DustEntity> {

        protected final DustEntity itemType;
        protected DustEntity unit;

        public Entity(DustEntity center, DustEntity linkType, boolean leftCenter, DustEntity itemType) {
            super(center, linkType, leftCenter);

            this.itemType = itemType;
        }
        public Entity(DustEntity center, Object linkType, boolean leftCenter, Object itemType) {
            this(center, DustUtils.optResolve(linkType), leftCenter, DustUtils.optResolve(itemType));
        }

        public void setUnit(DustEntity unit) {
            this.unit = unit;
        }
        
        @Override
        protected final DustEntity createForRef(String id, DustEntity other) {
            return other;
        }

        @Override
        protected final DustEntity create(String key, Object... hints) {
            DustEntity e = DustUtils.accessEntity(DataCommand.getEntity, itemType);
            DustUtils.accessEntity(DataCommand.setValue, e, DustGenericAtts.IdentifiedIdLocal, key);

            DustEntity eSrc = leftCenter ? center : e;
            DustEntity eTrg = leftCenter ? e : center;

            DustUtils.accessEntity(DataCommand.setRef, eSrc, linkType, eTrg);
            
            if ( null != unit ) {
                DustUtils.accessEntity(DataCommand.setRef, e, DustCommLinks.PersistentContainingUnit, unit);
            }

            postProcessNewItem(key, e);

            return e;
        }

        protected void postProcessNewItem(String key, DustEntity item) {

        }

    }

}
