package vkbot;

import vkbot.sql.DeleteSQLRequest;
import vkbot.sql.InsertSQLRequest;
import vkbot.sql.SelectSQLRequest;

public class AutoNotification implements Deletable {
	
	private String id, timeFrom, timeTo, direction, day, userId, time;
	
	@Override
	public int deleteFromTable() {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		DeleteSQLRequest request = new DeleteSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields(" id = " + this.getId());
		int result = request.execute();
		return result;
	}
	
	public int post() {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		InsertSQLRequest request = new InsertSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName());
		request.putValue("time_from", timeFrom);
		request.putValue("time_to", timeTo);
		request.putValue("user_id", "\"" + userId + "\"");
		request.putValue("day", day);
		request.putValue("direction", direction);
		int result = request.execute();
		return result;
	}
	
	public boolean isExist() {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		SelectSQLRequest request = new SelectSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("*").setWhereFields(" user_id = \"" + getUserId() + "\" AND direction = " + getDirection() + " AND day = " + getDay());
		return request.execute().next();
	}

	public AutoNotification setId(String id) {
		this.id = id;
		return this;
	}

	public AutoNotification setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
		return this;
	}

	public AutoNotification setTimeTo(String timeTo) {
		this.timeTo = timeTo;
		return this;
	}
	
	public AutoNotification setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getDirection() {
		return direction;
	}

	public AutoNotification setDay(String day) {
		this.day = day;
		return this;
	}

	public String getId() {
		return id;
	}

	public String getTimeFrom() {
		return timeFrom;
	}

	public String getTimeTo() {
		return timeTo;
	}

	public AutoNotification setDirection(String direction) {
		this.direction = direction;
		return this;
	}

	public String getDay() {
		return day;
	}
	
	public String getUserId() {
		return this.userId;
	}

}
