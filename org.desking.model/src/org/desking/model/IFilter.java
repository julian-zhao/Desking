package org.desking.model;

import java.sql.PreparedStatement;

public interface IFilter {
	public static final int NOT				= 1 << 0;
	public static final int EQUALS			= 1 << 1;
	public static final int NOT_EQUALS		= NOT | EQUALS;
	
	public static final int LIKE			= 1 << 2;
	public static final int NOT_LIKE		= NOT | LIKE;
	
	public static final int GREATER_THAN	= 1 << 3;
	
	public static final int LESS_THAN		= 1 << 4;
	
	public static final int IN				= 1 << 5;
	public static final int NOT_IN			= NOT | IN;
	
	public static final int ISNULL			= 1 << 6;
	public static final int IS_NOT_NULL		= NOT | ISNULL;
	
	public static final int BETWEEN			= 1 << 7;
	public static final int NOT_BETWEEN		= NOT | BETWEEN;
	
	public static final int EXISTS			= 1 << 8;
	public static final int NOT_EXISTS		= NOT | EXISTS;
	
	public static final int AND				= 1 << 10;
	public static final int OR				= 1 << 11;
	
	public void add(IFilter filter);
	public void add(String propertyName, int op, Object value);

	//public void addNot(String propertyName, int op, Object value);
	//public void addBetween(String propertyName, Object value1, Object value2);
	
	public String generate(String sql, String primaryKey);
	public int setParameter(PreparedStatement s, int currentIndex) throws ModelException;
}
