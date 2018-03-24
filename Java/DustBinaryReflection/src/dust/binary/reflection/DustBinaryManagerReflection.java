package dust.binary.reflection;

import java.lang.reflect.Method;

import dust.gen.dust.utils.DustUtilsComponents;
import dust.pub.Dust;
import dust.pub.DustException;
import dust.pub.DustPubComponents;
import dust.pub.DustUtils;
import dust.pub.DustUtilsDev;
import dust.pub.boot.DustBootComponents;
import dust.pub.boot.DustBootComponents.DustConfig;
import dust.utils.DustUtilsFactory;

public class DustBinaryManagerReflection implements DustBootComponents.DustBinaryManager,
		DustBootComponents.DustConfigurable, DustPubComponents, DustUtilsComponents {

	class BinFactory extends DustUtilsFactory<DustEntity, Object> {
		public BinFactory() {
			super(false);
		}

		@Override
		protected Object create(DustEntity key, Object... hints) {
			return null;
		}
	};

	private Creator<BinFactory> cBinFact = new Creator<DustBinaryManagerReflection.BinFactory>() {

		@Override
		public BinFactory create(Object... params) {
			return new BinFactory();
		}
	};

	DustUtilsFactory<DustEntity, Method> factMethods = new DustUtilsFactory<DustEntity, Method>(false) {
		@Override
		protected Method create(DustEntity key, Object... hints) {
			String name = Dust.getAttrValue(key, DustAttributeUtilsIdentified.idLocal);
			try {
				return hints[0].getClass().getMethod(name);
			} catch (Exception e) {
				DustException.wrapException(e, null);
				return null;
			}
		}
	};

	DustUtilsFactory<DustEntity, Class> factClasses = new DustUtilsFactory<DustEntity, Class>(false) {
		@Override
		protected Class create(DustEntity key, Object... hints) {
			return null;
		}
	};

	private DustEntity eSelf;

	@Override
	public void init(DustConfig config) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEntity(DustEntity entity) {
		eSelf = entity;
	}

	@Override
	public void shutdown() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessage(DustEntity msg) throws Exception {
		DustEntity target = Dust.getRefEntity(msg, false, DustLinkBaseMessage.Target, null);
		DustEntity cmd = Dust.getRefEntity(msg, false, DustLinkBaseMessage.Command, null);

		BinFactory factImpl = DustUtils.getAttrValueSafe(target, DustAttributeBaseEntity.svcImpl, cBinFact);

		Object binOb = factImpl.get(cmd);
//		factMethods.get(cmd, binOb).invoke(binOb);

		DustUtilsDev.dump("In runtime send message :-)");
	}

}
