package dust.runtime.simple;

import dust.pub.DustException;

public class DustSimpleManagerLink implements DustSimpleRuntimeComponents {

	void processRefs(DustBaseVisitor proc, SimpleEntity entity, DustBaseLink[] path, int idx) {
		DustBaseLink bl = path[idx];
		boolean last = idx == path.length-1;
		
		for ( SimpleRef ref : entity.getRefs(false) ) {
			if ( ref.linkDef.link == bl ) {
				if ( last ) {
					try {
						proc.dustDustBaseVisitorVisit(ref.eRight);
					} catch (Exception e) {
						DustException.wrapException(e, null);
					}
				} else {
					processRefs(proc, ref.eRight, path, idx + 1);
				}
			}
		}
	}

	DustBaseEntity modifyRefs(DustBaseLinkCommand refCmd, SimpleEntity seLeft, SimpleEntity seRight,
			DustBaseLink linkDef, Object[] params) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
