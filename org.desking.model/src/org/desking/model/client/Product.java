package org.desking.model.client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.desking.model.annotation.*;

@TABLE("PRODUCT")
public class Product {

	@PROPERTY("ID")
	private String id;

	@PROPERTY("NAME")
	private String name;
	
	@PROPERTY("DESCRIPTION")
	@LARGE
	private String description;
	
	@PROPERTY("CATEGORYID")
	private String categoryId;
	
	@PROPERTY("category")
	@ENTITY(ProductCategory.class)
	@COLUMN("CATEGORYID")
	private ProductCategory category;
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
	
	public Product() {
		//this.description = ("DESCRIPTION");
	}
	

	@GET("DESCRIPTION")
	public String getDescription() {
		return description;
	}


	@SET("DESCRIPTION")
	public void setDescription(String description) {
		propertyChangeSupport.firePropertyChange("description", this.description,
				this.description = description);
	}



	@GET("category")
	public ProductCategory getCategory() {
		return category;
	}

	@SET("category")
	public void setCategory(ProductCategory category) {
		this.category = category;
		this.categoryId = category.getId();
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
		
		propertyChangeSupport.firePropertyChange("name", this.name,
				this.name = name);
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", description="
				+ description + ", categoryId=" + categoryId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
