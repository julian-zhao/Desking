package org.desking.model.internal.devices;

public interface ITable extends IDeviceElement {
	public static final String TABLE = "TABLE";
	public static final String VIEW = "VIEW";
	public static final String SYSTEM_TABLE = "SYSTEM TABLE";
	public static final String GLOBAL_TEMPORARY = "GLOBAL TEMPORARY";
	public static final String LOCAL_TEMPORARY = "LOCAL TEMPORARY";
	public static final String ALIAS = "ALIAS";
	public static final String SYNONYM = "SYNONYM";
	
	public String getType();
	
	public ISchema getSchema();
	public IColumn getColumn(String name);
	public IColumn[] getColumns();
	
	public String getPrimaryKey();
	public String[] getPrimaryKeys();
}
