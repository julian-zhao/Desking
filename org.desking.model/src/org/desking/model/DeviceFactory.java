package org.desking.model;

public class DeviceFactory {
	public static IModelDevice createDevice(int type) {
		switch (type) {
		case IModelDevice.DERBY_CLIENT:
			return new Derby(IModelDevice.DERBY_CLIENT);
		case IModelDevice.DERBY_EMBEDDED:
			return new Derby(IModelDevice.DERBY_EMBEDDED);
		default:
			throw new IllegalArgumentException("Unsupported device type : " + type);
		}
	}
}
