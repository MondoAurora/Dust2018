package dust.qnd.pub;

import dust.utils.DustUtilsComponents;

public interface QnDDConstants extends QnDDComponents, DustUtilsComponents {
	enum DLinkTagged implements QnDDLinkDef {
		Parent
	}

	enum DAttArr implements QnDDAttDef {
		lastIndex
	}

	enum DLinkTag implements QnDDLinkDef {
		Root, CounterRoot, Children(DLinkTagged.Parent);

		private DLinkTag() {
		}
		private DLinkTag(QnDDLinkDef opp) {
			QnDDCompUtils.registerRevPair(this, opp);
		}
	}
	
	enum DAttText implements QnDDAttDef {
		dataId, text
	}
	
}
