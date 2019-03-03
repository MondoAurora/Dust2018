package dust.mj02.dust.java;

import dust.mj02.dust.knowledge.DustKernelComponents;

public interface DustJavaComponents extends DustKernelComponents {
	
	enum DustJavaUnits implements DustEntityKey {
		DustJava
	};
	
	enum DustJavaTypes implements DustEntityKey {
		JavaConnector, JavaPackage, JavaItem, JavaMethod, JavaField
	};
	
	enum DustJavaAtts implements DustEntityKey {
		JavaPackageObj, JavaItemObj
	}
	
	enum DustJavaLinks implements DustEntityKey {
		JavaItemParentPackage, 
		JavaPackageParent, JavaPackageItems, JavaPackageMembers
	}

	enum DustJavaServices implements DustEntityKey {
		JavaConnector
	};
}
