package dust.runtime.simple;

import java.util.HashMap;
import java.util.Map;

import org.omg.CORBA.CTX_RESTRICT_SCOPE;

import dust.gen.dust.base.DustBaseServices;
import dust.gen.dust.meta.DustMetaComponents;
import dust.pub.boot.DustBootComponents;
import dust.utils.DustUtilsFactory;

public interface DustSimpleRuntimeComponents extends DustBootComponents, DustBaseServices, DustMetaComponents {
	
	class SimpleAttribute {
		SimpleType type;
		
		DustBaseAttribute id;
		DustAttrType fldType;

		public SimpleAttribute(SimpleType type, DustBaseAttribute key) {
			this.type = type;
			this.id = key;
		}

		@Override
		public String toString() {
			return id.toString();
		}
		
		public DustAttrType getAttrType() {
			return fldType;
		}
	}

	class SimpleLinkDef {
		SimpleType type;
		
		DustBaseLink id;
		DustAttrType fldType;
		SimpleLinkDef backRef;

		public SimpleLinkDef(SimpleType type, DustBaseLink key) {
			this.type = type;
			this.id = key;
		}

		@Override
		public String toString() {
			return id.toString();
		}
		
		public DustAttrType getAttrType() {
			return fldType;
		}
	}

	class SimpleType extends DustUtilsFactory<DustBaseAttribute, SimpleAttribute> {
		Enum<?> id;
		SimpleEntity entity;

		public SimpleType(Enum<?> key) {
			super(true);

			this.id = key;
		}
		
		public SimpleEntity getEntity() {
			return entity;
		}

		@Override
		protected SimpleAttribute create(DustBaseAttribute key, Object... hints) {
			return new SimpleAttribute(this, key);
		}

	}

	class SimpleModel {
		SimpleType type;
		Map<SimpleAttribute, Object> values = new HashMap<>();

		public SimpleModel(SimpleType type) {
			this.type = type;
		}

		public String toString() {
			return type.toString();
		}

		@SuppressWarnings("unchecked")
		public <ValType> ValType getFieldValue(SimpleAttribute att) {
			return (ValType) values.get(att);
		}

		public void setFieldValue(SimpleAttribute att, Object value) {
			values.put(att, value);
		}
	}

	class SimpleEntity implements DustBaseEntity {
		private DustSimpleContext ctx;
		private DustEntityState state;

		private SimpleType type;
		private DustUtilsFactory<SimpleType, SimpleModel> factModels = new DustUtilsFactory<SimpleType, SimpleModel>(
				false) {
			@Override
			protected SimpleModel create(SimpleType key, Object... hints) {
				return new SimpleModel(key);
			}
		};

		public SimpleEntity(DustSimpleContext ctx, SimpleType type) {
			this.ctx = ctx;
			this.type = type;
		}

		void setState(DustEntityState state) {
			this.state = state;
		}

		DustSimpleContext getCtx() {
			return ctx;
		}

		public DustEntityState getState() {
			return state;
		}

		public String toString() {
			return type.toString();
		}

		public <ValType> ValType getFieldValue(SimpleAttribute att) {
			SimpleModel m = factModels.peek(att.type);
			return (null == m) ? null : m.getFieldValue(att);
		}

		public void setFieldValue(SimpleAttribute att, Object value) {
			SimpleType tt = ((SimpleAttribute) att).type;
			SimpleModel m = (null == value) ? factModels.peek(tt) : factModels.get(tt);

			if (null != m) {
				m.setFieldValue(att, value);
			}
		}
	}
}
