package dust.mj02.dust.java;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Pattern;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.java.DustJavaComponents.DustJavaAtts;
import dust.mj02.dust.java.DustJavaComponents.DustJavaLinks;
import dust.mj02.dust.java.DustJavaComponents.DustJavaTypes;
import dust.mj02.dust.knowledge.DustKernelComponents;
import dust.mj02.dust.knowledge.DustProcComponents;

public class DustJavaConnector implements DustKernelComponents, DustProcComponents.DustProcActive {
	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private String classPostfix = ".class";
	private Pattern innerClass = Pattern.compile(".*\\$\\d++");

	@Override
	public void activeInit() throws Exception {
		String root = DustUtils.getCtxVal(ContextRef.self, DustGenericAtts.IdentifiedIdLocal, false);
		loadPackage(root);
	}

	private DustEntity loadPackage(String packageName) {
		return DustUtils.accessEntity(DataCommand.getEntity, DustJavaTypes.JavaPackage, null, packageName,
				new EntityProcessor() {
					@Override
					public void processEntity(DustEntity entity) {
						initPackage(packageName, entity);
					}
				});
	}

	private void initPackage(String packageName, DustEntity ePack) {
		Package p = Package.getPackage(packageName);
		DustUtils.accessEntity(DataCommand.setValue, ePack, DustJavaAtts.JavaPackageObj, p);
		DustUtils.accessEntity(DataCommand.setValue, ePack, DustGenericAtts.IdentifiedIdLocal, packageName);

		String path = packageName.replace('.', '/');

		Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(path);
//			resources = ClassLoader.getSystemResources(path);
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				String name = resource.getFile();
				File f = new File(name);

				if (f.isDirectory()) {
					for (File fm : f.listFiles()) {
						DustEntity eMember = null;
						Object ldParent = null;
						String pathMember = packageName + "." + fm.getName();
						
						if (fm.isDirectory()) {
							ldParent = DustJavaLinks.JavaPackageParent;
							eMember = loadPackage(pathMember);
						} else if (pathMember.endsWith(classPostfix)){
							ldParent = DustJavaLinks.JavaItemParentPackage;
							String cName = pathMember.substring(0, pathMember.lastIndexOf("."));
							if(innerClass.matcher(cName).matches()) {
								continue;
							}
							
							eMember = DustUtils.accessEntity(DataCommand.getEntity, DustJavaTypes.JavaItem, null, cName,
									new EntityProcessor() {
										@Override
										public void processEntity(DustEntity eClass) {
											try {
												Class<?> c = Class.forName(cName);
												DustUtils.accessEntity(DataCommand.setValue, eClass,
														DustJavaAtts.JavaItemObj, c);
												DustUtils.accessEntity(DataCommand.setValue, eClass, 
														DustGenericAtts.IdentifiedIdLocal, cName);

											} catch (Exception e) {
												Dust.wrapAndRethrowException("", e);
											}
										}
									});
						}
						
						if ( null != eMember ) {
							DustUtils.accessEntity(DataCommand.setRef, eMember, ldParent, ePack);
						}
					}
				}
			}
		} catch (Exception e) {
			Dust.wrapAndRethrowException("", e);
		}
	}

	@Override
	public void activeRelease() throws Exception {
		// TODO Auto-generated method stub

	}
}
