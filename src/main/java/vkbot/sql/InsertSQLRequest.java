package vkbot.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class InsertSQLRequest extends AbstractSQLRequest<Integer> {
	private Map<String,String> values = new HashMap<String,String>();
	
	public InsertSQLRequest() {
		
	}
	
	public InsertSQLRequest(String database, String tableName, String username, String url, String driver, String passFileName) {
		super(database, tableName, username, url, driver, passFileName);
	}
	
	@Override
	public Integer execute() {
		try {
			String columnsName = "(";
			String values = "(";
			Class.forName(getDriver());
			BufferedReader reader = new BufferedReader(new FileReader(getPassFileName()));
			String pass = reader.readLine();
			reader.close();
			Connection connection = DriverManager.getConnection(getUrl() + getDatabase(), getUsername(), pass);
			Statement statement = connection.createStatement();
			for (Map.Entry<String,String> pair : this.values.entrySet()) {
				columnsName += pair.getKey() + ",";
				values += pair.getValue() + ",";
			}
			columnsName = columnsName.substring(0, columnsName.length() - 1);
			columnsName += ")";
			values = values.substring(0, values.length() - 1);
			values += ")";
			Integer result = statement.executeUpdate("INSERT INTO " + getTableName() + " " + columnsName + " VALUES " + values + ";");
			connection.close();
			return result;
		} catch(Exception e) {
		}
		return -1;
	}
	
	public void putValue(String columnName, String value) {
		values.put(columnName, value);
	}
	public void clearValues() {
		values.clear();
	}
}
