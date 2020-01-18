package vkbot;

import javax.persistence.*;

import vkbot.service.AutoNotificationService;

@Entity
@Table(name = "auto_notifications")
public class AutoNotification implements Deletable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "user_id")
	private String userId;
	@Column(name = "time_from")
	private int timeFrom;
	@Column(name = "time_to")
	private int timeTo;
	private int direction, day;
	@Transient
	private int time;
	
	@Override
	public int deleteFromTable() {
		return new AutoNotificationService().remove(this);
	}
	
	public int post() {
		return new AutoNotificationService().save(this);
	}

	public AutoNotification setId(int id) {
		this.id = id;
		return this;
	}

	public AutoNotification setTimeFrom(int timeFrom) {
		this.timeFrom = timeFrom;
		return this;
	}

	public AutoNotification setTimeTo(int timeTo) {
		this.timeTo = timeTo;
		return this;
	}
	
	public AutoNotification setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public int getDirection() {
		return direction;
	}

	public AutoNotification setDay(int day) {
		this.day = day;
		return this;
	}

	public int getId() {
		return id;
	}

	public int getTimeFrom() {
		return timeFrom;
	}

	public int getTimeTo() {
		return timeTo;
	}

	public AutoNotification setDirection(int direction) {
		this.direction = direction;
		return this;
	}

	public int getDay() {
		return day;
	}
	
	public String getUserId() {
		return this.userId;
	}

	public int getTime() {
		return time;
	}

	public AutoNotification setTime(int time) {
		this.time = time;
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		Integer tFromMinutes = timeFrom % 60;
		Integer tToMinutes = timeTo % 60;
		sb.append("Направление: ");
		sb.append(Flight.getDirectionString(direction));
		sb.append("\nДень: ");
		sb.append(Flight.getDayString(day));
		sb.append("\nВремя: ");
		sb.append("с " + timeFrom / 60 + ":" + (tFromMinutes < 10 ? "0" + tFromMinutes : "" + tFromMinutes) + " до " + timeTo / 60 + ":" + (tToMinutes < 10 ? "0" + tToMinutes : "" + tToMinutes));
		return sb.toString();
	}

}
