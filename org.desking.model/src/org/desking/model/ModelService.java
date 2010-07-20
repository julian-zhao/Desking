package org.desking.model;

import org.desking.model.annotation.TABLE;
import org.desking.model.internal.devices.ITable;


public class ModelService implements IModelService {

	private static ModelService instance;
	
	public static ModelService getInstance() {
		if (instance == null)
			instance = new ModelService();
		return instance;
	}
	
	
	private IModelDevice device;
	private ModelRegistry registry;
	
	private ModelService() {		
		device = DeviceFactory.createDevice(IModelDevice.DERBY_CLIENT);
		registry = new ModelRegistry(device);
	}
	
	public void initialize(String url, String username, String password) throws ModelException {
		device.initialize(url, username, password);
	}
	
	@Override
	public <T> IModel<T> getModel(Class<T> clazz) throws ModelException {
		IModel<T> model = registry.getModel(clazz);
		
		return model;
	}
	
	public void shutdown() {
		if (device != null) {
			device.shutdown();
			device = null;
		}
	}

}
