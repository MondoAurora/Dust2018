package dust.gen.runtime.access;

import dust.gen.knowledge.info.DustKnowledgeInfoComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;

public interface DustRuntimeAccessComponents extends DustKnowledgeInfoComponents, DustKnowledgeProcComponents {
	
	enum DustStatusRuntimeAccess implements DustEntity {
		AccessDenied;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeProc.Status;
		}
	}
	
	enum DustConstRuntimeAccessAccessMode implements DustConst {
		Check, Read, Write, Execute;
		@Override
		public DustType getType() {
			return DustTypeKnowledgeMeta.Const;
		}
	}
	
}
