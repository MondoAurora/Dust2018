package dust.mj02.dust.text;

import dust.mj02.dust.DustComponents;

public interface DustTextComponents extends DustComponents {
	
	enum DustTextTypes implements DustEntityKey {
	    TextBase, TextLanguage, TextEncoding, 
	    TextSpan, TextStatement, 
		TextRenderer, TextRenderContext
	};
	
	enum DustTextAtts implements DustEntityKey {
	    TextSpanString
	}
	
	enum DustTextLinks implements DustEntityKey {
	    TextBaseLanguage, TextBaseEncoding,
	    TextRendererRoot, 
	    TextRenderContextSpanSource, TextRenderContextTarget, TextRenderContextMessage
	}
	
	enum DustTextServices implements DustEntityKey {
	    TextRendererPlain, TextSource
	};
	
//	enum DustProcMessages implements DustEntityKey {
//	};
	
}
