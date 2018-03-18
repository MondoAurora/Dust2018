package dust.runtime.simple;

import java.util.Set;

import dust.pub.DustException;
import dust.pub.DustUtils;

public class DustSimpleManagerLink implements DustSimpleRuntimeComponents {
	
	DustBaseLinkCommand[] REFCMD_SINGLE = {};

	void processRefs(DustBaseVisitor proc, SimpleEntity entity, DustLink[] path, int idx) {
		DustLink bl = path[idx];
		boolean last = idx == path.length-1;
		
		for ( SimpleRef ref : entity.getRefs(false) ) {
			if ( ref.linkDef.link == bl ) {
				if ( last ) {
					try {
						proc.dustDustBaseVisitorVisit(ref.eTarget);
					} catch (Exception e) {
						DustException.wrapException(e, null);
					}
				} else {
					processRefs(proc, ref.eTarget, path, idx + 1);
				}
			}
		}
	}

	DustEntity modifyRefs(DustBaseLinkCommand refCmd, SimpleEntity seLeft, SimpleEntity seRight,
			SimpleLinkDef sld, Object[] params) {
		
		DustMetaLinkType lt = (null == sld) ? null : sld.linkType;
		
		Object key = DustUtils.safeGet(0, params);
		
		Set<SimpleRef> refSet = seLeft.getRefs(DustBaseLinkCommand.Add == refCmd);
		SimpleRef sr;
		
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
			break;
		}
		
		return null;
	}

	public SimpleEntity getRefEntity(SimpleEntity se, boolean createIfMissing, SimpleLinkDef ld, Object key) throws Exception {
		Set<SimpleRef> refs = se.getRefs(createIfMissing);
		
		for ( SimpleRef r : refs ) {
			if ( r.match(ld, null, key) ) {
				return r.eTarget;
			}
		}
		
		SimpleEntity ret = null;
		if ( createIfMissing ) {
			ret = se.getCtx().dustSourceGet(ld.getTargetType(), null, null);
			SimpleRef sr = new SimpleRef(ld, ret, key);
			refs.add(sr);
		}
		return ret;
	}
	
}
