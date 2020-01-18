package vkbot.service;

import java.util.ArrayList;
import java.util.List;

import vkbot.Flight;
import vkbot.dao.FlightDAO;
import vkbot.dao.JDBCFlightDAO;
import vkbot.state.FlightInfoState;

public class FlightService {
	
	public FlightDAO getDAO() {
		return new JDBCFlightDAO();
	}
	
	public int save(Flight flight) {
		return getDAO().save(flight);
	}
	
	public int remove(Flight flight) {
		return getDAO().remove(flight);
	}
	
	public int removeAllByDay(Flight flight){
		return -1;
	}
	
	/*
	 * by DDT - by day, direction and time
	 */
	public List<FlightInfoState> getAllByDDT(Flight flight) {
		List<FlightInfoState> result = new ArrayList<FlightInfoState>();
		if (flight.getTime() < flight.getAccuracyMinus()) {
			int curTimeMinus = flight.getAccuracyMinus();
			int curTimePlus = flight.getAccuracyPlus();
			int curTime = flight.getTime();
			if (flight.getDay() > 1) {
				int curDay = flight.getDay();
				flight.setDay(curDay - 1);
				flight.setAccuracyMinus(flight.getAccuracyMinus() - flight.getTime() - 1);
				flight.setTime(23 * 60 + 59);
				flight.setAccuracyPlus(0);
				result = flight.find();
				flight.setDay(curDay);
			}
			flight.setTime(0);
			flight.setAccuracyMinus(0);
			flight.setAccuracyPlus(curTime + curTimePlus);
			result.addAll(flight.find());
			flight.setTime(curTime);
			flight.setAccuracyMinus(curTimeMinus);
			flight.setAccuracyPlus(curTimePlus);
		}
		if ((23 * 60 + 59 - flight.getTime()) < flight.getAccuracyPlus()) {
			int curTimeMinus = flight.getAccuracyMinus();
			int curTimePlus = flight.getAccuracyPlus();
			int curTime = flight.getTime();
			flight.setTime(curTime - curTimeMinus);
			flight.setAccuracyMinus(0);
			flight.setAccuracyPlus(23 * 60 + 59 - flight.getTime());
			result = flight.find();
			flight.setTime(curTime);
			flight.setAccuracyPlus(curTimePlus);
			if (flight.getDay() < Flight.getDaysCount() ) {
				int curDay = flight.getDay();
				flight.setDay(curDay + 1);
				flight.setAccuracyMinus(0);
				flight.setAccuracyPlus(flight.getAccuracyPlus() - 1 - ((23 * 60 + 59) - flight.getTime()));
				flight.setTime(0);
				result.addAll(flight.find());
				flight.setDay(curDay);
			}
			flight.setTime(curTime);
			flight.setAccuracyMinus(curTimeMinus);
			flight.setAccuracyPlus(curTimePlus);
		}
		if (flight.getTime() >= flight.getAccuracyMinus() && (24 * 60 - flight.getTime()) > flight.getAccuracyPlus() ) {
			return getDAO().getAllByDDT(flight);
		}
		return result;
	}
	
	public boolean isFlightExist(Flight flight) {
		return getDAO().isFlightExist(flight);
	}
	
	public List<Flight> getAllByUserId(String userId) {
		return getDAO().getAllByUserId(userId);
	}
	
}
