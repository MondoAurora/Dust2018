package dust.runtime.simple;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import dust.gen.knowledge.info.DustKnowledgeInfoServices;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcServices;
import dust.gen.runtime.access.DustRuntimeAccessComponents;
import dust.pub.DustPubComponents;
import dust.pub.DustUtils;
import dust.pub.DustUtilsJava;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public interface DustSimpleRuntimeComponents extends DustBootComponents, DustKnowledgeInfoServices,
		DustKnowledgeMetaComponents, DustRuntimeAccessComponents, DustKnowledgeProcServices, DustPubComponents {
	Set<SimpleRef> NO_REFS = Collections.emptySet();

	class SimpleAttDef {
		SimpleType type;

		DustAttribute id;
		DustConstKnowledgeMetaAttrType fldType;

		public SimpleAttDef(SimpleType type, DustAttribute key) {
			this.type = type;
			this.id = key;
		}

		@Override
		public String toString() {
			return id.toString();
		}

		public DustConstKnowledgeMetaAttrType getAttrType() {
			return fldType;
		}
	}

	class SimpleLinkDef {
		SimpleType ownerType;

		DustLink link;
		DustConstKnowledgeMetaCardinality cardinality;
		SimpleType targetType;
		SimpleLinkDef backRef;

		public SimpleLinkDef(SimpleType type, DustLink link) {
			this.ownerType = type;
			this.link = link;
		}

		@Override
		public String toString() {
			return link.toString();
		}

		public DustConstKnowledgeMetaCardinality getCardinality() {
			return cardinality;
		}

		public DustType getTargetType() {
			return (null == targetType) ? null : targetType.getType();
		}
	}

	class SimpleType {
		DustType id;
		SimpleEntity entity;

		DustUtilsFactory<DustAttribute, SimpleAttDef> factAtts = new DustUtilsFactory<DustAttribute, SimpleAttDef>(
				false) {
			@Override
			protected SimpleAttDef create(DustAttribute key, Object... hints) {
				return new SimpleAttDef(SimpleType.this, key);
			}
		};

		DustUtilsFactory<DustLink, SimpleLinkDef> factLinks = new DustUtilsFactory<DustLink, SimpleLinkDef>(false) {
			@Override
			protected SimpleLinkDef create(DustLink key, Object... hints) {
				return new SimpleLinkDef(SimpleType.this, key);
			}
		};

		public SimpleType(DustType key) {
			this.id = key;
		}

		DustType getType() {
			return (DustType) id;
		}

		public SimpleEntity getEntity() {
			return entity;
		}

		SimpleAttDef getAttDef(DustAttribute att) {
			return factAtts.get(att);
		}

		SimpleLinkDef getLinkDef(DustLink link) {
			return factLinks.get(link);
		}
		
		@Override
		public String toString() {
			return id.toString();
		}
	}

	class SimpleRef implements Comparable<SimpleRef> {
		SimpleLinkDef linkDef;
		Object key;
		SimpleRef reverse;

		SimpleEntity eRef;
		SimpleEntity eTarget;

		public SimpleRef(SimpleLinkDef linkDef, SimpleEntity eTarget, Object key) {
			this.linkDef = linkDef;
			this.eTarget = eTarget;
			this.key = key;
		}

		@Override
		public int compareTo(SimpleRef o) {
			return hashCode() - o.hashCode();
		}

		public boolean match(SimpleLinkDef sld, SimpleEntity target, Object key) {
			boolean t = (null == sld) || sld.equals(this.linkDef);
			boolean e = (null == target) || target.equals(this.eTarget);
			boolean k = (null == key) || key.equals(this.key);

			return t && e && k;
		}

		public void setTarget(SimpleEntity target) {
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

	class SimpleModel implements DumpFormatter {
		SimpleType type;
		Map<SimpleAttDef, Object> values = new HashMap<>();

		public SimpleModel(SimpleType type) {
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

	class SimpleEntity implements DustEntity, DumpFormatter {
		private DustSimpleManagerData ctx;
		private DustConstKnowledgeInfoEntityState state;

		private SimpleType type;
		private DustUtilsFactory<SimpleType, SimpleModel> factModels = new DustUtilsFactory<SimpleType, SimpleModel>(
				false, "Models") {
			@Override
			protected SimpleModel create(SimpleType key, Object... hints) {
				return new SimpleModel(key);
			}
		};
		private Set<SimpleRef> refs = NO_REFS;

		public SimpleEntity(DustSimpleManagerData ctx, SimpleType type) {
			this.ctx = ctx;
			this.type = type;
		}

		@Override
		public DustType getType() {
			return type.getType();
		}

		void setState(DustConstKnowledgeInfoEntityState state) {
			this.state = state;
		}

		DustSimpleManagerData getCtx() {
			return ctx;
		}

		public DustConstKnowledgeInfoEntityState getState() {
			return state;
		}

		public String toString() {
			return toString(true);
		}

		public String toString(boolean withContent) {
			StringBuilder sb = DustUtilsJava.sbApend(null, "", false, "{ \"Entity\": \"", hashCode(),
					"\", \"primaryType\": \"", DustUtils.toString(type), "\"");

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
			SimpleModel m = factModels.peek(att.type);
			return (null == m) ? null : m.getFieldValue(att);
		}

		public void setFieldValue(SimpleAttDef att, Object value) {
			SimpleType tt = ((SimpleAttDef) att).type;
			SimpleModel m = (null == value) ? factModels.peek(tt) : factModels.get(tt);

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
}
