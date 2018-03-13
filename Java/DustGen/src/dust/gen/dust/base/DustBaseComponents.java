package dust.gen.dust.base;

public interface DustBaseComponents {

	interface DustBaseEntity {
	}

	interface DustBaseAttributeDef {
	}

	interface DustBaseLinkDef {
	}

	enum DustBaseLinkCommand implements DustBaseEntity {
		Set, Remove, RemoveAll, ChangeKey;
	}

	enum DustBaseVisitorResponse implements DustBaseEntity {
		OK, Skip, Exit, Repeat, Restart;
	}

	interface DustBaseVisitor {
		DustBaseVisitorResponse dustDustBaseVisitorVisit(DustBaseEntity entity) throws Exception;
	}

	interface DustBaseBlockProcessor {
		void dustBaseBlockProcessorBegin() throws Exception;
		void dustBaseBlockProcessorEnd(DustBaseVisitorResponse lastResp, Exception optException) throws Exception;
	}

	enum DustBaseContext implements DustBaseEntity {
		Self, Message, Block;
	}

}
