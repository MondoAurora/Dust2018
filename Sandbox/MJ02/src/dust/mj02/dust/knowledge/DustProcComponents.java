package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustProcComponents extends DustComponents, DustDataComponents {
	
	enum DustProcTypes implements DustEntityKey {
		System, Session, Binary, NativeBound, Scheduler, Task, AccessControl,
		Relay, Iterator, ValueUpdater, Dispatcher, UndoStack
	};
	
	enum DustProcAtts implements DustEntityKey {
		BinaryObjectName, BinaryAutoInit, NativeBoundId,
		TaskRepeatSec, TaskNextRun, SessionChangeMute, SessionCallDepth,
	}
	
	enum DustProcLinks implements DustEntityKey {
		SessionRootEntity, SessionType, SessionChangeListeners, SessionChangeAgents, SessionBinaryAssignments, 
		SessionCurrentStatement, SessionUndoStack, 
		BinaryImplementedServices,
		ChangeSource,
		SchedulerTasks, TaskEntity, TaskMessage, TaskInitiator,
		AccessControlAccess, AccessControlChange,
		RelayTarget, IteratorLinkLoop, IteratorPathMsgTarget, IteratorEvalFilter, IteratorMsgStart, IteratorMsgSep, IteratorMsgEnd,
		ValueUpdaterSource, ValueUpdaterTarget, DispatcherTargets
	}
	
    enum DustProcSessionTypeValues implements DustEntityKey {
        SessionTypeDirect, SessionTypeCloneShallow, SessionTypeCloneDeep
    };


	enum DustProcServices implements DustEntityKey {
		Listener, Agent, Channel, Processor, Active, Scheduler, Evaluator, AccessControl, 
		Iterator, ValueUpdater
	};
	
	enum DustProcMessages implements DustEntityKey {
		ListenerProcessChange, AgentProcessStatement, ChannelOpen, ChannelClose, ProcessorProcess, EvaluatorEvaluate, 
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
    
    interface DustProcAgent {
        public void dustProcAgentProcessStatement() throws Exception;
    }
}
