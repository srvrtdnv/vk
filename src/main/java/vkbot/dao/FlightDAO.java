package vkbot.dao;

import java.util.List;

import vkbot.Flight;
import vkbot.state.FlightInfoState;

public interface FlightDAO {
	int save(Flight flight);
	int remove(Flight flight);
	int removeAllByDay(Flight flight);
	/*
	 * DDT - day, direction, time
	 */
	List<FlightInfoState> getAllByDDT(Flight flight);
	boolean isFlightExist(Flight flight);
	List<Flight> getAllByUserId(String userId);
}
