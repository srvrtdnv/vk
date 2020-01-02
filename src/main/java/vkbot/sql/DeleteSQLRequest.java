package vkbot.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DeleteSQLRequest extends AbstractSQLRequest<Integer> {
	private String whereFields;
	
	public DeleteSQLRequest() {
		
	}
	
	public DeleteSQLRequest(String database, String tableName, String username, String url, String driver, String passFileName) {
		super(database, tableName, username, url, driver, passFileName);
	}
	
	@Override
	public Integer execute() {
		try {
			Class.forName(getDriver());
			BufferedReader reader = new BufferedReader(new FileReader(getPassFileName()));
			String pass = reader.readLine();
			reader.close();
			Connection connection = DriverManager.getConnection(getUrl() + getDatabase(), getUsername(), pass);
			Statement statement = connection.createStatement();
			String request = "DELETE FROM " + getTableName();
			if (!(this.whereFields == null || this.whereFields.equals(""))) request += " WHERE " + this.whereFields + ";";
			else request += ";";
			Integer result = statement.executeUpdate(request);
			connection.close();
			return result;
		} catch(Exception e) {
			System.out.println("DELETE SQL: " + whereFields + "\n" + e);
		}
		return -1;
	}
	
	public DeleteSQLRequest setWhereFields (String whereFields) {
		this.whereFields = whereFields;
		return this;
	}

}
