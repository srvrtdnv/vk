package vkbot.dao;

import java.util.ArrayList;
import java.util.List;

import vkbot.AutoNotification;
import vkbot.ProcessingCenter;
import vkbot.sql.DeleteSQLRequest;
import vkbot.sql.InsertSQLRequest;
import vkbot.sql.RowArray;
import vkbot.sql.SelectSQLRequest;

public class JDBCAutoNotificationDAO extends AutoNotificationDAO {

	@Override
	public List<AutoNotification> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllUserIdsByDDT(AutoNotification autoN) {
		ProcessingCenter pCenter = this.getPCenter();
		SelectSQLRequest selectRequest = new SelectSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("user_id").setWhereFields("time_from < " + autoN.getTime() + " AND time_to > " + autoN.getTime() + " AND direction = " + autoN.getDirection() + " AND day = " + autoN.getDay());
		RowArray res = selectRequest.execute();
		List<String> userIds = new ArrayList<String>();
		while (res.next()) {
			userIds.add(res.getString("user_id"));
		}
		return userIds;
	}

	@Override
	public List<AutoNotification> getAllByDD(AutoNotification autoN) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AutoNotification> getAllByUserId(String userId) {
		List<AutoNotification> result = new ArrayList<AutoNotification>();
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		SelectSQLRequest request = new SelectSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("*").setWhereFields("user_id = " + userId);
		RowArray rs = request.execute();
		while (rs.next()) {
			int id = rs.getInt("id");
			int direction = rs.getInt("direction");
			int day = rs.getInt("day");
			int timeFrom = rs.getInt("time_from");
			int timeTo = rs.getInt("time_to");
			result.add(new AutoNotification().setId(id).setDirection(direction).setDay(day).setTimeFrom(timeFrom).setTimeTo(timeTo));
		}
		return result;
	}

	@Override
	public int save(AutoNotification autoN) {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		InsertSQLRequest request = new InsertSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName());
		request.putValue("time_from", "" + autoN.getTimeFrom());
		request.putValue("time_to", "" + autoN.getTimeTo());
		request.putValue("user_id", "\"" + autoN.getUserId() + "\"");
		request.putValue("day", "" + autoN.getDay());
		request.putValue("direction", "" + autoN.getDirection());
		return request.execute();
	}

	@Override
	public int remove(AutoNotification autoN) {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		DeleteSQLRequest request = new DeleteSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields(" id = " + autoN.getId());
		return request.execute();
	}

	@Override
	public boolean isAutoNExist(AutoNotification autoN) {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		SelectSQLRequest request = new SelectSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("*").setWhereFields(" user_id = \"" + autoN.getUserId() + "\" AND direction = " + autoN.getDirection() + " AND day = " + autoN.getDay());
		return request.execute().next();
	}

}
