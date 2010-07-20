package org.desking.model;

public interface IModelProvider {
	public <T> IModel<T> getModel(Class<T> clazz) throws ModelException;
}
