package org.desking.model.internal.devices;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.desking.model.ILogger;
import org.desking.model.IModelDevice;
import org.desking.model.ModelException;

public abstract class ModelDevice implements IModelDevice {
	protected String url;
	protected Properties properties;
	private boolean driverLoaded;
	private ILogger logger;
	private Connection cachedConnection;
	
	private String name;
	private String version;
	private int majorVersion;
	private int minorVersion;
	private String driverName;
	private String driverVersion;
	private int driverMajorVersion;
	private int driverMinorVersion;
	private int JDBCMajorVersion;
	private int JDBCMinorVersion;
	
	private Schema schema;
	
	public ModelDevice() {
		
	}
	
	public String getUrl() {
		return url;
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
			this.name = meta.getDatabaseProductName();
			this.version = meta.getDatabaseProductVersion();
			this.majorVersion = meta.getDatabaseMajorVersion();
			this.minorVersion = meta.getDatabaseMinorVersion();
			this.driverName = meta.getDriverName();
			this.driverVersion = meta.getDriverVersion();
			this.driverMajorVersion = meta.getDriverMajorVersion();
			this.driverMinorVersion = meta.getDriverMinorVersion();
			this.JDBCMajorVersion = meta.getJDBCMajorVersion();
			this.JDBCMinorVersion = meta.getJDBCMinorVersion();

			loadSchemas(conn, meta);			
		} catch (SQLException e) {
			throw new ModelException(e);
		}
		
	}

	private void loadSchemas(Connection conn, DatabaseMetaData meta) throws SQLException {
		ResultSet rs = meta.getSchemas();
		String name = getCurrentSchema().toUpperCase();
		while (rs.next()) {
			String schemaName = rs.getString("TABLE_SCHEM");
			if (schemaName.toUpperCase().equals(name)) {
				schema = new Schema(schemaName);			
				loadTables(conn, meta, schema);
				break;
			}
		}
	}

	private void loadTables(Connection conn, DatabaseMetaData meta,
			Schema schema) throws SQLException {
		String[] types = {"TABLE", "VIEW"}; 
		ResultSet rs = meta.getTables(null, schema.getName(), "%", types);
		while (rs.next()) {
			String tableName = rs.getString("TABLE_NAME");
			String tableType = rs.getString("TABLE_TYPE");
			Table table = new Table(tableName, tableType);
			schema.addTable(table);

			ResultSet rsPK = meta.getPrimaryKeys(null, schema.getName(), tableName);
			while (rsPK.next()) {
				//String pkName = rsPK.getString("PK_NAME");
				String columnName = rsPK.getString("COLUMN_NAME");
				table.addPrimaryKey(columnName);
			}
			
			loadColumns(conn, table);			
			
			//System.out.println(table);
		}
	}

	private void loadColumns(Connection conn, Table table) throws SQLException {
		String sql = "SELECT * FROM \"" + table.getName() + "\" WHERE 1<0";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData meta = rs.getMetaData();
			int count = meta.getColumnCount();
			for (int i = 1; i <= count; ++i) {
				String name = meta.getColumnName(i);
				String label = meta.getColumnLabel(i);
				String className = meta.getColumnClassName(i);
				int index = i;
				int type = meta.getColumnType(i);
				String typeName = meta.getColumnTypeName(i);
				int length = meta.getPrecision(i);
				int precision = meta.getScale(i);
				int displaySize = meta.getColumnDisplaySize(i);
				int nullable = meta.isNullable(i);
				boolean autoIncrement = meta.isAutoIncrement(i);
				boolean caseSensitive = meta.isCaseSensitive(i);
				boolean searchable = meta.isSearchable(i);
				boolean currency = meta.isCurrency(i);
				boolean signed = meta.isSigned(i);
				boolean readOnly = meta.isReadOnly(i);
				boolean writable = meta.isWritable(i);
				Column column = new Column(name, label, className, index, type,
						typeName, length, precision, displaySize, nullable,
						autoIncrement, caseSensitive, searchable, currency,
						signed, readOnly, writable);
				table.addColumn(column);
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	public ITable getTable(String name) {
		return schema.getTable(name);
	}
	
	public ITable[] getTables() {
		return schema.getTables();
	}
	
	public Connection getCachedConnection() throws ModelException {
		if (cachedConnection == null) {
			cachedConnection = createConnection();
		}
		return cachedConnection;
	}
	
	public Connection createConnection() throws ModelException {
		loadDriver();
		return doCreateConnection();
	}
	
	protected abstract String getCurrentSchema();
	protected abstract Connection doCreateConnection() throws ModelException;
	protected abstract void doLoadDriver() throws ModelException;
	protected abstract void doShutdown();
	
	private void loadDriver() throws ModelException {
		if (driverLoaded) return;
		doLoadDriver();
		driverLoaded = true;
	}
	


	public void shutdown() {
		if (!driverLoaded) 
			return;
		if (cachedConnection != null) {
			try {
				closeConnection(cachedConnection);
			} catch (ModelException e) {
				e.printStackTrace();
			} finally {
				cachedConnection = null;
			}
		}
		doShutdown();
	}
	
	public abstract void closeConnection(Connection connection) throws ModelException;
	
	protected void printSQLException(SQLException e) {
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
	
	public ILogger getLogger() {
		if (logger == null)
			logger = ILogger.DEFAULT;
		return logger;
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
		return driverName;
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
}
