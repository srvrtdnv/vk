package vkbot.sql;

public class ConcretTablesManager extends AbstractTablesManager {
		@SuppressWarnings("unchecked")
		public <T> T sendRequest(AbstractSQLRequest<T> request) {
			if (1 > 0) {
				return (T) returnStr();
			}
			return null;
		}
		public String returnStr() {
			return new String();
		}
}
