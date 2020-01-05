package vkbot.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import vkbot.ProcessingCenter;

public class UpdateSQLRequest extends AbstractSQLRequest<Integer> {
	private String whereFields;
	private Map<String,String> values = new HashMap<String,String>();
	
	
	public UpdateSQLRequest() {
		
	}
	
	public UpdateSQLRequest(String database, String tableName, String username, String url, String driver, String passFileName) {
		super(database, tableName, username, url, driver, passFileName);
	}

	@Override
	public Integer execute() {
		try {
			String str = "";
			Class.forName(getDriver());
			BufferedReader reader= new BufferedReader(new FileReader(getPassFileName()));
			String pass = reader.readLine();
			reader.close();
			Connection connection = DriverManager.getConnection(getUrl() + getDatabase(), getUsername(), pass);
			for (Map.Entry<String,String> pair : this.values.entrySet()) {
				str += pair.getKey() + " = " + pair.getValue() + ", ";
			}
			str = str.substring(0, str.length() - 2);
			String request = "UPDATE " + getTableName() + " SET " + str;
			if (!(this.whereFields == null || this.whereFields.equals(""))) request += " WHERE " + this.whereFields + ";";
			else request += ";";
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate(request);
			connection.close();
			return result;
		} catch (Exception e) {
			ProcessingCenter.logError(e);
		}
		return -1;
	}

	public String getWhereFields() {
		return whereFields;
	}

	public UpdateSQLRequest setWhereFields(String whereFields) {
		this.whereFields = whereFields;
		return this;
	}
	
	public UpdateSQLRequest putValue(String columnName, String value) {
		values.put(columnName, value);
		return this;
	}
	public void clearValues() {
		values.clear();
	}

}
