package org.desking.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.desking.model.annotation.*;
import org.desking.model.internal.devices.IColumn;
import org.desking.model.internal.devices.ITable;

public class Model<T> implements IModel<T> {
	//private ModelRegistry registry;
	private IModelDevice device;
	private ITable table;
	private String primaryColumnName;
	private Class<T> clazz;
	
	private EntityEnhancer enhancer;
	//private IModelHandler<T> handler;
	
	private List<Property> list = new ArrayList<Property>();
	private Map<String, Property> properties = new HashMap<String, Property>();
	
	private Map<String, T> cache = new HashMap<String, T>();
	private Property primaryProperty;
	
	protected Model() {
		
	}
	
	public Model(ModelRegistry registry, IModelDevice device, Class<T> clazz, ITable table) {
		//this.registry = registry;
		this.device = device;
		this.table = table;
		this.clazz = clazz;
		this.primaryColumnName = table.getPrimaryKey();
		initialize();
	}
	
	private void initialize() {
		initProperties();
		initEnhancer();
	}
	
	private void initProperties() {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field: fields) {
			if (!field.isAnnotationPresent(PROPERTY.class))
				continue;
			
			String propertyName = field.getAnnotation(PROPERTY.class).value();
			
			Property property = null;
			
			
			if (field.isAnnotationPresent(ENTITY_LIST.class)) {
				Class<?> entityClass = field.getAnnotation(ENTITY_LIST.class).value();
				
				if (field.isAnnotationPresent(MANY_TO_MANY.class)) {
					MANY_TO_MANY an = field.getAnnotation(MANY_TO_MANY.class);
					String relationTableName = an.table();
					String relationSourceId = an.source();
					String relationTargetId = an.target();
					
					property = new ManyToManyProperty(field, propertyName, entityClass, relationTableName, relationSourceId, relationTargetId);
				} else if (field.isAnnotationPresent(MANY_TO_ONE.class)) {
					MANY_TO_ONE an = field.getAnnotation(MANY_TO_ONE.class);
					String sourceId = an.source();
					String targetId = an.target();
					IColumn column = table.getColumn(sourceId);
					if (column == null)
						throw new IllegalArgumentException("Column '" + sourceId + "' not found for " + clazz.getName() + "." + field.getName());
					property = new ManyToOneProperty(field, propertyName, entityClass, sourceId, targetId);
				} else {
					throw new IllegalArgumentException("Unknown relation for " + clazz.getName() + "." + field.getName());
				}
			} else if (field.isAnnotationPresent(ENTITY.class)) {
				Class<?> entityClass = field.getAnnotation(ENTITY.class).value();
				
				if (!field.isAnnotationPresent(COLUMN.class))
					throw new IllegalArgumentException("Unknown column for " + clazz.getName() + "." + field.getName());
				String columnName = field.getAnnotation(COLUMN.class).value();
				IColumn column = table.getColumn(columnName);
				if (column == null)
					throw new IllegalArgumentException("Column '" + columnName + "' not found for " + clazz.getName() + "." + field.getName());
				property = new EntityProperty(field, propertyName, entityClass, column);
			} else {
				int type = IProperty.SMALL_DATA;
				if (field.isAnnotationPresent(LARGE.class)) 
					type = IProperty.LARGE_DATA;
				
				String columnName = propertyName;
				if (field.isAnnotationPresent(COLUMN.class)) {
					columnName = field.getAnnotation(COLUMN.class).value();
				}
				
				IColumn column = table.getColumn(columnName);
				if (column == null)
					throw new IllegalArgumentException("Column '" + columnName + "' not found for " + clazz.getName() + "." + field.getName());
				
				
					
				property = new DataProperty(field, propertyName, type, column);
				
				if (column.isPrimaryKey())
					primaryProperty = property;
			}
			
			if (property != null) {
				field.setAccessible(true);
				list.add(property);
				properties.put(property.getName(), property);
			}
		}
	}

	private void initEnhancer() {
		enhancer = new EntityEnhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object obj, Method method, Object[] args,
					MethodProxy proxy) throws Throwable {
				
				String methodName = method.getName();
				
				IEntity e = null;
				if (obj instanceof IEntity) {
					e = (IEntity) obj;
				}
				
				if (e == null || "toString".equals(methodName) || "hashCode".equals(methodName)) {
					return proxy.invokeSuper(obj, args);
				}

				IEntityData data = e.getEntityData();
				
				if (method.isAnnotationPresent(GET.class)) {
					GET get = method.getAnnotation(GET.class);
					String propName = get.value();
					boolean loaded = data.isLoaded(propName);
					if (!loaded) {
						System.out.println("***" + propName + " is not loaded.");
						loadProperty(data, propName);
					}
				}
				
				if (method.isAnnotationPresent(SET.class)) {
					SET set = method.getAnnotation(SET.class);
					String propName = set.value();
					
					if (data.isPresent() && propName.equals(primaryProperty.getName()))
						throw new ModelException("Cannot set primary key on a present entity.");
					
					System.out.println(propName + " is modified.");
					
					data.setModified(true);
					data.setModified(propName, true);
				}
				
				
				//System.out.println("Before " + methodName);
				
				Object value = proxy.invokeSuper(obj, args);
				
				//System.out.println("After " + methodName);
				return value;
			}
		
		});
	}

	private void loadProperty(IEntityData data, String propName) throws ModelException {
		String primaryKey = table.getPrimaryKey();
		Object value = data.getValue(primaryKey);
		if (value == null)
			throw new ModelException("value of primary key not found.");
		String id = value.toString();
		IProperty property = getProperty(propName);
		
		int type = property.getType();
		if (type == IProperty.LARGE_DATA) {
			Connection connection = getConnection();
			String columnName = property.getColumn().getName();
			String sql = "SELECT " + columnName + " FROM \"" + table.getName() + "\" WHERE " + primaryKey + " = ?";
			System.out.println(sql);
			PreparedStatement s = null;
			ResultSet rs = null;
			try {
				s = connection.prepareStatement(sql);
				s.setString(1, id);
				rs = s.executeQuery();
				if (rs.next()) {
					data.setValue(propName, rs.getObject(columnName));
					data.setLoaded(propName, true);
				}
			} catch (SQLException e) {
				throw new ModelException(e);
			}
		} else if (type == IProperty.ENTITY) {
			String columnName = property.getColumn().getName();
			Object targetId = data.getValue(columnName);
			if (targetId == null)
				throw new ModelException("value for entity " + property.getName() + " not found.");
			EntityProperty p = (EntityProperty) property;
			Object entity = p.loadEntity(targetId);
			data.setValue(propName, entity);
			data.setLoaded(propName, true);
		} else if (type == IProperty.ENTITY_LIST) {
			if (property instanceof ManyToOneProperty) {
				ManyToOneProperty p = (ManyToOneProperty) property;
				List<?> list = p.loadEntities(id);
				data.setValue(propName, list);
				data.setLoaded(propName, true);
			}
			
			if (property instanceof ManyToManyProperty) {
				ManyToManyProperty p = (ManyToManyProperty) property;
				List<?> list = p.loadEntities(id);
				data.setValue(propName, list);
				data.setLoaded(propName, true);
			}
		}
	}
	
	protected Connection getConnection() throws ModelException {
		return device.getCachedConnection();
	}
	
	@Override
	public void insert(T entity) throws ModelException {
		if (!(entity instanceof IEntity)) 
			throw new ModelException("Entity instance must be created by model.");

		doInsert(getConnection(), entity);
	}
	
	private void doInsert(Connection connection, T entity) throws ModelException {
		IEntity e = (IEntity) entity;
		IEntityData data = e.getEntityData();
		
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
				IProperty property = getProperty(columnName);
				
				if (property == null)
					throw new ModelException("no class field for column " + columnName);
				
				property.setParameter(entity, s, idx++);

			}
			s.executeUpdate();
			data.setPresent(true);
			data.setModified(false);
		} catch (SQLException se) {
			throw new ModelException(se);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T newInstance() {
		IEntity e = (IEntity) enhancer.create();

		IEntityData data = new EntityData<T>(this, e, clazz);
		e.setEntityData(data);
		
		return (T) e;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T newInstance(Class<?>[] argumentTypes, Object[] arguments) {
		IEntity e = (IEntity) enhancer.create(argumentTypes, arguments);
		IEntityData data = new EntityData<T>(this, e, clazz);
		e.setEntityData(data);
		
		return (T) e;
	}
	
	@Override
	public T get(String id) throws ModelException {
		return get(id, IProperty.SMALL_DATA);
	}
	
	@Override
	public T get(String id, int type) throws ModelException {
		T e = cache.get(id);
		if (e == null) {
			e = doGet(getConnection(), id, type);
			cache.put(id, e);
		}
		return e;
	}
	
	private T doGet(Connection connection, String id, int type) throws ModelException {
		type |= IProperty.SMALL_DATA;

		T e = newInstance();
		IEntity entity = (IEntity) e;
		IEntityData data = entity.getEntityData();
		String tableName = table.getName();
		
		
		String sql = "SELECT ";
		String where = null;
		boolean first = true;
		Map<String, Property> loadedColumns = new HashMap<String, Property>();
		List<Property> entityProperties = new ArrayList<Property>();
		Iterator<Property> it = list.iterator();
		while (it.hasNext()) {
			final String comma = first ? "" : ",";
			Property p = it.next();
			int ptype = p.getType();
			if ((ptype & type) == 0)
				continue;
			switch (ptype) {
			case IProperty.SMALL_DATA:
			case IProperty.LARGE_DATA:	
			case IProperty.ENTITY:
				if (ptype == IProperty.ENTITY)
					entityProperties.add(p);
				
				IColumn column = p.getColumn();
				String columnName = column.getName();
				
				if (loadedColumns.containsKey(columnName))
					continue;
				
				sql += comma + columnName;
				if (column.isPrimaryKey()) {
					if (where != null)
						throw new ModelException(tableName + " has more than one primary key, cannot get entity by id");
					where = columnName + "=?";
				}
				loadedColumns.put(columnName, p);
				break;
			case IProperty.ENTITY_LIST:
				entityProperties.add(p);
				break;
			default:
				throw new ModelException("Unknown property type.");
			}
			first = false;
		}
		sql += " FROM \"" + tableName + "\" WHERE " + where;
		
		System.out.println(sql);
		
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			s = connection.prepareStatement(sql);
			s.setString(1, id);
			rs = s.executeQuery();
			if (rs.next()) {
				Iterator<String> sit = loadedColumns.keySet().iterator();
				while (sit.hasNext()) {
					Property p = loadedColumns.get(sit.next());
					p.setValue(e, rs);
					data.setLoaded(p.getName(), true);
					//p.setValue(e, rs.getObject(p.getName()));
				}
				
				Iterator<Property> eit = entityProperties.iterator();
				while(eit.hasNext()) {
					this.loadProperty(data, eit.next().getName());
				}
				data.setPresent(true);
				data.setModified(false);
			} else {
				throw new ModelException("no records for id " + id);
			}
			
		} catch (SQLException se) {
			throw new ModelException(se);
		}
		return e;
	}

	@Override
	public void update(T e) throws ModelException {
		doUpdate(getConnection(), e);
	}

	private void doUpdate(Connection connection, T entity) throws ModelException {
		IEntity e = (IEntity) entity;
		IEntityData data = e.getEntityData();
		boolean dataModified = data.isModified();
		if (!dataModified)
			return;
		
		String tableName = table.getName();
		String primaryKey = table.getPrimaryKey();
		Object value = data.getValue(primaryKey);
		if (value == null)
			throw new ModelException("value of primary key not found.");
		String id = value.toString();
		
		String sql = "UPDATE \"" + tableName + "\" SET ";
		boolean first = true;
		List<Property> updateList = new ArrayList<Property>();
		Iterator<Property> it = list.iterator();
		while (it.hasNext()) {
			final String comma = first ? "" : ",";
			Property p = it.next();
			int type = p.getType();
			switch (type) {
			case IProperty.SMALL_DATA:
			case IProperty.LARGE_DATA:	
			case IProperty.ENTITY:				
				IColumn column = p.getColumn();
				String columnName = column.getName();
				boolean modified = data.isModified(p.getName());
				if (modified) {
					sql += comma + columnName + "=?";

					updateList.add(p);
					first = false;
				}
				break;
			case IProperty.ENTITY_LIST:
				break;
			default:
				throw new ModelException("Unknown property type.");
			}
			
		}
		sql += " WHERE " + primaryKey + "=?";
		
		System.out.println(sql);
		
		try {
			PreparedStatement s = connection.prepareStatement(sql);
			Iterator<Property> uit = updateList.iterator();
			int idx = 1;
			while(uit.hasNext()) {				
				Property property = uit.next();
				property.setParameter(entity, s, idx++);			
			}
			s.setString(idx, id);
			s.executeUpdate();
			
			uit = updateList.iterator();
			while(uit.hasNext()) {				
				Property property = uit.next();
				data.setModified(property.getName(), false);		
			}
			data.setModified(false);
		} catch (SQLException se) {
			throw new ModelException(se);
		}
	}

	@Override
	public void delete(T e) throws ModelException {

	}

	@Override
	public void delete(String id) throws ModelException {

	}

	@Override
	public IProperty getProperty(String name) {
		return properties.get(name);
	}

	@Override
	public IProperty[] getProperites() {
		int size = list.size();
		IProperty[] retArray = new IProperty[size];
		list.toArray(retArray);
		return retArray;
	}

	@Override
	public int getCount() throws ModelException {
		return getCount(null);
	}

	@Override
	public int getCount(IFilter filter) throws ModelException {
		Connection connection = getConnection();
		String tableName = table.getName();
		String primaryKey = table.getPrimaryKey();
		
		String sql = "SELECT COUNT(*) AS count FROM \"" + tableName + "\" ";

		if (filter != null)
			sql = filter.generate(sql, primaryKey);

		System.out.println(sql);
		
		PreparedStatement s = null;
		ResultSet rs = null;
		int count = 0;
		try {
			s = connection.prepareStatement(sql);
			
			if (filter != null)
				filter.setParameter(s, 1);
			
			rs = s.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			
		} catch (SQLException se) {
			throw new ModelException(se);
		}
		return count;
	}
	
	@Override
	public List<T> getList() throws ModelException {
		return getList(null);
	}

	@Override
	public List<T> getList(IFilter filter) throws ModelException {
		Connection connection = getConnection();
		
		int type = IProperty.SMALL_DATA;

		String tableName = table.getName();
		String primaryKey = table.getPrimaryKey();
		
		String sql = "SELECT ";

		boolean first = true;
		Map<String, Property> loadedColumns = new HashMap<String, Property>();
		List<Property> entityProperties = new ArrayList<Property>();
		Iterator<Property> it = list.iterator();
		while (it.hasNext()) {
			final String comma = first ? "" : ",";
			Property p = it.next();
			int ptype = p.getType();
			if ((ptype & type) == 0)
				continue;
			switch (ptype) {
			case IProperty.SMALL_DATA:
			case IProperty.LARGE_DATA:	
			case IProperty.ENTITY:
				if (ptype == IProperty.ENTITY)
					entityProperties.add(p);
				
				IColumn column = p.getColumn();
				String columnName = column.getName();
				
				if (loadedColumns.containsKey(columnName))
					continue;
				
				sql += comma + "e." + columnName;

				loadedColumns.put(columnName, p);
				break;
			case IProperty.ENTITY_LIST:
				entityProperties.add(p);
				break;
			default:
				throw new ModelException("Unknown property type.");
			}
			first = false;
		}
		
		sql += " FROM \"" + tableName + "\" e ";
		
		if (filter != null)
			sql = filter.generate(sql, primaryKey);
		
		System.out.println(sql);
		
		PreparedStatement s = null;
		ResultSet rs = null;
		List<T> result = new ArrayList<T>();
		try {
			s = connection.prepareStatement(sql);
			
			if (filter != null)
				filter.setParameter(s, 1);
			
			rs = s.executeQuery();
			while (rs.next()) {
				String id = rs.getString(primaryKey);
				
				
				T e = cache.get(id);
				
				if (e != null) {
					result.add(e);
					System.out.println(id + " cache used.");
					continue;
				}
				
				e = newInstance();
				
				cache.put(id, e);
				
				IEntity entity = (IEntity) e;
				IEntityData data = entity.getEntityData();
				
				Iterator<String> sit = loadedColumns.keySet().iterator();
				while (sit.hasNext()) {
					Property p = loadedColumns.get(sit.next());
					p.setValue(e, rs);
					data.setLoaded(p.getName(), true);
				}
				data.setPresent(true);
				data.setModified(false);
				result.add(e);
				Iterator<Property> eit = entityProperties.iterator();
				while(eit.hasNext()) {
					this.loadProperty(data, eit.next().getName());
				}
			}
			
		} catch (SQLException se) {
			throw new ModelException(se);
		}
		return result;
	}




}
