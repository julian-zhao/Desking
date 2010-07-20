package org.desking.model;


import java.sql.SQLException;

public class ModelException extends Exception {

	private static final long serialVersionUID = -7562128346203344082L;

	public ModelException() {
		super();
	}
	
	public ModelException(String message) {
		super(message);
	}
	
	public ModelException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ModelException(Throwable cause) {
		super(cause);
	}
	
	public int getSQLErrorCode() {
		Throwable cause = getCause();
		if (cause != null && cause instanceof SQLException)
			return ((SQLException) cause).getErrorCode();
		return 0;
	}
	
	public String getSQLState() {
		Throwable cause = getCause();
		if (cause != null && cause instanceof SQLException)
			return ((SQLException) cause).getSQLState();
		return "";
	}
}
