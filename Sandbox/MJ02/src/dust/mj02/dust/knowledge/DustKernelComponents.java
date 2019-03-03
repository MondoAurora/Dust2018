package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;
import dust.mj02.dust.tools.DustGenericComponents;

public interface DustKernelComponents
		extends DustComponents, DustMetaComponents, DustGenericComponents, DustCommComponents , DustProcComponents , DustDataComponents {

	enum DustKernelUnits implements DustEntityKey {
		DustMeta, DustData, DustProc, DustComm
	};

}
