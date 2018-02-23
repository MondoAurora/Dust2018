package dust.runtime.simple;

import java.util.HashMap;
import java.util.Map;

import dust.pub.DustRuntimeComponents;

public interface DustSimpleRuntimeComponents extends DustRuntimeComponents {
	String IDSEP = ".";

	class SimpleField implements DustField {
		SimpleType type;
		String id;
		
		public SimpleField(SimpleType type, String key) {
			this.type = type;
			this.id = key;
		}
		
		@Override
		public String toString() {
			return id;
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
		Map<DustField, Object> values = new HashMap<>();
		
		public SimpleModel(SimpleType type) {
			this.type = type;
		}
		
		public String toString() {
			return type.toString();
		}
	}
	
	class SimpleEntity implements DustEntity {
		SimpleType type;
		DustUtilsFactory<SimpleType, SimpleModel> factModels = new DustUtilsFactory<SimpleType, SimpleModel>(false) {		
			@Override
			protected SimpleModel create(SimpleType key, Object... hints) {
				return new SimpleModel(key);
			}
		};
		
		public SimpleEntity(SimpleType type) {
			this.type = type;
		}
		
		public String toString() {
			return type.toString();
		}
	}

}
