package org.desking.model;

import java.sql.Connection;
import java.util.Properties;

import org.desking.model.internal.devices.IDeviceElement;
import org.desking.model.internal.devices.ITable;

public interface IModelDevice extends IDeviceElement {
	public final static int DERBY_EMBEDDED 	= 1;
	public final static int DERBY_CLIENT	= 2;
	
	public final static int SQLITE			= 3; //Not supported yet
	public final static int FIREBIRD		= 4; //Not supported yet
	public final static int MYSQL			= 5; //Not supported yet
	public final static int MSSQLSERVER		= 6; //Not supported yet
	public final static int ORACLE			= 7; //Not supported yet
	
	public void initialize(String url, String username, String password) throws ModelException;
	public void initialize(String url, Properties properties) throws ModelException;
	public Connection getCachedConnection() throws ModelException;
	public Connection createConnection() throws ModelException;
	
	public void shutdown();
	public void setLogger(ILogger logger);
	
	public ITable getTable(String name);
	public ITable[] getTables();
	
	public String getName();
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
