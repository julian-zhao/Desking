package org.desking.model.internal.devices;

public interface IDatabase extends IDeviceElement {
	public String getVersion();
	public int getMajorVersion();
	public int getMinorVersion();
	
	public String getDriverName();
	public String getDriverVersion();
	public int getDriverMajorVersion();
	public int getDriverMinorVersion();
	
	public int getJDBCMajorVersion();
	public int getJDBCMinorVersion();
}
