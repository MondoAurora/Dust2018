package dust.qnd.pub;

public interface QnDDTypes extends QnDDComponents {

	enum QnDDAttStreamProc implements QnDDAttDef {
		uri
	}

	enum QnDDAttCore implements QnDDAttDef {
		logicOverrides
	}

	enum QnDDAttSelector implements QnDDAttDef {
		selCondition
	}

	enum QnDDLinkChain implements QnDDLinkDef {
		Next
	}

}
