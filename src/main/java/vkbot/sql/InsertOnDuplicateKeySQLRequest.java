package vkbot.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class InsertOnDuplicateKeySQLRequest extends AbstractSQLRequest<Integer> {
	private String primColumnName, primValue;
	private Map<String,String> values = new HashMap<String,String>();

	public InsertOnDuplicateKeySQLRequest() {
		
	}
	
	public InsertOnDuplicateKeySQLRequest(String database, String tableName, String username, String url, String driver, String passFileName) {
		super(database, tableName, username, url, driver, passFileName);
	}
	@Override
	public Integer execute() {
		try {
			String columnsName = "(" + primColumnName + ",";
			String values = "(" + primValue + ",";
			String update = "";
			Class.forName(getDriver());
			BufferedReader reader = new BufferedReader(new FileReader(getPassFileName()));
			String pass = reader.readLine();
			reader.close();
			Connection connection = DriverManager.getConnection(getUrl() + getDatabase(), getUsername(), pass);
			Statement statement = connection.createStatement();
			for (Map.Entry<String,String> pair : this.values.entrySet()) {
				columnsName += pair.getKey() + ",";
				values += pair.getValue() + ",";
				update += pair.getKey() + " = " + pair.getValue() + ",";
			}
			columnsName = columnsName.substring(0, columnsName.length() - 1);
			columnsName += ")";
			values = values.substring(0, values.length() - 1);
			values += ")";
			update = update.substring(0, update.length() - 1);
			Integer result = statement.executeUpdate("INSERT INTO " + getTableName() + " " + columnsName + " VALUES " + values + " ON DUPLICATE KEY UPDATE " + update + ";");
			connection.close();
			return result;
		} catch (Exception e) {
			System.out.println("INSERT ON UPDATE: " + e);
		}
		return -1;
	}
	
	public InsertOnDuplicateKeySQLRequest putValue(String columnName, String value) {
		values.put(columnName, value);
		return this;
	}
	
	public InsertOnDuplicateKeySQLRequest clearValues() {
		values.clear();
		return this;
	}
	
	public InsertOnDuplicateKeySQLRequest setPrimData(String columnName, String value) {
		this.primColumnName = columnName;
		this.primValue = value;
		return this;
	}

}
