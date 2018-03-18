package dust.runtime.simple;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import dust.gen.dust.aaa.DustAaaComponents;
import dust.gen.dust.base.DustBaseServices;
import dust.gen.dust.meta.DustMetaComponents;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public interface DustSimpleRuntimeComponents
		extends DustBootComponents, DustBaseServices, DustMetaComponents, DustAaaComponents {
	Set<SimpleRef> NO_REFS = Collections.emptySet();

	class SimpleAttDef {
		SimpleType type;

		DustAttribute id;
		DustMetaAttrType fldType;

		public SimpleAttDef(SimpleType type, DustAttribute key) {
			this.type = type;
			this.id = key;
		}

		@Override
		public String toString() {
			return id.toString();
		}

		public DustMetaAttrType getAttrType() {
			return fldType;
		}
	}

	class SimpleLinkDef {
		SimpleType ownerType;

		DustLink link;
		DustMetaLinkType linkType;
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

		public DustMetaLinkType getLinkType() {
			return linkType;
		}

		public DustType getTargetType() {
			return (null == targetType) ? null : targetType.getType();
		}
	}

	class SimpleType {
		Enum<?> id;
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

		public SimpleType(Enum<?> key) {
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
	}

	class SimpleModel {
		SimpleType type;
		Map<SimpleAttDef, Object> values = new HashMap<>();

		public SimpleModel(SimpleType type) {
			this.type = type;
		}

		public String toString() {
			return type.toString();
		}

		@SuppressWarnings("unchecked")
		public <ValType> ValType getFieldValue(SimpleAttDef att) {
			return (ValType) values.get(att);
		}

		public void setFieldValue(SimpleAttDef att, Object value) {
			values.put(att, value);
		}
	}

	class SimpleEntity implements DustEntity {
		private DustSimpleManagerData ctx;
		private DustEntityState state;

		private SimpleType type;
		private DustUtilsFactory<SimpleType, SimpleModel> factModels = new DustUtilsFactory<SimpleType, SimpleModel>(
				false) {
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

		void setState(DustEntityState state) {
			this.state = state;
		}

		DustSimpleManagerData getCtx() {
			return ctx;
		}

		public DustEntityState getState() {
			return state;
		}

		public String toString() {
			return type.toString();
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
