package dust.gen.dust.base;

public interface DustBaseServices extends DustBaseComponents {
	
	interface DustBaseVisitor {
		DustBaseVisitorResponse dustDustBaseVisitorVisit(DustEntity entity) throws Exception;
	}

	interface DustBaseBlockProcessor {
		void dustBaseBlockProcessorBegin() throws Exception;
		void dustBaseBlockProcessorEnd(DustBaseVisitorResponse lastResp, Exception optException) throws Exception;
	}

	interface DustRuntime extends DustBaseBlockProcessor {
		<ValType> ValType getAttrValue(DustEntity entity, DustAttribute field);
		void setAttrValue(DustEntity entity, DustAttribute field, Object value);

		void processRefs(DustBaseVisitor proc, DustEntity root, DustLink... path);
		DustEntity modifyRefs(DustBaseLinkCommand refCmd, DustEntity left, DustEntity right, DustLink linkDef,
				Object... params);

		void send(DustEntity msg);
	}
	
	interface DustBaseSource {
		boolean dustSourceIsTypeSupported(DustEntity eType);
		
		DustEntity dustSourceGet(DustType type, String srcId, String revId) throws Exception;
		void dustSourceFind(DustType type, DustEntity expression, DustEntity processor) throws Exception;
		void dustSourceDestruct(DustEntity entity) throws Exception;
	}




}
