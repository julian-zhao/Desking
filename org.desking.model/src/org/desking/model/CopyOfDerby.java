package org.desking.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.desking.model.internal.devices.ITable;

public class CopyOfDerby implements IModelDevice {
	private final static String DRIVER_CLIENT = "org.apache.derby.jdbc.ClientDriver"; //$NON-NLS-1$
	private final static String DRIVER_EMBEDDED = "org.apache.derby.jdbc.EmbeddedDriver"; //$NON-NLS-1$
	private final static String PROTOCOL = "jdbc:derby:"; //$NON-NLS-1$
	
	private int mode = DERBY_EMBEDDED;
	private String driverClassName = DRIVER_EMBEDDED;
	private String url;
	private Properties properties;
	private boolean driverLoaded;
	private ILogger logger;
	private Connection cachedConnection;
	
	private String name;
	private String version;
	private int majorVersion;
	private int minorVersion;
	private String dirverName;
	private String driverVersion;
	private int driverMajorVersion;
	private int driverMinorVersion;
	private int JDBCMajorVersion;
	private int JDBCMinorVersion;
	
	public CopyOfDerby(int mode) {
		this.mode = mode;
		if (this.mode == DERBY_CLIENT) {
			driverClassName = DRIVER_CLIENT;
		}
	}
	
	public void initialize(String url, String username, String password) throws ModelException {
		Properties properties = new Properties();
		if (username != null)
			properties.put("user", username);
		if (password != null)
			properties.put("password", password);
		initialize(url, properties);
	}
	
	public void initialize(String url, Properties properties) throws ModelException {
		this.url = url;
		this.properties = properties;
		loadDriver();
		getCachedConnection();
		loadElements();
	}
	
	private void loadElements() throws ModelException {
		Connection conn = getCachedConnection();
		try {			
			DatabaseMetaData meta = conn.getMetaData();
			String dbProductName = meta.getDatabaseProductName();
			String dbProductVersion = meta.getDatabaseProductVersion();
			
			System.out.println(dbProductName + " " + dbProductVersion);
			
			String[] types = {"TABLE", "VIEW"}; 

			ResultSet rs = meta.getSchemas();
			
			while (rs.next()) {
				System.out.println(rs.getString("TABLE_SCHEM"));
			}
			
			rs = meta.getTables(null, null, null, types);
			while (rs.next()) {
				String schema = rs.getString("TABLE_SCHEM");
				String table = rs.getString("TABLE_NAME");
				String type = rs.getString("TABLE_TYPE");
				System.out.println(type + "  [" + schema + "].[" + table + "]");
			}
			
			rs = meta.getColumns(null, null, "Customer", null);
			
			while (rs.next()) {
				String table = rs.getString("TABLE_NAME");
				String column = rs.getString("COLUMN_NAME");
				System.out.println(table + "." + column);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public Connection getCachedConnection() throws ModelException {
		if (cachedConnection == null) {
			cachedConnection = createConnection();
		}
		return cachedConnection;
	}
	
	public Connection createConnection() throws ModelException {
		loadDriver();
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(PROTOCOL + url + ";create=true", properties);
			//connection.setAutoCommit(true);
		} catch (SQLException e) {
			throw new ModelException("Unable to create connection " + url, e);
		}
		return connection;
	}
	
	private void loadDriver() throws ModelException {
		if (driverLoaded) return;

		try {
			getLogger().logInfo("Starting Derby in " + getModeName() + " mode.");
			Class.forName(driverClassName).newInstance();
			driverLoaded = true;
			getLogger().logInfo("Derby started.");
		} catch (InstantiationException e) {
			throw new ModelException("Unable to instantiate the JDBC driver " + driverClassName, e);
		} catch (IllegalAccessException e) {
			throw new ModelException("Not allowed to access the JDBC driver " + driverClassName, e);
		} catch (ClassNotFoundException e) {
			throw new ModelException("Unable to load the JDBC driver " + driverClassName, e);
		}

	}
	
	public void shutdown() {
		if (!driverLoaded) 
			return;
		try {
			if (cachedConnection != null) {
				cachedConnection.close();
				cachedConnection = null;
			}
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
	
	private void printSQLException(SQLException e) {
		// Unwraps the entire exception chain to unveil the real cause of the
		// Exception.
		while (e != null) {
			System.err.println("\n----- SQLException -----");
			System.err.println("  SQL State:  " + e.getSQLState());
			System.err.println("  Error Code: " + e.getErrorCode());
			System.err.println("  Message:    " + e.getMessage());
			// for stack traces, refer to derby.log or uncomment this:
			// e.printStackTrace(System.err);
			e = e.getNextException();
		}
	}
	
	private ILogger getLogger() {
		if (logger == null)
			logger = ILogger.DEFAULT;
		return logger;
	}
	
	private String getModeName() {
		if (mode == DERBY_CLIENT)
			return "CLIENT";
		return "EMBEDDED";
	}
	
	public void setLogger(ILogger logger) {
		this.logger = logger;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public int getMajorVersion() {
		return majorVersion;
	}

	@Override
	public int getMinorVersion() {
		return minorVersion;
	}

	@Override
	public String getDriverName() {
		return dirverName;
	}

	@Override
	public String getDriverVersion() {
		return driverVersion;
	}

	@Override
	public int getDriverMajorVersion() {
		return driverMajorVersion;
	}

	@Override
	public int getDriverMinorVersion() {
		return driverMinorVersion;
	}

	@Override
	public int getJDBCMajorVersion() {
		return JDBCMajorVersion;
	}

	@Override
	public int getJDBCMinorVersion() {
		return JDBCMinorVersion;
	}

	@Override
	public ITable getTable(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITable[] getTables() {
		// TODO Auto-generated method stub
		return null;
	}
}
