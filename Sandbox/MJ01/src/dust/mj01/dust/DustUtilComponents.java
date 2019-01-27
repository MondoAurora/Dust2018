package dust.mj01.dust;

public interface DustUtilComponents extends DustComponents {
	
	public enum DustUtilDependency implements DustId {
		Required;
	}

	public enum DustUtilOwned implements DustId {
		Owner;
	}

}
