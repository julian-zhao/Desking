package org.desking.model;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.desking.model.internal.devices.IColumn;

public class ManyToManyProperty extends Property {

	private Class<?> entityClass;
	private String relationTableName;
	private String sourceId;
	private String targetId;
	
	public ManyToManyProperty(Field field, String name, Class<?> entityClass,
			String relationTableName, String sourceId, String targetId) {
		super(field, name, ENTITY_LIST);
		this.entityClass = entityClass;
		this.relationTableName = relationTableName;
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
		//String sql = "SELECT t.* FROM t INNER JOIN r ON r.targetId = t.id WHERE r.sourceId = id";
		
		IModel<?> m = ModelService.getInstance().getModel(entityClass);
		IFilter filter = new RelationFilter(relationTableName, sourceId, targetId, id);
		List<?> list = m.getList(filter);
		return list;
	}

}
