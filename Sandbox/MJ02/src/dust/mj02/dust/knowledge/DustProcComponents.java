package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustProcComponents extends DustComponents, DustDataComponents {
	
	enum DustProcTypes implements DustEntityKey {
		System, Context, Change, Binary, NativeBound, Scheduler, Task
	};
	
	enum DustProcAtts implements DustEntityKey {
		ChangeOldValue, ChangeNewValue, BinaryObjectName, BinaryAutoInit, NativeBoundId,
		TaskRepeatSec, TaskNextRun
	}
	
	enum DustProcLinks implements DustEntityKey {
		ContextChangeListeners, ContextBinaryAssignments, 
		BinaryImplementedServices,
		ChangeCmd, ChangeEntity, ChangeKey,
		SchedulerTasks, TaskEntity, TaskMessage
	}

	enum DustProcServices implements DustEntityKey {
		Listener, Channel, Processor, Active, Scheduler
	};
	
	enum DustProcMessages implements DustEntityKey {
		ListenerProcessChange, ChannelOpen, ChannelClose, ProcessorProcess,
		ActiveInit, ActiveRelease,
	};
	


	
	interface DustProcPocessor {
		public void processorProcess() throws Exception;
	}
	
	interface DustProcActive {
		public void activeInit() throws Exception;
		public void activeRelease() throws Exception;
	}
	
	interface DustProcListener {
		public void dustProcListenerProcessChange() throws Exception;
	}
	
	interface DustProcChangeListener {
		public void dustProcChangedAttribute(DustEntity entity, DustEntity att, Object value) throws Exception;
		public void dustProcChangedRef(DustEntity entity, DustRef ref, DataCommand cmd) throws Exception;
	}
	
}
