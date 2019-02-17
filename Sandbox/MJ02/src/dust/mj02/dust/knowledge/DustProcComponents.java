package dust.mj02.dust.knowledge;

import dust.mj02.dust.DustComponents;

public interface DustProcComponents extends DustComponents, DustDataComponents {
	
	enum DustProcTypes {
		Context, Change, Binary
	};
	
	enum DustProcAtts {
		ChangeValue, ChangeKey, BinaryObjectName
	}
	
	enum DustProcLinks {
		ContextChangeListeners, BinaryImplementedServices,
		ChangeCmd, ChangeEntity, ChangeAtt, ChangeLinkDef, ChangeTarget
	}

	enum DustProcServices {
		Listener, Channel, Pocessor
	};
	
	enum DustProcMessages {
		ListenerProcessChange, ChannelOpen, ChannelClose, ProcessorProcess
	};
	


	
	interface DustProcInitable {
		public void dustProcInitableInit() throws Exception;
	}
	
	interface DustProcChangeListener {
		public void dustProcChangedAttribute(DustEntity entity, DustEntity att, Object value) throws Exception;
		public void dustProcChangedRef(DustEntity entity, DustRef ref, DataCommand cmd) throws Exception;
	}
	
}
