package org.desking.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.desking.model.internal.devices.IColumn;

public interface IProperty {
	public static final int SMALL_DATA 	= 1 << 0;
	public static final int LARGE_DATA 	= 1 << 1;
	public static final int ENTITY 		= 1 << 2;
	public static final int ENTITY_LIST = 1 << 3;

	public String getName();
	public int getType();
	public IColumn getColumn();
	
	public Object getValue(Object obj) throws ModelException;
	public void setValue(Object obj, Object value) throws ModelException;
	
	public void setParameter(Object obj, PreparedStatement stmt, int parameterIndex) throws ModelException;
	public void setValue(Object obj, ResultSet resultSet) throws ModelException;
}
