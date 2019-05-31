package dust.mj02.dust.knowledge;

import java.util.Map;

import dust.mj02.dust.DustComponents;

public interface DustCommComponents extends DustComponents {
	enum DustCommTypes implements DustEntityKey {
		Domain, Unit, Persistent, Store, RemoteRef
	};
	
	enum DustCommAtts implements DustEntityKey {
		PersistentEntityId, PersistentCommitId, UnitNextEntityId,
		RemoteRefModuleName, RemoteRefModuleCommitId, RemoteRefItemModuleId, 
	}
	
	enum DustCommLinks implements DustEntityKey {
		PersistentContainingUnit, PersistentStoreWith, UnitEntities, UnitDomain, 
	}

	enum DustCommTags implements DustEntityKey {
		PersistentNotStored
	}
	
	enum DustCommServices implements DustEntityKey {
		Store
	};
	
	enum DustCommMessages implements DustEntityKey {
		StoreLoad, StoreSave
	};


	enum CommKeys {
		CommSrcHandler, KeyCommIdLocal, KeyCommIdStore;
	};
	
	
	interface DustCommStore {
		public void dustCommStoreLoad() throws Exception;
		public void dustCommStoreSave() throws Exception;
	}

	
	
	interface DustCommSource {
		public Map<Object, Object> dustCommSourceRead(Object src) throws Exception;
	}
}
