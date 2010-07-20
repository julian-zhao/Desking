package org.desking.model.internal.devices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Schema implements ISchema {

	private Catalog catalog;
	private String name;
	private List<ITable> tables = new ArrayList<ITable>();
	
	public Schema(String name) {
		this.name = name;
	}
	
	public void addTable(Table table) {
		tables.add(table);
		table.setSchema(this);
	}
	
	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public ICatalog getCatalog() {
		return catalog;
	}

	@Override
	public ITable getTable(String name) {
		ITable[] tables = getTables();
		Arrays.sort(tables, IDeviceElement.NAME_COMPARATOR);
		int idx = Arrays.binarySearch(tables, name, IDeviceElement.NAME_COMPARATOR);
		if (idx < 0)
			return null;
		return tables[idx];
	}

	@Override
	public ITable[] getTables() {
		int size = tables.size();
		ITable[] retArray = new ITable[size];
		tables.toArray(retArray);
		return retArray;
	}

}
