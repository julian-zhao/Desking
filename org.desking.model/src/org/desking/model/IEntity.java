package org.desking.model;

public interface IEntity {
	public static final int DATA_LOADED		= 1 << 0;
	public static final int DATA_MODIFIED	= 1 << 1;
	

	public IEntityData getEntityData();
	public void setEntityData(IEntityData data);
	
}
