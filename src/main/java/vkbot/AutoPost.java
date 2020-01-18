package vkbot;

import javax.persistence.*;

import vkbot.service.AutoPostService;

@Entity
@Table(name = "auto_post")
public class AutoPost implements Deletable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int time, direction, frequency;
	private String days,  number, note;
	@Column(name = "user_id")
	private String userId;

	@Override
	public int deleteFromTable() {
		return new AutoPostService().remove(this);
	}

	public int getId() {
		return id;
	}

	public AutoPost setId(int id) {
		this.id = id;
		return this;
	}

	public int getTime() {
		return time;
	}
	
	public String getUserId() {
		return this.userId;
	}

	public AutoPost setTime(int time) {
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

	public int getFrequency() {
		return frequency;
	}

	public AutoPost setFrequency(int frequency) {
		this.frequency = frequency;
		return this;
	}

	public int getDirection() {
		return direction;
	}

	public AutoPost setDirection(int direction) {
		this.direction = direction;
		return this;
	}
	
	public AutoPost setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public AutoPost setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getNote() {
		return note;
	}

	public AutoPost setNote(String note) {
		this.note = note;
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		sb.append("Направление: ");
		sb.append(Flight.getDirectionString(direction));
		sb.append("\nВремя: ");
		Integer minutes = time % 60;
		sb.append(time / 60 + ":" +(minutes < 10 ? "0" + minutes : minutes));
		sb.append("\nЗаметка:");
		sb.append(note);
		sb.append("\nНомер:");
		sb.append(number);
		sb.append("\nРежим: ");
		sb.append(frequency == 0 ? "на текущую неделю" : "еженедельно");
		sb.append("\nДни: ");
		sb.append(days);
		return sb.toString();
	}

}
