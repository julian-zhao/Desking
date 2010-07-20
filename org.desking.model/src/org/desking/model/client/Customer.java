package org.desking.model.client;

import org.desking.model.annotation.*;

@TABLE("Customer")
public class Customer {

	@PROPERTY("ID")
	private String id;

	@PROPERTY("NAME")
	private String name;

	public String getId() {
		return id;
	}

	@SET("ID")
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	@SET("NAME")
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + "]";
	}
	
	
}
