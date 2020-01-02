package vkbot;

import vkbot.sql.DeleteSQLRequest;

public class AutoPost implements Deletable {
	
	private String id, time, days, frequency, direction, userId;

	@Override
	public int deleteFromTable() {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		DeleteSQLRequest request = new DeleteSQLRequest("vk_bot", "auto_post", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("id = " + this.getId());
		int result = request.execute();
		return result;
	}

	public String getId() {
		return id;
	}

	public AutoPost setId(String id) {
		this.id = id;
		return this;
	}

	public String getTime() {
		return time;
	}
	
	public String getUserId() {
		return this.userId;
	}

	public AutoPost setTime(String time) {
		this.time = time;
		return this;
	}

	public String getDays() {
		return days;
	}

	public AutoPost setDays(String days) {
		this.days = days;
		return this;
	}

	public String getFrequency() {
		return frequency;
	}

	public AutoPost setFrequency(String frequency) {
		this.frequency = frequency;
		return this;
	}

	public String getDirection() {
		return direction;
	}

	public AutoPost setDirection(String direction) {
		this.direction = direction;
		return this;
	}
	
	public AutoPost setUserId(String userId) {
		this.userId = userId;
		return this;
	}

}
