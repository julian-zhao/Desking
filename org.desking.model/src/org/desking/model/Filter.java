package org.desking.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Filter implements IFilter {

	private boolean and;
	private boolean group;
	private List<IFilter> filters;
	
	private String propertyName;
	private int op;
	private Object value;
	
	public Filter() {
		this(true);
	}
	
	public Filter(boolean and) {
		this.and = and;
		this.group = true;
	}
	
	public Filter(String propertyName, int op, Object value) {
		this.group = false;
		this.propertyName = propertyName;
		this.op = op;
		this.value = value;
	}
	
	protected void setGroup(boolean group) {
		this.group = group;
	}
	
	@Override
	public void add(IFilter filter) {
		if (!group) 
			throw new IllegalStateException("cannot add child filter into a non-group filter.");
		if (filters == null)
			filters = new ArrayList<IFilter>();
		filters.add(filter);
	}

	@Override
	public void add(String propertyName, int op, Object value) {
		add(new Filter(propertyName, op, value));
	}

	@Override
	public String generate(String sql, String primaryKey) {
		String s = "";
		if (group) {
			Iterator<IFilter> it = filters.iterator();
			String sg = and ? " AND ":" OR ";
			s += " (";
			boolean first = true;
			while (it.hasNext()) {
				s += (first ? "" : sg) + it.next();
				first = false;
			}
			s += ") ";
		} else {
			s = " (" + propertyName + " " + getSymbol() + " ?) ";
		}
		return sql + " WHERE " + s;
	}
	
	@Override
	public int setParameter(PreparedStatement s, int currentIndex) throws ModelException {
		if (group) {
			Iterator<IFilter> it = filters.iterator();
			while (it.hasNext()) {
				currentIndex = it.next().setParameter(s, currentIndex);
			}
		} else {
			try {
				s.setObject(currentIndex, value);
			} catch (SQLException e) {
				throw new ModelException(e);
			}
		}
		return currentIndex + 1;
	}
	
	@Override
	public String toString() {
		String s = "";
		if (group) {
			Iterator<IFilter> it = filters.iterator();
			String sg = and ? " AND ":" OR ";
			s += " ( ";
			boolean first = true;
			while (it.hasNext()) {
				s += (first ? "" : sg) + it.next();
				first = false;
			}
			s += " ) ";
		} else {
			s = " ( " + propertyName + " " + getSymbol() + " " + value.toString() + " ) ";
		}
		return s;
	}

	private String getSymbol() {
		switch (op) {
		case EQUALS:
			return "=";
		case NOT_EQUALS:
			return "<>";
		case LIKE:
			return "LIKE";
		case NOT_LIKE:
			return "NOT LIKE";
		case GREATER_THAN:
			return ">";
		case GREATER_THAN | EQUALS:
			return ">=";
		case LESS_THAN:
			return "<";
		case LESS_THAN | EQUALS:
			return "<=";
		case IN:
			return "IN";
		case NOT_IN:
			return "NOT IN";
		}
		return "UNKNOWNS";
	}

}
