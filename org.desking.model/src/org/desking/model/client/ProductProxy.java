package org.desking.model.client;

import java.util.HashMap;
import java.util.Map;

public class ProductProxy extends Product {
	
	private Map<String, Integer> states = new HashMap<String, Integer>();

	public Integer getFieldState(String name) {
		return states.get(name);
	}

	public void setFieldState(String name, Integer state) {
		states.put(name, state);
	}
	

}
