package dust.runtime.simple;

import java.util.HashMap;
import java.util.Map;

import dust.gen.base.DustBaseServices;
import dust.pub.DustRuntimeComponents;
import dust.utils.DustUtilsFactory;

public interface DustSimpleRuntimeComponents extends DustRuntimeComponents, DustBaseServices {
	
	enum DustEntityState {
		esTemporal, esInSync, esRefChanged, esChanged, esConstructed, esDestructed
	}

	enum DustAttrType {
		fldId, fldInt, fldFloat, fldBool;
	}

	enum DustLinkType {
		linkSingle, linkSet, linkArray;
	}
	
	String IDSEP = ".";

	class SimpleField implements DustAttrDef {
		SimpleType type;
		
		String id;
		DustAttrType fldType;
		SimpleField revField;

		public SimpleField(SimpleType type, String key) {
			this.type = type;
			this.id = key;
		}

		@Override
		public String toString() {
			return id;
		}
		
		
		public DustAttrType getAttrType() {
			return fldType;
		}
	}

	class SimpleType extends DustUtilsFactory<String, SimpleField> {
		String id;

		public SimpleType(String key) {
			super(true);

			this.id = key;
		}

		@Override
		protected SimpleField create(String key, Object... hints) {
			return new SimpleField(this, key);
		}

		@Override
		public String toString() {
			return id;
		}
	}

	class SimpleModel {
		SimpleType type;
		Map<DustAttrDef, Object> values = new HashMap<>();

		public SimpleModel(SimpleType type) {
			this.type = type;
		}

		public String toString() {
			return type.toString();
		}

		@SuppressWarnings("unchecked")
		public <ValType> ValType getFieldValue(DustAttrDef field) {
			return (ValType) values.get(field);
		}

		public void breakRef(DustAttrDef field, SimpleEntity ref) {
			ref.setState(DustEntityState.esDestructed);
		}

		public void setFieldValue(DustAttrDef field, Object value) {
			values.put(field, value);
		}
	}

	class SimpleEntity implements DustEntity {
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

		public <ValType> ValType getFieldValue(DustAttrDef field) {
			SimpleModel m = factModels.peek(((SimpleField) field).type);
			return (null == m) ? null : m.getFieldValue(field);
		}

		public void setFieldValue(DustAttrDef field, Object value) {
			SimpleType tt = ((SimpleField) field).type;
			SimpleModel m = (null == value) ? factModels.peek(tt) : factModels.get(tt);

			if (null != m) {
				m.setFieldValue(field, value);
			}
		}
	}
}
