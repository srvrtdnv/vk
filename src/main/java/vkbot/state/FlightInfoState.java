package vkbot.state;

import vkbot.handler.*;

public class FlightInfoState extends State {
	private String userId, number, note, time, id, direction, day;	

	
	public FlightInfoState() {
		super("saved state",  "", new BackCommandHandler().setNext(new MainMenuCommandHandler()).setNext(new UnknownCommandHandler()) ,true);
	}
	
	public FlightInfoState(String userId, String time, String number, String note) {
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

	public FlightInfoState setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public FlightInfoState setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getTime() {
		return time;
	}

	public FlightInfoState setTime(String time) {
		this.time = time;
		super.setName(time);
		return this;
	}


	public String getId() {
		return id;
	}


	public FlightInfoState setId(String id) {
		this.id = id;
		return this;
	}


	public String getDirection() {
		return direction;
	}


	public FlightInfoState setDirection(String direction) {
		this.direction = direction;
		return this;
	}


	public String getDay() {
		return day;
	}


	public FlightInfoState setDay(String day) {
		this.day = day;
		return this;
	}

}
