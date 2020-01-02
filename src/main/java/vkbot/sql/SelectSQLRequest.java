package vkbot.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class SelectSQLRequest extends AbstractSQLRequest<RowArray> {
	private ArrayList<String> selectingFields = new ArrayList<String>();
	private String whereFields;
	
	public SelectSQLRequest() {
		
	}
	
	public SelectSQLRequest(String database, String tableName, String username, String url, String driver, String passFileName) {
		super(database, tableName, username, url, driver, passFileName);
	}
	
	@Override
	public RowArray execute() {
		try {
			Class.forName(getDriver());
			BufferedReader reader= new BufferedReader(new FileReader(getPassFileName()));
			String pass = reader.readLine();
			reader.close();
			Connection connection = DriverManager.getConnection(getUrl() + getDatabase(), getUsername(), pass);
			String selectingFields = this.selectingFields.get(0);
			for (int index = 1; index < this.selectingFields.size(); index++) {
				selectingFields += "," + this.selectingFields.get(index);
			}
			
			String request = "SELECT " + selectingFields + " FROM " + this.getTableName() + " WHERE " + this.whereFields + ";";
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(request);
			//System.out.println(result.getMetaData().getColumnCount());
			RowArray res = this.convertResultSet(result);
			connection.close();
			return res;
		} catch (Exception e) {
			System.out.println("SelectSQL. " + e);
		}
		
		return null;
	}
	
	public RowArray convertResultSet(ResultSet rs) throws SQLException {
		int count = rs.getMetaData().getColumnCount();
		ArrayList<HashMap<String, String>> rows = new ArrayList<HashMap<String, String>>();
		while (rs.next()) {
			HashMap<String, String> row = new HashMap<String, String>();
			for (int index = 1; index <= count; index++) {
				row.put(rs.getMetaData().getColumnName(index), rs.getString(index));
			}
			rows.add(row);
		}
		return new RowArray(rows);
	}
	
	public void clearSelectingFields() {
		this.selectingFields.clear();
	}
	
	public SelectSQLRequest addSelectingField(String field) {
		selectingFields.add(field);
		return this;
	}
	
	public SelectSQLRequest setWhereFields (String whereFields) {
		this.whereFields = whereFields;
		return this;
	}
	
	public ArrayList<String> getSelectingFields() {
		return this.selectingFields;
	}
	
	public String getWhereFields() {
		return this.whereFields;
	}
}
