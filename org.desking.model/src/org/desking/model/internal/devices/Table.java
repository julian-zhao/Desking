package org.desking.model.internal.devices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Table implements ITable {
	private Schema schema;
	private String name;
	private String type;
	
	private String primaryKey;
	
	private Set<String> primaryKeys = new HashSet<String>();
	private List<IColumn> columns = new ArrayList<IColumn>();
	
	public Table(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public void addPrimaryKey(String columnName) {
		primaryKeys.add(columnName);
		primaryKey = columnName;
	}
	
	public String getPrimaryKey() {
		return primaryKey;
	}
	
	public void addColumn(Column column) {
		columns.add(column);
		column.setTable(this);
		if (primaryKeys.contains(column.getName()))
			column.setPrimaryKey(true);
	}
	
	public void setSchema(Schema schema) {
		this.schema = schema;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public ISchema getSchema() {
		return schema;
	}

	@Override
	public IColumn getColumn(String name) {
		IColumn[] columns = getColumns();
		Arrays.sort(columns, IDeviceElement.NAME_COMPARATOR);
		int idx = Arrays.binarySearch(columns, name, IDeviceElement.NAME_COMPARATOR);
		if (idx < 0)
			return null;
		return columns[idx];
	}

	@Override
	public IColumn[] getColumns() {
		int size = columns.size();
		IColumn[] retArray = new IColumn[size];
		columns.toArray(retArray);
		return retArray;
	}

	@Override
	public String toString() {
		String s = "Table [name=" + name + ", type=" + type + "]";
		Iterator<IColumn> it = columns.iterator();
		while (it.hasNext())
			s += "\n" + it.next();
		return s;
	}

	@Override
	public String[] getPrimaryKeys() {
		int size = primaryKeys.size();
		String[] retArray = new String[size];
		primaryKeys.toArray(retArray);
		return retArray;
	}

}
