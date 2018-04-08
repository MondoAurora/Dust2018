package dust.gen.knowledge.info;

public interface DustKnowledgeInfoServices extends DustKnowledgeInfoComponents {
	
	interface DustKnowledgeInfoSource {
		boolean dustKnowledgeInfoSourceIsTypeSupported(DustType eType);
		
		DustEntity dustKnowledgeInfoSourceGet(DustType type, String idStore) throws Exception;
		void dustKnowledgeInfoSourceFind(DustType type, DustEntity expression, DustEntity processor) throws Exception;
		void dustKnowledgeInfoSourceDestruct(DustEntity entity) throws Exception;
	}

}
