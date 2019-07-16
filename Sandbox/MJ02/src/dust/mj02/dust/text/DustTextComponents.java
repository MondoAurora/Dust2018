package dust.mj02.dust.text;

import dust.mj02.dust.DustComponents;

public interface DustTextComponents extends DustComponents {
	
	enum DustTextTypes implements DustEntityKey {
	    TextBase, TextLanguage, TextEncoding, 
	    TextSpan, TextStatement, TextAttToText, 
		TextRenderer, TextRenderContext
	};
	
	enum DustTextAtts implements DustEntityKey {
	    TextSpanString
	}
	
	enum DustTextLinks implements DustEntityKey {
	    TextLanguageType, 
	    TextBaseLanguage, TextBaseEncoding,
	    TextRendererRoot, 
	    TextRenderContextSpanSource, TextRenderContextTarget, TextRenderContextMessage,
	}
	
	enum DustTextServices implements DustEntityKey {
	    TextRendererPlain, TextSource, TextAttToText
    };
    
    enum DustTextValues implements DustEntityKey {
        TextLanguageTypeHuman, TextLanguageTypeProgramming, TextLanguageTypeData, 
	};
	
//	enum DustProcMessages implements DustEntityKey {
//	};
	
}
