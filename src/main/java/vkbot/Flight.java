package vkbot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vkbot.handler.BackCommandHandler;
import vkbot.handler.MainMenuCommandHandler;
import vkbot.handler.UnknownCommandHandler;
import vkbot.service.AutoNotificationService;
import vkbot.service.AutoPostService;
import vkbot.service.FlightService;
import vkbot.sql.InsertOnDuplicateKeySQLRequest;
import vkbot.state.FlightInfoState;
import vkbot.state.State;

import javax.persistence.*;

@Entity
@Table(name = "flights")
public class Flight implements Deletable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int direction, day, time;
	private String number, note; 
	@Column(name = "user_id")
	private String userId;
	@Transient
	private static Map<Integer, String> directions= new HashMap<Integer, String>();
	@Transient
	private static int directionsCount, daysCount = 2;
	@Transient
	private String autoPostDays;
	@Transient
	private boolean isAutoPostOn = false;
	@Transient
	private int accuracyMinus, accuracyPlus, frequency;
	
	static {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("directions.txt"), "CP1251"));
			String line = reader.readLine();
			Integer count = 1;
			while (line != null) {
				directions.put(count++, line);
				line = reader.readLine();
			}
			reader.close();
			Flight.directionsCount = count;
		} catch (Exception e) {
		}
	}
	
	@Override
	public int deleteFromTable() {
		return new FlightService().remove(this);
	}
	
	public int post(SimpleMessenger messenger) {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		FlightService fService = new FlightService();
		int result = 1;
		if (fService.save(this) < 1) return -1;
		List<String> userIds = new AutoNotificationService().getAllUserIdsByDDT(new AutoNotification().setDay(this.getDay()).setDirection(this.getDirection()).setTime(this.getTime()));
		if (userIds.size() > 0) pCenter.setStateWithUserIds(messenger, userIds, new State("saved state", false).setMessage("&#128232;Автоуведомление&#128232;\n" + this.getFullInfo().replaceAll("Автопубликация.*", "")).setHandler(new BackCommandHandler().setNext(new MainMenuCommandHandler().setNext(new UnknownCommandHandler()))));
		if (isAutoPostOn) {
			String nextDay = "";
			switch (new Date().getDay()) {
			case 6:
				nextDay = "вс";
				break;
			case 0:
				nextDay = "пн";
				break;
			case 1:
				nextDay = "вт";
				break;
			case 2:
				nextDay = "ср";
				break;
			case 3:
				nextDay = "чт";
				break;
			case 4:
				nextDay = "пт";
				break;
			case 5:
				nextDay = "сб";
				break;
			}
			if (autoPostDays.contains(nextDay) && day != 2) {
				Flight flight = new Flight().setDay(2).setTime(time).setDirection(direction).setNote(note).setNumber(number).setUserId(userId).setAutoPostOn(false);
				if (!fService.isFlightExist(flight)) result = flight.post(messenger); 
				if (result < 1) return -1;
			}
			AutoPost autoP = new AutoPost().setDays(autoPostDays).setDirection(direction).setFrequency(frequency).setTime(time).setUserId(userId).setNote(note).setNumber(number);
			if (new AutoPostService().save(autoP) < 1) return -1;
		}
		InsertOnDuplicateKeySQLRequest insRequest = new InsertOnDuplicateKeySQLRequest("vk_bot", "user_ids", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setPrimData("user_id", "\"" + userId + "\"").putValue("saved_number", "\"" + number + "\"").putValue("saved_note",  "\"" + note + "\"");
		insRequest.execute();
		return result;
	}
	
	
	public List<FlightInfoState> find() {
		return new FlightService().getAllByDDT(this);
	}
	
	public String getFullInfo() {
		int time = this.getTime();
		String minutes = time % 60 < 10 ? "0" + (time % 60) : "" + (time % 60);
		
		StringBuilder result = new StringBuilder("Направление: ");
		result.append(Flight.getDirectionString(this.getDirection()));
		result.append("\nКогда: ");
		result.append(Flight.getDayString(this.getDay()));
		result.append("\nВремя: ");
		result.append((time / 60) + ":" + minutes);
		result.append("\nНомер: ");
		result.append(this.getNumber());
		result.append("\nЗаметка: ");
		result.append(this.getNote());
		
		if (!this.isAutoPostOn()) {
			result.append("\nАвтопубликация: нет");
		} else {
			result.append("\nАвтопубликация: ");
			result.append(this.getFrequency() == 0 ? "на текущую неделю" : "еженедельно");
			result.append("\nВыбранные дни: ");
			result.append(this.getAutoPostDays());
		}
		
		return result.toString();
	}

	public Flight setDirection(int direction) {
		this.direction = direction;
		return this;
	}

	public Flight setDay(int day) {
		this.day = day;
		return this;
	}

	public Flight setTime(int time) {
		this.time = time;
		return this;
	}
	
	public Flight setDirection(String direction) {
		for (Map.Entry<Integer, String> pair : this.directions.entrySet()) {
			if (pair.getValue().toLowerCase().equals(direction)) {
				this.direction = pair.getKey();
			}
		}
		return this;
	}

	public Flight setDay(String day) {
		switch (day) {
		case "сегодня":
			this.day = 1;
			break;
		case "завтра":
			this.day = 2;
			break;
		}
		return this;
	}

	public Flight setNumber(String number) {
		this.number = number;
		return this;
	}

	public Flight setAutoPostDays(String autoPostDays) {
		this.autoPostDays = autoPostDays;
		return this;
	}

	public Flight setNote(String note) {
		this.note = note;
		return this;
	}

	public Flight setId(int id) {
		this.id = id;
		return this;
	}

	public Flight setFrequency(int frequency) {
		this.frequency = frequency;
		return this;
	}

	public Flight setAutoPostOn(boolean isAutoPostOn) {
		this.isAutoPostOn = isAutoPostOn;
		return this;
	}

	public Flight setUserId(String userId) {
		this.userId = userId;
		return this;
	}
	
	public Flight setAccuracyMinus(int accuracyMinus) {
		this.accuracyMinus = accuracyMinus;
		return this;
	}
	
	public Flight setAccuracyPlus(int accuracyPlus) {
		this.accuracyPlus = accuracyPlus;
		return this;
	}
	
	public int getAccuracyMinus() {
		return this.accuracyMinus;
	}
	
	public int getAccuracyPlus() {
		return this.accuracyPlus;
	}
	
	public int getDirection() {
		return direction;
	}

	public int getDay() {
		return day;
	}

	public int getTime() {
		return time;
	}

	public String getNumber() {
		return number;
	}

	public String getNote() {
		return note;
	}
	

	public String getAutoPostDays() {
		return autoPostDays;
	}

	public boolean isAutoPostOn() {
		return isAutoPostOn;
	}

	public int getFrequency() {
		return frequency;
	}

	public String getUserId() {
		return userId;
	}

	public int getId() {
		return id;
	}
	
	public int getTimeFrom() {
		return this.time - this.accuracyMinus;
	}
	
	public int getTimeTo() {
		return this.time + this.accuracyPlus;
	}
	
	public static int getDirectionsCount() {
		return Flight.directionsCount;
	}
	
	public static int getDaysCount() {
		return Flight.daysCount;
	}
	
	public static String getDirectionNames() {
		StringBuilder result = new StringBuilder("");
		for (Map.Entry<Integer, String> pair : Flight.directions.entrySet()) {
			result.append("\n");
			result.append(pair.getKey());
			result.append(" - ");
			result.append(pair.getValue());
		}
		return result.toString();
	}
	

	
	public static String getDayNames() {
		Date date = new Date();
		int today = date.getDate();
		int curMonth = date.getMonth() + 1;
		date.setTime(date.getTime() + 24 * 3600 * 1000);
		int tomorrow = date.getDate();
		int month = date.getMonth() + 1;
		return "\n1 - Сегодня (" +  today + "." + curMonth  + ")\n2 - Завтра (" + tomorrow + "." + month + ")";
	}
	
	/*
	 * для большей гибкости 
	 * нужно создать таблицу/мапу 
	 * с соответствиями строки и числа
	 * а не создавать свитч
	 */
	public static String getDirectionString(int direction) {
		switch (direction) {
		case 1:
			return "в Уфу";
		case 2:
			return "из Уфы";
		}
		return "неизвестное направление";
	}
	
	public static String getDayString(int day) {
		Date date = new Date();
		switch (day) {
		case 1:
			return "сегодня (" +  date.getDate() + "." + (date.getMonth() + 1 ) + ")";
		case 2:
			date.setTime(date.getTime() + 24 * 3600 * 1000);;
			return "завтра (" + date.getDate() + "." + (date.getMonth() + 1 ) + ")";
		}
		return "неизвестный день";
	}
	
	
	public String toStringWOAutoPost() {
		StringBuilder sb = new StringBuilder("");
		sb.append("Направление: " + Flight.getDirectionString(direction));
		sb.append("\nВремя: ");
		Integer minutes = time % 60;
		sb.append(time / 60 + ":" + (minutes < 10 ? "0" + minutes : minutes));
		sb.append("\nДень: " + Flight.getDayString(day));
		sb.append("\nНомер: ");
		sb.append(number);
		sb.append("\nЗаметка: ");
		sb.append(note);
		return sb.toString();
	}
	
}
