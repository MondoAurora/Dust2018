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

	
	
	enum DustCommandCoreExecVisitor implements DustCommand {
		Visit;
		@Override
		public DustType getType() {
			return null;
		}
		@Override
		public DustService getService() {
			return DustServiceCoreExec.Visitor;
		}
	}

	enum DustCommandCoreExecProcessor implements DustCommand {
		Begin, End;
		@Override
		public DustType getType() {
			return null;
		}
		@Override
		public DustService getService() {
			return DustServiceCoreExec.Processor;
		}
	}

	enum DustServiceCoreExec implements DustService {
		Visitor, Processor;
		@Override
		public DustType getType() {
			return DustTypeCoreMeta.Service;
		}
	}
}
