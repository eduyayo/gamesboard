package com.pigdroid.game.resource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResourceProvider {

	private Map<String, Integer> idByName = new HashMap<String, Integer>();
	private Map<Integer, String> nameById = new HashMap<Integer, String>();

	public ResourceProvider() {
	}

	public Integer addResource(String resourceName, Integer newId) {
		Integer id = newId == null ? (int) UUID.randomUUID().getLeastSignificantBits() : newId;
		idByName.put(resourceName, id);
		nameById.put(id, resourceName);
		return id;
	}

	private InputStream openResource(Integer key) {
		String name = nameById.get(key);
		return name != null ? openResource(nameById.get(key)) : null;
	}

	private InputStream openResource(String resourceName) {
		return ResourceProvider.class.getClassLoader()
				.getResourceAsStream(resourceName);
	}

	public Integer addResource(String resourceName) {
		return addResource(resourceName, null);
	}

	public InputStream getResource(String resourceName) {
		Integer id = idByName.get(resourceName);
		if (id == null) {
			throw new RuntimeException("Resource not found by name or alias '" + resourceName +"'.");
		}
		return getResource(id);
	}

	public InputStream getResource(Integer id) {
		return openResource(id);
	}
	
	public String getResourceName(Integer id) {
		return nameById.get(id);
	}

}
