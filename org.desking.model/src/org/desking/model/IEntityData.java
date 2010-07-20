package org.desking.model;

public interface IEntityData {
	public static final int PRESENT	= 1 << 0;
	public static final int MODIFIED = 1 << 1;
	public static final int DELETED	= 1 << 3;

	public static final int COLUMN_LOADED = 1 << 0;
	public static final int COLUMN_MODIFIED = 1 << 1;
	
	//entity state
	public boolean isPresent();
	public void setPresent(boolean present);
	public boolean isModified();
	public void setModified(boolean modified);
	public boolean isDeleted();
	public void setDeleted(boolean deleted);
	
	public IProperty getProperty(String name);
	public IProperty[] getProperites();

	public boolean isModified(String name);
	public boolean isLoaded(String name);
	public void setLoaded(String name, boolean loaded);
	public void setModified(String name, boolean modified);
	
	public Object getValue(String name) throws ModelException;
	public void setValue(String name, Object value) throws ModelException;
}
