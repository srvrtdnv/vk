package vkbot.state;

import vkbot.handlers.*;

public class FleightInfoState extends State {
	private String userId, number, note, time, id, direction, day;	

	
	public FleightInfoState() {
		super("saved state",  "", new BackCommandHandler().setNext(new MainMenuCommandHandler()).setNext(new UnknownCommandHandler()) ,true);
	}
	
	public FleightInfoState(String userId, String time, String number, String note) {
		super("saved state",  "", new BackCommandHandler().setNext(new MainMenuCommandHandler()).setNext(new UnknownCommandHandler()) ,true);
		this.note = note;
		this.number = number;
		this.time = time;
		this.userId = userId;
		// TODO Auto-generated constructor stub
	}


	public String getUserId() {
		return userId;
	}

	public FleightInfoState setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public FleightInfoState setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getTime() {
		return time;
	}

	public FleightInfoState setTime(String time) {
		this.time = time;
		super.setName(time);
		return this;
	}


	public String getId() {
		return id;
	}


	public FleightInfoState setId(String id) {
		this.id = id;
		return this;
	}


	public String getDirection() {
		return direction;
	}


	public FleightInfoState setDirection(String direction) {
		this.direction = direction;
		return this;
	}


	public String getDay() {
		return day;
	}


	public FleightInfoState setDay(String day) {
		this.day = day;
		return this;
	}

}
