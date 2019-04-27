package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustProcComponents extends DustComponents, DustDataComponents {
	
	enum DustProcTypes implements DustEntityKey {
		System, Context, Change, Binary, NativeBound, Scheduler, Task, AccessControl,
	};
	
	enum DustProcAtts implements DustEntityKey {
		ChangeOldValue, ChangeNewValue, BinaryObjectName, BinaryAutoInit, NativeBoundId,
		TaskRepeatSec, TaskNextRun
	}
	
	enum DustProcLinks implements DustEntityKey {
		ContextChangeListeners, ContextBinaryAssignments, 
		BinaryImplementedServices,
		ChangeCmd, ChangeEntity, ChangeKey, ChangeSource,
		SchedulerTasks, TaskEntity, TaskMessage, TaskInitiator,
		AccessControlAccess, AccessControlChange,
	}

	enum DustProcServices implements DustEntityKey {
		Listener, Channel, Processor, Active, Scheduler, Evaluator, AccessControl
	};
	
	enum DustProcMessages implements DustEntityKey {
		ListenerProcessChange, ChannelOpen, ChannelClose, ProcessorProcess, EvaluatorEvaluate, 
		ActiveInit, ActiveRelease,
	};
	
	
    interface DustProcPocessor {
        public void processorProcess() throws Exception;
    }
    
    interface DustProcEvaluator {
        public Object evaluatorEvaluate() throws Exception;
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
