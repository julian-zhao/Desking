package org.desking.model;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.desking.model.internal.devices.IColumn;

public class DataProperty extends Property {

	private IColumn column;
	
	public DataProperty(Field field, String name, int type, IColumn column) {
		super(field, name, type);
		this.column = column;
	}

	@Override
	public void setParameter(Object obj, PreparedStatement stmt,
			int parameterIndex) throws ModelException {
		Object value = getValue(obj);
		try {
			stmt.setObject(parameterIndex, value, column.getType());
		} catch (SQLException e) {
			System.out.println("**** setParameter " + getName() + " Exception ****");
			throw new ModelException(e);
		}
	}

	@Override
	public void setValue(Object obj, ResultSet resultSet) throws ModelException {
		try {
			Object value = resultSet.getObject(column.getName());
			setValue(obj, value);
		} catch (SQLException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public IColumn getColumn() {
		return column;
	}

}
