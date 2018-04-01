package dust.gen.knowledge.info;

public interface DustKnowledgeInfoServices extends DustKnowledgeInfoComponents {
	
	interface DustKnowledgeInfoSource {
		boolean dustKnowledgeInfoSourceIsTypeSupported(DustEntity eType);
		
		DustEntity dustKnowledgeInfoSourceGet(DustType type, String srcId, String revId) throws Exception;
		void dustKnowledgeInfoSourceFind(DustType type, DustEntity expression, DustEntity processor) throws Exception;
		void dustKnowledgeInfoSourceDestruct(DustEntity entity) throws Exception;
	}

}
