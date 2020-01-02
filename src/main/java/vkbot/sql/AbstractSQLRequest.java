package vkbot.sql;


public abstract class AbstractSQLRequest<T> {
	private String passFileName, database, tableName, username, url, driver, request;
	
	public AbstractSQLRequest() {
		
	}
	
	public AbstractSQLRequest(String database, String tableName, String username, String url, String driver, String passFileName) {
		this.database = database;
		this.setTableName(tableName);
		this.driver = driver;
		this.passFileName = passFileName;
		this.url = url;
		this.username = username;
	}
	
	public abstract T execute();
	
	public String getRequest() {
		return request;
	}
	
	public AbstractSQLRequest<T> setDatabase(String database) {
		this.database = database;
		return this;
	}
	
	public AbstractSQLRequest<T> setUsername(String username) {
		this.username = username;
		return this;
	}
	
	public AbstractSQLRequest<T> setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public AbstractSQLRequest<T> setDriver(String driver) {
		this.driver = driver;
		return this;
	}

	public String getDatabase() {
		return database;
	}

	public String getUsername() {
		return username;
	}

	public String getUrl() {
		return url;
	}

	public String getDriver() {
		return driver;
	}

	public String getPassFileName() {
		return passFileName;
	}

	public AbstractSQLRequest<T> setPassFileName(String passFileName) {
		this.passFileName = passFileName;
		return this;
	}

	public String getTableName() {
		return tableName;
	}

	public AbstractSQLRequest<T> setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}
}
