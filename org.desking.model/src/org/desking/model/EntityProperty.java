package org.desking.model;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.desking.model.internal.devices.IColumn;

public class EntityProperty extends Property {

	private Class<?> entityClass;
	private IColumn column;
	
	public EntityProperty(Field field, String name, Class<?> entityClass, IColumn column) {
		super(field, name, ENTITY);
		this.entityClass = entityClass;
		this.column = column;
	}

	@Override
	public void setParameter(Object obj, PreparedStatement stmt,
			int parameterIndex) throws ModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(Object obj, ResultSet resultSet) throws ModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public IColumn getColumn() {
		return column;
	}

	public Object loadEntity(Object targetId) throws ModelException {
		IModel<?> m = ModelService.getInstance().getModel(entityClass);
		return m.get((String)targetId);
	}

}
