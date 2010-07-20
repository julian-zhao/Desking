package org.desking.model;

import java.sql.Connection;

public class SlaveModel<T> extends Model<T> {

	private Connection connection;
	private IModel<T> parent;
	
	public SlaveModel(IModel<T> parent, Connection connection) {
		this.parent = parent;
		this.connection = connection;
	}

	@Override
	public void insert(T entity) throws ModelException {

	}

	
	@Override
	protected Connection getConnection() throws ModelException {
		return connection;
	}

	@Override
	public T newInstance() {
		return parent.newInstance();
	}

	@Override
	public T newInstance(Class<?>[] argumentTypes, Object[] arguments) {
		return parent.newInstance(argumentTypes, arguments);
	}

}
