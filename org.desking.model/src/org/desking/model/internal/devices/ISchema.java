package org.desking.model.internal.devices;

public interface ISchema extends IDeviceElement {
	public ICatalog getCatalog();
	public ITable getTable(String name);
	public ITable[] getTables();
}
