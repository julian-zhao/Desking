package org.desking.model;

import java.lang.reflect.Field;

public abstract class Property implements IProperty {

	private String name;
	private Field field;
	private int type;

	public Property(Field field, String name, int type) {
		this.field = field;
		this.name = name;
		this.type = type;
	}

	public Field getField() {
		return field;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public Object getValue(Object obj) throws ModelException {
		try {
			return field.get(obj);
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	@Override
	public void setValue(Object obj, Object value) throws ModelException {
		try {
			field.set(obj, value);
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

}
