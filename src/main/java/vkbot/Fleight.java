package vkbot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vkbot.handlers.BackCommandHandler;
import vkbot.handlers.MainMenuCommandHandler;
import vkbot.handlers.UnknownCommandHandler;
import vkbot.sql.DeleteSQLRequest;
import vkbot.sql.InsertOnDuplicateKeySQLRequest;
import vkbot.sql.InsertSQLRequest;
import vkbot.sql.RowArray;
import vkbot.sql.SelectSQLRequest;
import vkbot.state.FleightInfoState;
import vkbot.state.State;

public class Fleight implements Deletable {
	private static Map<Integer, String> directions= new HashMap<Integer, String>();
	private static int directionsCount, daysCount = 2;
	private String number, note, autoPostDays, userId;
	private boolean isAutoPostOn = false;
	private boolean frequency;
	private int direction, day, id, time, accuracyMinus, accuracyPlus;
	
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
			Fleight.directionsCount = count;
		} catch (Exception e) {
		}
	}
	
	@Override
	public int deleteFromTable() {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		DeleteSQLRequest request = new DeleteSQLRequest("vk_bot", "flights", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("id = " + this.getId());
		int result = request.execute();
		return result;
	}
	
	public int post(SimpleMessenger messenger) {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		InsertSQLRequest request = new InsertSQLRequest("vk_bot", "flights", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName());
		request.putValue("time", "" + getTime());
		request.putValue("user_id", "\"" + userId + "\"");
		request.putValue("number", "\"" + number + "\"");
		request.putValue("note", "\"" + note + "\"");
		request.putValue("direction", "" + direction);
		request.putValue("day", "" + day);
		int result = request.execute();
		if (result < 1) return -1;
		SelectSQLRequest selectRequest = new SelectSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("user_id").setWhereFields("time_from < " + time + " AND time_to > " + time + " AND direction = " + direction + " AND day = " + day);
		RowArray res = selectRequest.execute();
		List<String> userIds = new ArrayList<String>();
		while (res.next()) {
			userIds.add(res.getString("user_id"));
		}
		if (userIds.size() > 0) pCenter.setStateWithUserIds(messenger, userIds, new State("saved state", false).setMessage("Автоуведомление.\n" + this.getFullInfo().replaceAll("Автопубликация.*", "")).setHandler(new BackCommandHandler().setNext(new MainMenuCommandHandler().setNext(new UnknownCommandHandler()))));
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
				Fleight flight = new Fleight().setDay(2).setTime(time).setDirection(direction).setNote(note).setNumber(number).setUserId(userId).setAutoPostOn(false);
				if (!flight.isExist()) result = flight.post(messenger); 
				if (result < 1) return -1;
			}
			request.clearValues();
			request.setTableName("auto_post");
			request.putValue("time", "" + time);
			request.putValue("user_id", "\"" + userId + "\"");
			request.putValue("frequency", frequency ? "1" : "0");
			request.putValue("days", "\"" + autoPostDays + "\"");
			request.putValue("direction", direction + "");
			request.putValue("number", "\"" + number + "\"");
			request.putValue("note", "\"" + note + "\"");
			result = request.execute();
			if (result < 1) return -1;
		}
		InsertOnDuplicateKeySQLRequest insRequest = new InsertOnDuplicateKeySQLRequest("vk_bot", "user_ids", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setPrimData("user_id", "\"" + userId + "\"").putValue("saved_number", "\"" + number + "\"").putValue("saved_note",  "\"" + note + "\"");
		insRequest.execute();
		return result;
	}
	
	public int post() {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		InsertSQLRequest request = new InsertSQLRequest("vk_bot", "flights", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName());
		request.putValue("time", "" + getTime());
		request.putValue("user_id", "\"" + userId + "\"");
		request.putValue("number", "\"" + number + "\"");
		request.putValue("note", "\"" + note + "\"");
		request.putValue("direction", "" + direction);
		request.putValue("day", "" + day);
		int result = request.execute();
		return result;
	}
	
	public List<FleightInfoState> find() {
		List<FleightInfoState> result = new ArrayList<FleightInfoState>();
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		SelectSQLRequest request = new SelectSQLRequest("vk_bot", "flights", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("direction = " + this.direction + " AND day = " + this.day + " AND time > " + (this.time - this.accuracyMinus) + " AND time < " + (this.time + this.accuracyPlus)).addSelectingField("*");
		RowArray response = request.execute();
		while (response.next()) {
			int intTime = Integer.parseInt(response.getString("time"));
			String minutes = (intTime % 60) < 10 ? "0" + (intTime % 60) : "" + (intTime % 60);
			String time = Integer.toString(intTime / 60) + ":" + minutes;
			String userId = response.getString("user_id");
			String number = response.getString("number");
			String note = response.getString("note");
			String name = "Время: " + time + "\nСтраница ВК: https://vk.com/" + userId + "\nНомер: " + number + "\nЗаметка: " + note;
			result.add((FleightInfoState) new FleightInfoState().setName(name));
		}
		return result;
	}
	
	public boolean isExist() {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		SelectSQLRequest request = new SelectSQLRequest("vk_bot", "flights", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("user_id = \"" + this.userId + "\" AND direction = " + direction + " AND day = " + day).addSelectingField("*");
		RowArray result = request.execute();
		return result.next();
	}
	
	public boolean isAutoPostExist() {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		SelectSQLRequest request = new SelectSQLRequest("vk_bot", "auto_post", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("user_id = " + this.userId + " AND direction = " + direction).addSelectingField("*");
		RowArray result = request.execute();
		return result.next();
	}
	
	public String getFullInfo() {
		int time = this.getTime();
		String minutes = time % 60 < 10 ? "0" + (time % 60) : "" + (time % 60);
		
		StringBuilder result = new StringBuilder("Направление: ");
		result.append(Fleight.getDirectionString(this.getDirection()));
		result.append("\nКогда: ");
		result.append(Fleight.getDayString(this.getDay()));
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
			result.append(this.isFrequency() ? "еженедельно" : "на текущую неделю");
			result.append("\nВыбранные дни: ");
			result.append(this.getAutoPostDays());
		}
		
		return result.toString();
	}

	public Fleight setDirection(int direction) {
		this.direction = direction;
		return this;
	}

	public Fleight setDay(int day) {
		this.day = day;
		return this;
	}

	public Fleight setTime(int time) {
		this.time = time;
		return this;
	}
	
	public Fleight setDirection(String direction) {
		for (Map.Entry<Integer, String> pair : this.directions.entrySet()) {
			if (pair.getValue().toLowerCase().equals(direction)) {
				this.direction = pair.getKey();
			}
		}
		return this;
	}

	public Fleight setDay(String day) {
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

	public Fleight setNumber(String number) {
		this.number = number;
		return this;
	}

	public Fleight setAutoPostDays(String autoPostDays) {
		this.autoPostDays = autoPostDays;
		return this;
	}

	public Fleight setNote(String note) {
		this.note = note;
		return this;
	}

	public Fleight setId(int id) {
		this.id = id;
		return this;
	}

	public Fleight setFrequency(boolean frequency) {
		this.frequency = frequency;
		return this;
	}

	public Fleight setAutoPostOn(boolean isAutoPostOn) {
		this.isAutoPostOn = isAutoPostOn;
		return this;
	}

	public Fleight setUserId(String userId) {
		this.userId = userId;
		return this;
	}
	
	public Fleight setAccuracyMinus(int accuracyMinus) {
		this.accuracyMinus = accuracyMinus;
		return this;
	}
	
	public Fleight setAccuracyPlus(int accuracyPlus) {
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

	public boolean isFrequency() {
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
		return Fleight.directionsCount;
	}
	
	public static int getDaysCount() {
		return Fleight.daysCount;
	}
	
	public static String getDirectionNames() {
		StringBuilder result = new StringBuilder("");
		for (Map.Entry<Integer, String> pair : Fleight.directions.entrySet()) {
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
	
	
}
