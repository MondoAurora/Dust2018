package dust.mj02.dust;

import dust.mj02.dust.knowledge.DustKernelComponents;

public class DustTempHacks extends DustUtils implements DustKernelComponents {
	public static DustEntity loadFromEnum(Object key) {
		DustEntity e = null;
		
		if (key instanceof DustEntityKey) {
			// so that all enums will have their entity without problem
			String cn = key.getClass().getName();
			String en = ((Enum<?>) key).name();
			String kk = cn +":" + en;
			
			e = EntityResolver.register(kk, key);
			
			DustUtils.accessEntity(DataCommand.setValue, e, DustGenericAtts.identifiedIdLocal, en);
			DustUtils.accessEntity(DataCommand.setValue, e, DustCommAtts.idStore, kk);
			
			DustMetaTypes mt = DustMetaTypes.getMetaTypeHack(cn);
			
			if ( null != mt ) {
				DustUtils.accessEntity(DataCommand.setRef, e, DustDataLinks.EntityPrimaryType, mt);				
			}
		}

		return e;
	}
}
