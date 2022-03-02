package tech.grasshopper.pojo;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class AdditionalData {

	@Getter
	private final Map<String, Object> info = new HashMap<>();

	public void addInfo(String key, Object value) {
		info.put(key, value);
	}

	public void getInfo(String key) {
		info.get(key);
	}
}
