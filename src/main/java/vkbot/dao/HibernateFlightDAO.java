package vkbot.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import vkbot.Flight;
import vkbot.state.FlightInfoState;

public class HibernateFlightDAO implements FlightDAO {

	@Override
	public int save(Flight flight) {
		int result = 1;
		Session session = HibernateSessionFactoryHolder.getFactory().openSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.save(flight);
			transaction.commit();
		} catch (Exception e) {
			result = -1;
		}
		session.close();
		return result;
	}

	@Override
	public int remove(Flight flight) {
		return -1;
	}

	@Override
	public int removeAllByDay(Flight flight) {
		return -1;
	}

	@Override
	public List<FlightInfoState> getAllByDDT(Flight flight) {
		return null;
	}

	@Override
	public boolean isFlightExist(Flight flight) {
		return false;
	}

	@Override
	public List<Flight> getAllByUserId(String userId) {
		return null;
	}

}
