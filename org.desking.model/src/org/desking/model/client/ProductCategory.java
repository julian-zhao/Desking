package org.desking.model.client;

import java.util.List;

import org.desking.model.annotation.*;

@TABLE("PRODUCTCATEGORY")
public class ProductCategory {

	@PROPERTY("ID")
	private String id;

	@PROPERTY("NAME")
	private String name;

	@PROPERTY("products")
	@ENTITY_LIST(Product.class)
	@MANY_TO_ONE(source = "ID", target = "CATEGORYID")
	private List<Product> products;
	
	@GET("products")
	public List<Product> getProducts() {
		return products;
	}

	@GET("ID")
	public String getId() {
		return id;
	}

	@SET("ID")
	public void setId(String id) {
		this.id = id;
	}

	@GET("NAME")
	public String getName() {
		return name;
	}

	@SET("NAME")
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ProductCategory [id=" + id + ", name=" + name + "]";
	}
	
	
}
