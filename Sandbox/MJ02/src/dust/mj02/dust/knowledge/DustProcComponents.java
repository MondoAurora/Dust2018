package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustProcComponents extends DustComponents, DustDataComponents {
	
	enum DustProcTypes implements DustEntityKey {
		System, Session, Change, Binary, NativeBound, Scheduler, Task, AccessControl,
		Relay, Iterator, ValueUpdater
	};
	
	enum DustProcAtts implements DustEntityKey {
		ChangeOldValue, ChangeNewValue, BinaryObjectName, BinaryAutoInit, NativeBoundId,
		TaskRepeatSec, TaskNextRun
	}
	
	enum DustProcLinks implements DustEntityKey {
		SessionRootEntity, SessionType, SessionChangeListeners, SessionBinaryAssignments, 
		BinaryImplementedServices,
		ChangeCmd, ChangeEntity, ChangeKey, ChangeSource,
		SchedulerTasks, TaskEntity, TaskMessage, TaskInitiator,
		AccessControlAccess, AccessControlChange,
		RelayTarget, IteratorLinkLoop, IteratorPathMsgTarget, IteratorEvalFilter, IteratorMsgStart, IteratorMsgSep, IteratorMsgEnd,
		ValueUpdaterSource, ValueUpdaterTarget
	}
	
    enum DustProcSessionTypeValues implements DustEntityKey {
        SessionTypeDirect, SessionTypeCloneShallow, SessionTypeCloneDeep
    };


	enum DustProcServices implements DustEntityKey {
		Listener, Channel, Processor, Active, Scheduler, Evaluator, AccessControl, 
		Iterator, ValueUpdater
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
