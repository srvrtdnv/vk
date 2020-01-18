package vkbot.dao;

import java.util.ArrayList;
import java.util.List;

import vkbot.Flight;
import vkbot.ProcessingCenter;
import vkbot.sql.DeleteSQLRequest;
import vkbot.sql.InsertSQLRequest;
import vkbot.sql.RowArray;
import vkbot.sql.SelectSQLRequest;
import vkbot.state.FlightInfoState;

public class JDBCFlightDAO implements FlightDAO {

	@Override
	public int save(Flight flight) {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		InsertSQLRequest request = new InsertSQLRequest("vk_bot", "flights", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName());
		request.putValue("time", "" + flight.getTime());
		request.putValue("user_id", "\"" + flight.getUserId() + "\"");
		request.putValue("number", "\"" + flight.getNumber() + "\"");
		request.putValue("note", "\"" + flight.getNote() + "\"");
		request.putValue("direction", "" + flight.getDirection());
		request.putValue("day", "" + flight.getDay());
		return request.execute();
	}

	@Override
	public int remove(Flight flight) {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		DeleteSQLRequest request = new DeleteSQLRequest("vk_bot", "flights", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("id = " + flight.getId());
		return request.execute();
	}

	@Override
	public int removeAllByDay(Flight flight) {
		return -1;
	}

	@Override
	public List<FlightInfoState> getAllByDDT(Flight flight) {
		List<FlightInfoState> result = new ArrayList<FlightInfoState>();
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		SelectSQLRequest request = new SelectSQLRequest("vk_bot", "flights", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("direction = " + flight.getDirection() + " AND day = " + flight.getDay() + " AND time > " + (flight.getTime() - flight.getAccuracyMinus()) + " AND time < " + (flight.getTime() + flight.getAccuracyPlus())).addSelectingField("*");
		RowArray response = request.execute();
		while (response.next()) {
			int intTime = Integer.parseInt(response.getString("time"));
			String minutes = (intTime % 60) < 10 ? "0" + (intTime % 60) : "" + (intTime % 60);
			String time = Integer.toString(intTime / 60) + ":" + minutes;
			String userId = response.getString("user_id");
			String number = response.getString("number");
			String note = response.getString("note");
			String name = "Время: " + time + "\nСтраница ВК: https://vk.com/id" + userId + "\nНомер: " + number + "\nЗаметка: " + note;
			result.add((FlightInfoState) new FlightInfoState().setName(name));
		}
		return result;
	}

	@Override
	public boolean isFlightExist(Flight flight) {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		SelectSQLRequest request = new SelectSQLRequest("vk_bot", "flights", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("user_id = \"" + flight.getUserId() + "\" AND direction = " + flight.getDirection() + " AND day = " + flight.getDay()).addSelectingField("*");
		RowArray result = request.execute();
		return result.next();
	}

	@Override
	public List<Flight> getAllByUserId(String userId) {
		List<Flight> flights = new ArrayList<Flight>();
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		SelectSQLRequest request = new SelectSQLRequest("vk_bot", "flights", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("*").setWhereFields("user_id = " + userId);
		RowArray rs = request.execute();
		while (rs.next()) {
			int id = rs.getInt("id");
			int direction = rs.getInt("direction");
			int day = rs.getInt("day");
			int time = rs.getInt("time");
			String number = rs.getString("number");
			String note = rs.getString("note");
			flights.add(new Flight().setId(id).setDirection(direction).setDay(day).setTime(time).setNote(note).setNumber(number));
		}
		return flights;
	}

}
