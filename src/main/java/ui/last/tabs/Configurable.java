package ui.last.tabs;

import java.io.File;

import org.json.JSONObject;

import operators.extractors.FileExtractor;

public interface Configurable {

	default JSONObject configuration() throws Exception {
		return new JSONObject(FileExtractor.readFile(configurationFile())).getJSONObject(configurationSection());
	};

	static File configurationFile() {
		return new File("config/settings.json");
	}

	String configurationSection();
}
