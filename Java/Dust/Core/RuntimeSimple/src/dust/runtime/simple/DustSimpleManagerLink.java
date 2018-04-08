package dust.runtime.simple;

import java.util.EnumSet;
import java.util.Set;

import dust.pub.DustException;
import dust.pub.DustPubComponents;
import dust.pub.DustUtils;

public class DustSimpleManagerLink implements DustSimpleRuntimeComponents {
	
	private static final EnumSet<DustConstKnowledgeInfoLinkCommand> REFCMD_CREATE = EnumSet.of(DustConstKnowledgeInfoLinkCommand.Add, DustConstKnowledgeInfoLinkCommand.Replace);

	void processRefs(DustKnowledgeProcVisitor proc, InfoEntity entity, DustLink[] path, int idx) {
		SimpleLinkDef bl = entity.getCtx().optResolveMeta(path[idx]);
		boolean last = idx == path.length-1;
		
		for ( SimpleRef ref : entity.getRefs(false) ) {
			if ( ref.linkDef == bl ) {
				if ( last ) {
					try {
						proc.dustKnowledgeProcVisitorVisit(ref.eTarget);
					} catch (Exception e) {
						DustException.wrapException(e, DustPubComponents.DustStatusInfoPub.ErrorVistorExecution);
					}
				} else {
					processRefs(proc, ref.eTarget, path, idx + 1);
				}
			}
		}
	}

	DustEntity modifyRefs(DustConstKnowledgeInfoLinkCommand refCmd, InfoEntity seLeft, InfoEntity seRight,
			SimpleLinkDef sld, Object[] params) {
		
//		DustMetaLinkType lt = (null == sld) ? null : sld.linkType;
		
		Object key = DustUtils.safeGet(0, params);
		
		Set<SimpleRef> refSet = seLeft.getRefs(REFCMD_CREATE.contains(refCmd));
		if ( null == refSet ) {
			return null;
		}
		SimpleRef sr = null;
		for ( SimpleRef r : refSet ) {
			if ( r.match(sld, null, key) ) {
				sr = r;
				break;
			}
		}

		
		switch ( refCmd ) {
		case Add:
			sr = new SimpleRef(sld, seRight, key);
			refSet.add(sr);
			break;
		case ChangeKey:
			break;
		case Remove:
			break;
		case Replace:
			if ( null == sr ) {
				sr = new SimpleRef(sld, seRight, key);
				refSet.add(sr);
			} else {
				sr.setTarget(seRight);
			}
			break;
		}
		
		return null;
	}

	public InfoEntity getRefEntity(InfoEntity se, boolean createIfMissing, SimpleLinkDef ld, Object key) throws Exception {
		Set<SimpleRef> refs = se.getRefs(createIfMissing);
		
		for ( SimpleRef r : refs ) {
			if ( r.match(ld, null, key) ) {
				return r.eTarget;
			}
		}
		
		InfoEntity ret = null;
		if ( createIfMissing ) {
			ret = se.getCtx().dustKnowledgeInfoSourceGet(null, null);
			SimpleRef sr = new SimpleRef(ld, ret, key);
			refs.add(sr);
		}
		return ret;
	}
	
}
