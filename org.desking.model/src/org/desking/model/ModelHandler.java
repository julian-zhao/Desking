package org.desking.model;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.desking.model.annotation.COLUMN;
import org.desking.model.annotation.TABLE;
import org.desking.model.internal.devices.IColumn;
import org.desking.model.internal.devices.ITable;

public class ModelHandler<T> implements IModelHandler<T> {

	private IModelDevice device;
	private Class<T> clazz;
	private ITable table;
	private Map<String, Field> fields = new HashMap<String, Field>();
	
	public ModelHandler(IModelDevice device, Class<T> clazz) throws ModelException {
		this.device = device;
		this.clazz = clazz;
		initialize();
	}
	
	private void initialize() throws ModelException {
		if (!clazz.isAnnotationPresent(TABLE.class))
			throw new ModelException("table of " + clazz.getName() + " not defined.");
		String tableName = clazz.getAnnotation(TABLE.class).value();
		table = device.getTable(tableName);
		if (table == null)
			throw new ModelException("table " + tableName + " not found.");
		Field[] fields = clazz.getDeclaredFields();
		for (Field field: fields) {
			if (!field.isAnnotationPresent(COLUMN.class)) 
				continue;
			COLUMN column = field.getAnnotation(COLUMN.class);
			String columnName = column.value();
			this.fields.put(columnName, field);
		}
	}

	@Override
	public void insert(Connection connection, T entity) throws ModelException {
		IEntity e = (IEntity) entity;
		IColumn[] columns = table.getColumns();
		int count = columns.length;
		String sql = "INSERT INTO \"" + table.getName() + "\" (";
		String holder = "";
		List<String> params = new ArrayList<String>();
		boolean first = true;
		for (int i=0; i<count; ++i) {
			IColumn column = columns[i];
			if (column.isAutoIncrement())
				continue;
			final String columnName = column.getName();
			final String comma = first ? "" : ",";
			params.add(columnName);
			sql += comma + columnName;
			holder += comma + "?";
			first = false;
		}
		sql += ") VALUES (" + holder + ")";
		
		System.out.println(sql);
		
		try {
			PreparedStatement s = connection.prepareStatement(sql);
			Iterator<String> it = params.iterator();
			int idx = 1;
			while(it.hasNext()) {
				String columnName = it.next();
				Field field = fields.get(columnName);
				if (field == null)
					throw new ModelException("no class field for column " + columnName);
				Object value = null;
				try {
					field.setAccessible(true);
					value = field.get(entity);
				} catch (IllegalArgumentException e1) {
					throw new ModelException(e1);
				} catch (IllegalAccessException e1) {
					throw new ModelException(e1);
				}
				s.setObject(idx++, value);
			}
			s.executeUpdate();
		} catch (SQLException e1) {
			throw new ModelException(e1);
		}
	}

}
