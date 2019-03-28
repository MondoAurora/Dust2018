package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustProcComponents extends DustComponents, DustDataComponents {
	
	enum DustProcTypes implements DustEntityKey {
		System, Context, Change, Binary
	};
	
	enum DustProcAtts implements DustEntityKey {
		ChangeOldValue, ChangeNewValue, BinaryObjectName, BinaryAutoInit
	}
	
	enum DustProcLinks implements DustEntityKey {
		ContextChangeListeners, ContextBinaryAssignments, 
		BinaryImplementedServices,
		ChangeCmd, ChangeEntity, ChangeKey, 
	}

	enum DustProcServices implements DustEntityKey {
		Listener, Channel, Processor, Active
	};
	
	enum DustProcMessages implements DustEntityKey {
		ListenerProcessChange, ChannelOpen, ChannelClose, ProcessorProcess,
		ActiveInit, ActiveRelease,
	};
	


	
	interface DustProcPocessor {
		public void dustProcPocessorPocess() throws Exception;
	}
	
	interface DustProcActive {
		public void dustProcActiveInit() throws Exception;
		public void dustProcActiveRelease() throws Exception;
	}
	
	interface DustProcListener {
		public void dustProcListenerProcessChange() throws Exception;
	}
	
	interface DustProcChangeListener {
		public void dustProcChangedAttribute(DustEntity entity, DustEntity att, Object value) throws Exception;
		public void dustProcChangedRef(DustEntity entity, DustRef ref, DataCommand cmd) throws Exception;
	}
	
}
