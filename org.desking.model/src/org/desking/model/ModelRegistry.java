package org.desking.model;

import java.util.HashMap;
import java.util.Map;

import org.desking.model.annotation.TABLE;
import org.desking.model.internal.devices.ITable;

public class ModelRegistry {

	private IModelDevice device;
	
	private Map<Class<?>, IModel<?>> models = new HashMap<Class<?>, IModel<?>>();
	
	private Map<String, IModelHandler<?>> handlers = new HashMap<String, IModelHandler<?>>();
	
	public ModelRegistry(IModelDevice device) {
		this.device = device;
	}
	
	@SuppressWarnings("unchecked")
	public <T> IModel<T> getModel(Class<T> clazz) throws ModelException {
		if (clazz == null)
			throw new IllegalArgumentException("class for model cannot be null.");
		IModel<?> model = models.get(clazz);
		
		if (model == null) {
			model = createModel(clazz);
			models.put(clazz, model);
		}
		return (IModel<T>) model;
	}
	
	private <T> IModel<T> createModel(Class<T> clazz) throws ModelException {
		if (!clazz.isAnnotationPresent(TABLE.class))
			throw new IllegalArgumentException("table of " + clazz.getName() + " not defined.");
		String tableName = clazz.getAnnotation(TABLE.class).value();
		ITable table = device.getTable(tableName);
		
		if (table == null)
			throw new ModelException("table '" + tableName + "' defined in " + clazz.getName() + "is not found in device.");
		
		IModel<T> model = new Model<T>(this, device, clazz, table);
		return model;
	}
	
	public <T> IModelHandler<T> getModelHandler(Class<T> clazz) throws ModelException {
		String key = clazz.getName();
		@SuppressWarnings("unchecked")
		IModelHandler<T> handler = (IModelHandler<T>) handlers.get(key);
		if (handler == null) {
			handler = new ModelHandler<T>(device, clazz);
			handlers.put(key, handler);
		}
		return handler;
	}
	
}
