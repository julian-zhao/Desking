package org.desking.model.internal.devices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Catalog implements ICatalog {

	private String name;
	private List<ISchema> schemas = new ArrayList<ISchema>();
	
	public Catalog(String name) {
		this.name = name;
	}
	
	public void addSchema(Schema schema) {
		schemas.add(schema);
		schema.setCatalog(this);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public ISchema getSchema(String name) {
		ISchema[] schemas = getSchemas();
		Arrays.sort(schemas, IDeviceElement.NAME_COMPARATOR);
		int idx = Arrays.binarySearch(schemas, name);
		if (idx < 0)
			return null;
		return schemas[idx];
	}

	@Override
	public ISchema[] getSchemas() {
		int size = schemas.size();
		ISchema[] retArray = new ISchema[size];
		schemas.toArray(retArray);
		return retArray;
	}

}
