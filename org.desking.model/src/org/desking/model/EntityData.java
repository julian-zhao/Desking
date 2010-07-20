package org.desking.model;

import java.util.HashMap;
import java.util.Map;

public class EntityData<T> implements IEntityData {
	private IModel<T> model;
	private Class<T> clazz;
	private Object entity;
	private int state;
	private Map<String, Integer> propertyStates = new HashMap<String, Integer>();
	
	public EntityData(IModel<T> model, Object entity, Class<T> clazz) {
		this.model = model;
		this.clazz = clazz;
		this.entity = entity;
	}
	
	@Override
	public boolean isPresent() {
		return (state & PRESENT) != 0;
	}

	@Override
	public void setPresent(boolean present) {
		if (present)
			state |= PRESENT;
		else
			state &= ~PRESENT;
	}

	@Override
	public boolean isModified() {
		return (state & MODIFIED) != 0;
	}

	@Override
	public void setModified(boolean modified) {
		if (modified)
			state |= MODIFIED;
		else
			state &= ~MODIFIED;
	}

	@Override
	public boolean isDeleted() {
		return (state & DELETED) != 0;
	}

	@Override
	public void setDeleted(boolean deleted) {
		if (deleted)
			state |= DELETED;
		else
			state &= ~DELETED;
	}

	@Override
	public Object getValue(String name) throws ModelException {
		IProperty property = model.getProperty(name);
		if (property == null)
			throw new IllegalArgumentException("Property not exists : " + name);
		return property.getValue(entity);
	}

	@Override
	public void setValue(String name, Object value) throws ModelException {
		IProperty property = model.getProperty(name);
		if (property == null)
			throw new IllegalArgumentException("Property not exists : " + name);
		property.setValue(entity, value);
	}

	@Override
	public IProperty getProperty(String name) {
		return model.getProperty(name);
	}

	@Override
	public IProperty[] getProperites() {
		return model.getProperites();
	}

	private int getPropertyState(String name) {
		IProperty property = model.getProperty(name);
		if (property == null)
			throw new IllegalArgumentException("Property not exists : " + name);
		Integer value = propertyStates.get(name);
		if (value == null) {
			value = 0;
			propertyStates.put(name, value);
		}
		return value;
	}
	
	@Override
	public boolean isModified(String name) {
		int value = getPropertyState(name);
		
		return (value & COLUMN_MODIFIED) != 0;
	}

	@Override
	public boolean isLoaded(String name) {
		int value = getPropertyState(name);
		
		return (value & COLUMN_LOADED) != 0;
	}

	@Override
	public void setLoaded(String name, boolean loaded) {
		int value = getPropertyState(name);
		if (loaded)
			value |= COLUMN_LOADED;
		else
			value &= (~COLUMN_LOADED);
		propertyStates.put(name, value);
	}

	@Override
	public void setModified(String name, boolean modified) {
		int value = getPropertyState(name);
		if (modified) 
			value |= COLUMN_MODIFIED;
		else
			value &= (~COLUMN_MODIFIED);
		propertyStates.put(name, value);
	}

}
