package background;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import javafx.concurrent.Task;

import com.google.cloud.translate.Translate.TranslateOption;

public class TranslatorBackgroundTask extends Task<Boolean> {	
	private String _code;
	private String _text;
	
	public TranslatorBackgroundTask(String code, String text) {
		//Set language code and text to translate
		_code = code;
		_text = text;
	}

	@Override
	protected Boolean call() {
		try {
		// Instantiates a client
	    Translate translate = TranslateOptions.getDefaultInstance().getService();

	    // Translates some text into Russian
	    Translation translation = translate.translate(_text, TranslateOption.sourceLanguage("en"), TranslateOption.targetLanguage(_code));
	    
	    updateMessage(translation.getTranslatedText());
	    
	    return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
