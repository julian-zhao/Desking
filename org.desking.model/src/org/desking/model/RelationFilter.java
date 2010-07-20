package org.desking.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RelationFilter extends Filter {

	private String relationTableName;
	private String sourceColumn;
	private String targetColumn;
	private String sourceColumnValue;

	public RelationFilter(String relationTableName, String sourceId,
			String targetId, String id) {
		this.relationTableName = relationTableName;
		this.sourceColumn = sourceId;
		this.targetColumn = targetId;
		this.sourceColumnValue = id;
		setGroup(false);
	}

	@Override
	public String generate(String sql, String primaryKey) {
		//String sql = "SELECT t.* FROM t INNER JOIN r ON r.targetId = t.id WHERE r.sourceId = id";
		sql += " INNER JOIN \"" + relationTableName + "\" r ON r." + targetColumn + "=e." + primaryKey + " WHERE r." + sourceColumn + "=?";
		return sql;
	}

	@Override
	public int setParameter(PreparedStatement s, int currentIndex)
			throws ModelException {
		try {
			s.setObject(currentIndex, sourceColumnValue);
		} catch (SQLException e) {
			throw new ModelException(e);
		}

		return currentIndex + 1;
	}
	
	
}
