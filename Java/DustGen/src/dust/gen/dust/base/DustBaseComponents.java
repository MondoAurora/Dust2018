package dust.gen.dust.base;

public interface DustBaseComponents {

	interface DustBaseEntity {
	}
	
	enum DustEntityState implements DustBaseEntity {
		Temporal, InSync, RefChanged, Changed, Constructed, Destructed
	}
	


	interface DustBaseAttribute {
	}

	interface DustBaseLink {
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
	
	interface DustRuntime extends DustBaseBlockProcessor {
		<ValType> ValType getAttrValue(DustBaseEntity entity, DustBaseAttribute field);
		void setAttrValue(DustBaseEntity entity, DustBaseAttribute field, Object value);

		void processRefs(DustBaseVisitor proc, DustBaseEntity root, DustBaseLink... path);
		DustBaseEntity modifyRefs(DustBaseLinkCommand refCmd, DustBaseEntity left, DustBaseEntity right, DustBaseLink linkDef,
				Object... params);

		void send(DustBaseEntity msg);
	}
}
