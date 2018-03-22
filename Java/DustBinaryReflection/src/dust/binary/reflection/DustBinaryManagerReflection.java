package dust.binary.reflection;

import dust.gen.dust.base.DustBaseComponents;
import dust.pub.Dust;
import dust.pub.DustUtilsDev;
import dust.pub.boot.DustBootComponents;
import dust.pub.boot.DustBootComponents.DustConfig;

public class DustBinaryManagerReflection
		implements DustBootComponents.DustBinaryManager, DustBootComponents.DustConfigurable, DustBaseComponents {
	@Override
	public void init(DustConfig config) throws Exception {
		// TODO Auto-generated method stub

	}
	@Override
	public void shutdown() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessage(DustEntity msg) throws Exception {
		DustEntity target = Dust.getRefEntity(msg, false, DustLinkBaseMessage.Target, null);
		Object factImpl = Dust.getAttrValue(target, DustAttributeBaseEntity.svcImpl);
		
		DustUtilsDev.dump("In runtime send message :-)");
	}

}
