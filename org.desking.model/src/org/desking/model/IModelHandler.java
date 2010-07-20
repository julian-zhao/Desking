package org.desking.model;

import java.sql.Connection;

public interface IModelHandler<T> {

	public void insert(Connection connection, T entity) throws ModelException;
}
