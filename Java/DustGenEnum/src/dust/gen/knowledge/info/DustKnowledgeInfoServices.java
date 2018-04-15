package dust.gen.knowledge.info;

public interface DustKnowledgeInfoServices extends DustKnowledgeInfoComponents {
	
	interface DustKnowledgeInfoSource {
		boolean dustKnowledgeInfoSourceIsTypeSupported(DustEntity eType);
		
		DustEntity dustKnowledgeInfoSourceGet(DustEntity type, String idStore) throws Exception;
		void dustKnowledgeInfoSourceFind(DustEntity type, DustEntity expression, DustEntity processor) throws Exception;
		void dustKnowledgeInfoSourceDestruct(DustEntity entity) throws Exception;
	}

}
