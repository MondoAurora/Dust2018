package dust.mj02.dust.java;

import dust.mj02.dust.knowledge.DustKernelComponents;

public interface DustJavaComponents extends DustKernelComponents {
	
	enum DustJavaUnits implements DustEntityKey {
		DustJava
	};
	
	enum DustJavaTypes implements DustEntityKey {
		JavaConnector, JavaPackage, JavaClass, JavaMethod, JavaField
	};
	
	enum DustJavaAtts implements DustEntityKey {
		packageObj, classObj
	}
	
	enum DustJavaLinks implements DustEntityKey {
		ClassParentPackage, ClassParentClass, 
		PackageParent, PackageClasses, PackageMembers
	}

	enum DustJavaServices implements DustEntityKey {
		JavaConnector
	};
}
