package org.desking.model.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.desking.model.IModelHandler;
import org.desking.model.ModelException;

public class CustomerHandler<T> implements IModelHandler<T> {

	@Override
	public void insert(Connection connection, T entity)
			throws ModelException {
		Customer customer = (Customer) entity;
		String sql = "INSERT INTO \"Customer\" (ID, NAME) VALUES (?,?)";
		PreparedStatement s = null;
		
		try {
			s = connection.prepareStatement(sql);
			s.setString(1, customer.getId());
			s.setString(2, customer.getName());
			s.executeUpdate();
		} catch (SQLException e) {
			throw new ModelException(e);
		}
	}


}
