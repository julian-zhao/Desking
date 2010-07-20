package org.desking.model.client;

import java.util.List;

import org.desking.model.annotation.*;

@TABLE("ORDER")
public class Order {

	@PROPERTY("ID")
	private String id;

	@PROPERTY("NAME")
	private String name;

	@PROPERTY("CUSTOMERID")
	private String customerId;
	
	@PROPERTY("customer")
	@ENTITY(Customer.class)
	@COLUMN("CUSTOMERID")
	private Customer customer;
	
	@PROPERTY("products")
	@ENTITY_LIST(Product.class)
	@MANY_TO_MANY(table = "ORDER_PRODUCT", source = "ORDERID", target = "PRODUCTID")
	private List<Product> products;
	
	@GET("products")
	public List<Product> getProducts() {
		return products;
	}
	
	@GET("customer")
	public Customer getCustomer() {
		return customer;
	}

	@SET("customer")
	public void setCustomer(Customer customer) {
		this.customer = customer;
		if (customer != null)
			customerId = customer.getId();
		else
			customerId = null;
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
		return "Order [id=" + id + ", name=" + name + ", customerId="
				+ customerId + "]";
	}
	
	
}
