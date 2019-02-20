package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustProcComponents extends DustComponents, DustDataComponents {
	
	enum DustProcTypes implements DustEntityKey {
		Context, Change, Binary
	};
	
	enum DustProcAtts implements DustEntityKey {
		ChangeOldValue, ChangeNewValue, BinaryObjectName
	}
	
	enum DustProcLinks implements DustEntityKey {
		ContextChangeListeners, ContextBinaryAssignments, 
		BinaryImplementedServices,
		ChangeCmd, ChangeEntity, ChangeKey, 
	}

	enum DustProcServices implements DustEntityKey {
		Listener, Channel, Pocessor
	};
	
	enum DustProcMessages implements DustEntityKey {
		ListenerProcessChange, ChannelOpen, ChannelClose, ProcessorProcess
	};
	


	
	interface DustProcPocessor {
		public void dustProcPocessorPocess() throws Exception;
	}
	
	interface DustProcInitable {
		public void dustProcInitableInit() throws Exception;
	}
	
	interface DustProcListener {
		public void dustProcListenerProcessChange() throws Exception;
	}
	
	interface DustProcChangeListener {
		public void dustProcChangedAttribute(DustEntity entity, DustEntity att, Object value) throws Exception;
		public void dustProcChangedRef(DustEntity entity, DustRef ref, DataCommand cmd) throws Exception;
	}
	
}
