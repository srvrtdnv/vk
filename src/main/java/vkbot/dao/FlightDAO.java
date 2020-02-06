package vkbot.dao;

import java.util.List;

import vkbot.Flight;
import vkbot.ProcessingCenter;
import vkbot.state.FlightInfoState;

public abstract class FlightDAO {
	private ProcessingCenter pCenter = ProcessingCenter.getInstance();
	
	abstract int save(Flight flight);
	abstract int remove(Flight flight);
	abstract int removeAllByDay(Flight flight);
	/*
	 * DDT - day, direction, time
	 */
	abstract List<FlightInfoState> getAllByDDT(Flight flight);
	abstract boolean isFlightExist(Flight flight);
	abstract List<Flight> getAllByUserId(String userId);
	
	public ProcessingCenter getPCenter() {
		return pCenter;
	}
	
	public void setPCenter(ProcessingCenter pCenter) {
		this.pCenter = pCenter;
	}
}
