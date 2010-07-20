package org.desking.model.internal.devices;

public interface ICatalog extends IDeviceElement {
	public ISchema getSchema(String name);
	public ISchema[] getSchemas();
}
