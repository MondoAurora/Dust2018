package dust.mj01.dust;

public interface DustMetaComponents extends DustComponents {
	
	public enum DustMetaId implements DustId {
		;
	}
	
	public enum DustMetaUnit implements DustId {
		Types, Services, Shared;
	}
	
	public enum DustMetaRelation implements DustId {
		Source, Target, Reverse;
	}
	
	public enum DustMetaAttribute implements DustId {
		Type, Mandatory, Multiplicity;
	}
	
}
