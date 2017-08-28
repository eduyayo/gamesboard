package com.pigdroid.gameboard.util;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONUtils {

	private JSONUtils() {

	}

	public static ObjectMapper createObjectMapper() {
		ObjectMapper ret = new ObjectMapper();
		ret.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return ret;
	}
}
