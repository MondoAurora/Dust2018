package dust.gen.dust.core.exec;

import dust.gen.dust.DustComponents;
import dust.gen.dust.core.data.DustCoreDataComponents;

public interface DustCoreExecComponents extends DustComponents, DustCoreDataComponents {

	enum DustConstCoreExecVisitorResponse implements DustEntity {
		OK, Skip, Exit, Repeat, Restart;
		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Const;
		}
	}

	enum DustLinkCoreExecMessage implements DustLink {
		Command, Target;
		@Override
		public DustType getType() {
			return DustTypeCoreExec.Message;
		}
	}

	enum DustTypeCoreExec implements DustType {
		Message, Status;
	}

}
