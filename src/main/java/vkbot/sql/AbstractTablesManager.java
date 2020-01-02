package vkbot.sql;

import java.util.ArrayList;
import java.util.Map;

public abstract class AbstractTablesManager {
		String database, password, username;
		
		public void setDatabase(String database) {
			this.database = database;
		}
		
		public void setUsername(String username) {
			this.username = username;
		}
		
		public void setPassword(String pass) {
			this.password = pass;
		}
		
		public abstract <T> T sendRequest(AbstractSQLRequest<T> query);
		
		
}
