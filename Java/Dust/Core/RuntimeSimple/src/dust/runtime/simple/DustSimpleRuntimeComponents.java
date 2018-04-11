package dust.runtime.simple;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.gen.runtime.access.DustRuntimeAccessComponents;
import dust.pub.DustPubComponents;
import dust.pub.DustUtils;
import dust.pub.DustUtilsJava;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public interface DustSimpleRuntimeComponents extends DustBootComponents, DustKnowledgeInfoComponents,
		DustKnowledgeMetaComponents, DustRuntimeAccessComponents, DustKnowledgeProcComponents, DustPubComponents {
	Set<SimpleRef> NO_REFS = Collections.emptySet();

	class InfoModel implements DumpFormatter {
		SimpleType type;
		Map<SimpleAttDef, Object> values = new HashMap<>();

		public InfoModel(SimpleType type) {
			this.type = type;
		}

		@SuppressWarnings("unchecked")
		public <ValType> ValType getFieldValue(SimpleAttDef att) {
			return (ValType) values.get(att);
		}

		public void setFieldValue(SimpleAttDef att, Object value) {
			values.put(att, value);
		}

		public String toString() {
			return DustUtilsJava.toStringBuilder(null, values.entrySet(), true, null).toString();
		}
	}

	abstract class InfoEntity implements DustEntity, DumpFormatter {
		private DustSimpleManagerData ctx;
		private DustConstKnowledgeInfoEntityState state;

		private DustUtilsFactory<SimpleType, InfoModel> factModels = new DustUtilsFactory<SimpleType, InfoModel>(
				false, "Models") {
			@Override
			protected InfoModel create(SimpleType key, Object... hints) {
				return new InfoModel(key);
			}
		};
		private Set<SimpleRef> refs = NO_REFS;

		public InfoEntity(DustSimpleManagerData ctx) {
			this.ctx = ctx;
		}

		public DustConstKnowledgeInfoEntityState getState() {
			return state;
		}

		void setState(DustConstKnowledgeInfoEntityState state) {
			this.state = state;
		}

		DustSimpleManagerData getCtx() {
			return ctx;
		}

		public SimpleType getType() {
			return null;
		}
		
		public String toString() {
			return toString(true);
		}

		public String toString(boolean withContent) {
			StringBuilder sb = DustUtilsJava.sbApend(null, "", false, "{ \"Entity\": \"", hashCode(),
					"\", \"primaryType\": \"", DustUtils.toString(getType()), "\"");

			if (withContent) {
				sb.append(", ");
				factModels.toStringBuilder(sb);
				sb.append(", ");
				DustUtilsJava.toStringBuilder(sb, refs, false, "Refs");
			}
			sb.append(" }");

			return sb.toString();
		}

		public <ValType> ValType getFieldValue(SimpleAttDef att) {
			InfoModel m = factModels.peek(att.getOwner());
			return (null == m) ? null : m.getFieldValue(att);
		}

		public void setFieldValue(SimpleAttDef att, Object value) {
			SimpleType tt = ((SimpleAttDef) att).getOwner();
			InfoModel m = (null == value) ? factModels.peek(tt) : factModels.get(tt);

			if (null != m) {
				m.setFieldValue(att, value);
			}
		}

		Set<SimpleRef> getRefs(boolean createIfMissing) {
			if ((NO_REFS == refs) && (createIfMissing)) {
				refs = new TreeSet<>();
			}

			return refs;
		}
	}
	
	class InfoEntityData extends InfoEntity {
		SimpleType type;

		public InfoEntityData(DustSimpleManagerData ctx, SimpleType type) {
			super(ctx);
			this.type = type;
		}
		
		public SimpleType getType() {
			return type;
		}
	}
	
	class SimpleType extends InfoEntity implements DustType {
		String id;

		DustUtilsFactory<String, SimpleAttDef> factAtts = new DustUtilsFactory<String, SimpleAttDef>(
				false) {
			@Override
			protected SimpleAttDef create(String key, Object... hints) {
				return new SimpleAttDef(getCtx(), SimpleType.this, key);
			}
		};

		DustUtilsFactory<String, SimpleLinkDef> factLinks = new DustUtilsFactory<String, SimpleLinkDef>(false) {
			@Override
			protected SimpleLinkDef create(String key, Object... hints) {
				return new SimpleLinkDef(getCtx(), SimpleType.this, key);
			}
		};

		public SimpleType(DustSimpleManagerData ctx, String id) {
			super(ctx);
			this.id = id;
		}

		SimpleAttDef getAttDef(String att) {
			return factAtts.get(att);
		}

		SimpleLinkDef getLinkDef(String link) {
			return factLinks.get(link);
		}
		
		@Override
		public String toString() {
			return id;
		}
	}
	
	class SimpleService extends InfoEntity implements DustService {
		String id;

		DustUtilsFactory<String, SimpleCommand> factCommands = new DustUtilsFactory<String, SimpleCommand>(
				false) {
			@Override
			protected SimpleCommand create(String key, Object... hints) {
				return new SimpleCommand(getCtx(), SimpleService.this, key);
			}
		};

		public SimpleService(DustSimpleManagerData ctx, String id) {
			super(ctx);
			this.id = id;
		}

		SimpleCommand getCommand(String id) {
			return factCommands.get(id);
		}

		@Override
		public String toString() {
			return id;
		}
	}
	
	abstract class MetaEntity<OwnerType> extends InfoEntity {
		OwnerType owner;
		String defId;

		public MetaEntity(DustSimpleManagerData ctx, OwnerType owner, String defId) {
			super(ctx);
			this.owner = owner;
			this.defId = defId;
		}
		
		public OwnerType getOwner() {
			return owner;
		}
	}
	
	class SimpleAttDef extends MetaEntity<SimpleType> implements DustAttribute {
		DustConstKnowledgeMetaAttrType fldType;		
	
		public SimpleAttDef(DustSimpleManagerData ctx, SimpleType owner, String defId) {
			super(ctx, owner, defId);
		}

		@Override
		public String toString() {
			return defId;
		}
		
		public DustConstKnowledgeMetaAttrType getAttrType() {
			return fldType;
		}
	}	
	
	class SimpleCommand extends MetaEntity<SimpleService> implements DustCommand {
		public SimpleCommand(DustSimpleManagerData ctx, SimpleService owner, String defId) {
			super(ctx, owner, defId);
		}

		@Override
		public String toString() {
			return defId;
		}
		
	}

	class SimpleLinkDef extends MetaEntity<SimpleType> implements DustLink {
		DustConstKnowledgeMetaCardinality cardinality;
		SimpleType targetType;
		SimpleLinkDef backRef;

		public SimpleLinkDef(DustSimpleManagerData ctx, SimpleType owner, String defId) {
			super(ctx, owner, defId);
		}
		
		@Override
		public String toString() {
			return defId;
		}

		public DustConstKnowledgeMetaCardinality getCardinality() {
			return cardinality;
		}

		public SimpleType getTargetType() {
			return targetType;
		}
	}

	class SimpleRef implements Comparable<SimpleRef> {
		SimpleLinkDef linkDef;
		Object key;
		SimpleRef reverse;

		InfoEntity eRef;
		InfoEntity eTarget;

		public SimpleRef(SimpleLinkDef linkDef, InfoEntity eTarget, Object key) {
			this.linkDef = linkDef;
			this.eTarget = eTarget;
			this.key = key;
		}

		@Override
		public int compareTo(SimpleRef o) {
			return hashCode() - o.hashCode();
		}

		public boolean match(SimpleLinkDef sld, InfoEntity target, Object key) {
			boolean t = (null == sld) || sld.equals(this.linkDef);
			boolean e = (null == target) || target.equals(this.eTarget);
			boolean k = (null == key) || key.equals(this.key);

			return t && e && k;
		}

		public void setTarget(InfoEntity target) {
			this.eTarget = target;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = DustUtilsJava.sbApend(null, "", false, "{ \"link\": \"", linkDef,
					"\", \"target\":", eTarget.toString(false));
			
			if ( null != key ) {
				sb.append(", \"key\":\"").append(key).append("\"");
			}
			
			sb.append("}");

			return sb.toString();
		}
	}

	
}
