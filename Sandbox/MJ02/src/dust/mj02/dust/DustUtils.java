package dust.mj02.dust;

import java.util.Map;

import dust.mj02.dust.knowledge.DustDataComponents.DustDataAtts;
import dust.mj02.dust.knowledge.DustProcComponents.DustProcAtts;
import dust.mj02.dust.knowledge.DustProcComponents.DustProcLinks;
import dust.mj02.dust.tools.DustGenericComponents.DustGenericLinks;
import dust.utils.DustUtilsJava;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DustUtils extends DustUtilsJava implements DustComponents {

	public static <RetVal> RetVal getMsgVal(DustEntityKey key, boolean resolveRef) {
		// Object ret = Dust.accessEntity(DataCommand.getValue, ContextRef.msg,
		// EntityResolver.getEntity(key), null, null);
		// if (resolveRef && (ret instanceof DustRef)) {
		// ret = ((DustRef) ret).get(RefKey.target);
		// }
		// return (RetVal) ret;
		return getCtxVal(ContextRef.msg, key, resolveRef);
	}

	public static <RetVal> RetVal getCtxVal(ContextRef ctxRef, DustEntityKey key, boolean resolveRef) {
		Object ret = Dust.accessEntity(DataCommand.getValue, ctxRef, EntityResolver.getEntity(key), null, null);
		if (resolveRef && (ret instanceof DustRef)) {
			ret = ((DustRef) ret).get(RefKey.target);
		}
		return (RetVal) ret;
	}

	public static <RetVal> RetVal optResolve(Object ob) {
		return (RetVal) ((ob instanceof DustEntityKey) ? EntityResolver.getEntity(ob) : ob);
	}

	public static DustEntity toEntity(Object ob) {
		if ((null == ob) || (ob instanceof DustEntity)) {
			return (DustEntity) ob;
		} else if (ob instanceof DustRef) {
			return ((DustRef) ob).get(RefKey.target);
		} else {
			return (DustEntity) ((ob instanceof DustEntityKey) ? EntityResolver.getEntity(ob) : ob);
		}
	}

	public static <RetVal> RetVal getByPath(Object ob, DustEntity... path) {
		for ( DustEntity key : path ) {
			DustEntity ee = toEntity(ob);
			ob = DustUtils.toEntity(DustUtils.accessEntity(DataCommand.getValue, ee, key));
			
			if ( null == ob ) {
				return null;
			}
		}
		
		return (RetVal) ob;
	}
	
	public static <RetVal> RetVal accessEntity(DataCommand cmd, Object... parameters) {
		DustEntity e = optResolve(parameters[0]);
		DustEntity key = (parameters.length > 1) ? optResolve(parameters[1]) : null;
		Object val = (parameters.length > 2) ? optResolve(parameters[2]) : null;
		Object collId = (parameters.length > 3) ? parameters[3] : null;

		Object ret = Dust.accessEntity(cmd, e, key, val, collId);

		return (RetVal) ret;
	}

	public static boolean isTrue(Object entity, Object att) {
		return Boolean.TRUE.equals(accessEntity(DataCommand.getValue, entity, att));
	}

	public static <RetVal> RetVal getBinary(Object entity, Object service) {
		Map bo = DustUtils.accessEntity(DataCommand.getValue, entity, DustDataAtts.EntityBinaries);
		if (null != bo) {
			DustEntity svc = optResolve(service);
			return (RetVal) bo.get(svc);
		}

		return null;
	}

	public static void registerService(Class<?> implClass, boolean autoInit, DustEntityKey svc,
			DustEntityKey... implServices) {
		String cName = implClass.getName();
		DustEntity ba = Dust.getEntity("BinaryAssignment: " + cName);

		DustUtils.accessEntity(DataCommand.setValue, ba, DustProcAtts.BinaryObjectName, cName, null);
		DustUtils.accessEntity(DataCommand.setRef, ba, DustProcLinks.BinaryImplementedServices, svc, null);
		DustUtils.accessEntity(DataCommand.setRef, ContextRef.ctx, DustProcLinks.ContextBinaryAssignments, ba, null);

		if (autoInit) {
			DustUtils.accessEntity(DataCommand.setValue, svc, DustProcAtts.BinaryAutoInit, true);
		}

		for (DustEntityKey impl : implServices) {
			DustUtils.accessEntity(DataCommand.setRef, svc, DustGenericLinks.ConnectedExtends, impl);
		}
	}
}
