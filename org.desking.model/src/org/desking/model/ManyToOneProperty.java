package org.desking.model;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.desking.model.internal.devices.IColumn;

public class ManyToOneProperty extends Property {

	private Class<?> entityClass;
	private String sourceId;
	private String targetId;
	
	public ManyToOneProperty(Field field, String name, Class<?> entityClass,
			String sourceId, String targetId) {
		super(field, name, ENTITY_LIST);
		this.entityClass = entityClass;
		this.sourceId = sourceId;
		this.targetId = targetId;
	}

	@Override
	public void setParameter(Object obj, PreparedStatement stmt,
			int parameterIndex) throws ModelException {
		throw new ModelException("list property don't support this method.");
	}

	@Override
	public void setValue(Object obj, ResultSet resultSet) throws ModelException {
		throw new ModelException("list property don't support this method.");
	}

	@Override
	public IColumn getColumn() {
		return null;
	}

	public List<?> loadEntities(String id) throws ModelException {
		IModel<?> m = ModelService.getInstance().getModel(entityClass);
		IFilter filter = new Filter(targetId, IFilter.EQUALS, id);
		List<?> list = m.getList(filter);
		return list;
	}

}
