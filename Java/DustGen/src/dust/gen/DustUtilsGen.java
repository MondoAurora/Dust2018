package dust.gen;

import java.util.regex.Matcher;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.pub.Dust;
import dust.pub.DustComponents;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

public class DustUtilsGen implements DustComponents, DustKnowledgeMetaComponents {
	
	public class EntityWrapper implements DustEntityWrapper, DustEntityAttribute, DustEntityLink {
		private final Enum<?> wrappedEnum;
		private DustEntity entity;

		public EntityWrapper(Enum<?> wrappedEnum) {
			this.wrappedEnum = wrappedEnum;
		}
		
		@Override
		public DustEntity entity() {
			if ( null == entity ) {
				IdResolverResult idr = FACT_ENUM_RESOLVER.get(wrappedEnum);
				DustEntity eType = Dust.getEntity(null, idr.getTypeId(), null);
				entity = Dust.getEntity(eType, idr.getStoreId(), null);
			}
			
			return entity;
		}

		@Override
		public void process(DustEntity entity, DustRefVisitor proc) {
			Dust.processRefs(proc, entity, entity());
		}

		@Override
		public DustEntity get(DustEntity entity, boolean createIfMissing, Object key) {
			return Dust.getRefEntity(entity, createIfMissing, entity(), key);
		}

		@Override
		public DustEntity modify(DustEntity entity, DustRefCommand cmd, DustEntity target, Object key) {
			return Dust.modifyRefs(cmd, entity, entity(), target, key);
		}

		@Override
		public <ValType> ValType getValue(DustEntity entity) {
			return Dust.getAttrValue(entity, entity());
		}

		@Override
		public void setValue(DustEntity entity, Object value) {
			Dust.setAttrValue(entity, entity(), value);
		}
		
		
	}
	
	public interface IdResolverResult {
		public String getStoreId();
		public String getTypeId();
	}
	
	private enum MetaPrefix {
		DustType(DustTypeKnowledgeMeta.Type), 
		DustAttribute(DustTypeKnowledgeMeta.AttDef, true), 
		DustLink(DustTypeKnowledgeMeta.LinkDef, true), 
		DustService(DustTypeKnowledgeMeta.Service), 
		DustCommand(DustTypeKnowledgeMeta.Command, true), 
		DustConst(DustTypeKnowledgeMeta.Const), 
		unknown(null);

		private final boolean child;
		private final Enum<?> metaEnum;
		
		
		private MetaPrefix(Enum<?> metaEnum, boolean child) {
			this.child = child;
			this.metaEnum = metaEnum;
		}

		private MetaPrefix(Enum<?> metaEnum) {
			this(metaEnum, false);
		}

		public static MetaPrefix fromName(String name) {
			for ( MetaPrefix mp : values() ) {
				if ( name.startsWith(mp.name())) {
					return mp;
				}
			}
			return unknown;
		}
		
		public String chopFrom(String name) {
			return name.substring(name().length());
		}
		
		public boolean isChild() {
			return child;
		}
		
		public Enum<?> getMetaEnum() {
			return metaEnum;
		}
	}
	
	private static final class EnumNames implements IdResolverResult {
		private final String typeId;
		private final String storeId;
		
		private EnumNames(Enum<?> e, boolean typeType) {
			Class<?> ec = e.getClass();
			
			String cName = ec.getSimpleName();
			MetaPrefix mp = MetaPrefix.fromName(cName);
			cName = mp.chopFrom(cName);
			
			String n = ec.getEnclosingClass().getSimpleName();
			String envName = "";
			Matcher m = PTRN_OWNER_FINDER.matcher(n);
			StringBuilder sb = null;
			if (m.matches()) {
				envName = m.group(1);
				Matcher rm = PTRN_OWNER_SPLITTER.matcher(envName);
				while (rm.find()) {
					sb = DustUtilsJava.sbApend(sb, SEP_PATH, false, rm.group());
				} 
			}
			sb = DustUtilsJava.sbApend(sb, SEP_PATH, false, cName.substring(envName.length()));
			sb = DustUtilsJava.sbApend(sb, mp.isChild() ? SEP_ATT : SEP_PATH, false, e.name());
			
			storeId = sb.toString();
			typeId = typeType ? storeId : FACT_ENUM_RESOLVER.get(mp.getMetaEnum()).storeId;
		}
		
		public String getStoreId() {
			return storeId;
		}
		public String getTypeId() {
			return typeId;
		}
		
		@Override
		public String toString() {
			return "{ " + typeId + ": " + storeId + " }";
		}
	}
	
	private static final DustUtilsFactory<Enum<?>, EnumNames> FACT_ENUM_RESOLVER = new DustUtilsFactory<Enum<?>, DustUtilsGen.EnumNames>(false) {
		@Override
		protected EnumNames create(Enum<?> key, Object... hints) {
			return new EnumNames(key, false);
		}
	};
	
	public static IdResolverResult resolveEnum(Enum<?> e) {
		return FACT_ENUM_RESOLVER.get(e);
	}
	
	static {
		FACT_ENUM_RESOLVER.put(DustTypeKnowledgeMeta.Type, new EnumNames(DustTypeKnowledgeMeta.Type, true));
	}
	
	public static void main(String[] args) {
		IdResolverResult r = resolveEnum(DustConstKnowledgeMetaAttrType.Bool);
		if ( r.getStoreId().isEmpty() ) {
			return;
		}
	}
}
