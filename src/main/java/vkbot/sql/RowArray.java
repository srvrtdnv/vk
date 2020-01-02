package vkbot.sql;

import java.util.*;

public class RowArray {
	
	private int cursor = -1;
	private ArrayList<HashMap<String, String>> rows = new ArrayList<HashMap<String, String>>();
	
	public RowArray(ArrayList<HashMap<String, String>> rows) {
		this.rows = rows;
	}
	
	public boolean next() {
		if (++cursor < this.rows.size()) {
			return true;
		}
		return false;
	}
	
	public String getString(String columnName) {
		return this.rows.get(cursor).get(columnName);
	}
	
	public int getInt(String columnName) {
		return Integer.parseInt(this.rows.get(cursor).get(columnName));
	}
	
	public int getRowsCount() {
		return rows.size();
	}
	
	public ArrayList<HashMap<String,String>> getAllRows() {
		return rows;
	}
	
}
