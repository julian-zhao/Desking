package org.desking.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.desking.model.internal.devices.ModelDevice;

public class Derby extends ModelDevice implements IModelDevice {
	private final static String DRIVER_CLIENT = "org.apache.derby.jdbc.ClientDriver"; //$NON-NLS-1$
	private final static String DRIVER_EMBEDDED = "org.apache.derby.jdbc.EmbeddedDriver"; //$NON-NLS-1$
	private final static String PROTOCOL = "jdbc:derby:"; //$NON-NLS-1$
	
	private int mode = DERBY_EMBEDDED;
	private String driverClassName = DRIVER_EMBEDDED;

	public Derby(int mode) {
		this.mode = mode;
		if (this.mode == DERBY_CLIENT) {
			driverClassName = DRIVER_CLIENT;
		}
	}

	@Override
	protected Connection doCreateConnection() throws ModelException {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(PROTOCOL + url + ";create=true", properties);
			//connection.setAutoCommit(true);
		} catch (SQLException e) {
			throw new ModelException("Unable to create connection " + url, e);
		}
		return connection;
	}

	@Override
	protected void doLoadDriver() throws ModelException {
		try {
			getLogger().logInfo("Starting Derby in " + getModeName() + " mode.");
			Class.forName(driverClassName).newInstance();
			getLogger().logInfo("Derby started.");
		} catch (InstantiationException e) {
			throw new ModelException("Unable to instantiate the JDBC driver " + driverClassName, e);
		} catch (IllegalAccessException e) {
			throw new ModelException("Not allowed to access the JDBC driver " + driverClassName, e);
		} catch (ClassNotFoundException e) {
			throw new ModelException("Unable to load the JDBC driver " + driverClassName, e);
		}
	}

	private String getModeName() {
		if (mode == DERBY_CLIENT)
			return "CLIENT";
		return "EMBEDDED";
	}
	
	@Override
	protected void doShutdown() {
		try {
			if (mode == DERBY_EMBEDDED) {
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
			}
		} catch (SQLException e) {
			 if (( (e.getErrorCode() == 50000)
                     && ("XJ015".equals(e.getSQLState()) ))) {
                 // we got the expected exception
				 getLogger().logInfo("Derby shut down normally");
                 // Note that for single database shutdown, the expected
                 // SQL state is "08006", and the error code is 45000.
             } else {
                 // if the error code or SQLState is different, we have
                 // an unexpected exception (shutdown failed)
            	 getLogger().logError("Derby did not shut down normally");
                 printSQLException(e);
             }
		}
	}

	@Override
	public void closeConnection(Connection connection) throws ModelException {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			//Cannot close a connection while a transaction is still active.
			//TODO Here must logged and notify the developers to know that his code is wrong.
			if (( (e.getErrorCode() == 20000)
                    && ("25001".equals(e.getSQLState()) ))) {
				
				try {
					connection.rollback();
					connection.close();
					connection = null;
				} catch (SQLException e1) {
					getLogger().logError("Transaction is still active when close.");
				}
				
				//throw new ModelException("Transaction is still active, must be committed or rollbacked.", e);
		
			} else {
				throw new ModelException("Unable to close connection", e);
			}
			
		}
	}

	@Override
	protected String getCurrentSchema() {
		return properties.getProperty("user");
	}


}
