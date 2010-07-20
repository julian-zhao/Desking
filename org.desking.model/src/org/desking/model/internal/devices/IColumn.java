package org.desking.model.internal.devices;

import java.sql.ResultSetMetaData;

public interface IColumn extends IDeviceElement {
	public static final int COLUMN_NO_NULLS = ResultSetMetaData.columnNoNulls;
	public static final int COLUMN_NULLABLE = ResultSetMetaData.columnNullable;
	public static final int COLUMN_NULLABLEUNKNOWN = ResultSetMetaData.columnNullableUnknown;
	
	public ITable getTable();
	public String getLabel();
	public String getClassName();
	
	public int getIndex(); //ORDINAL_POSITION index of column in table (starting at 1)
	public int getType(); //java.sql.Types
	public String getTypeName();
	public int getLength();
	public int getPrecision();
	public int getDisplaySize();
	
	public int isNullable();
	public boolean isAutoIncrement();
	public boolean isCaseSensitive();
	public boolean isSearchable();
	public boolean isCurrency();
	public boolean isSigned();
	public boolean isReadOnly();
	public boolean isWritable();
	
	public boolean isPrimaryKey();
}
