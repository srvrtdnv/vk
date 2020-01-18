package vkbot.dao;

import java.util.List;

import vkbot.AutoNotification;

public class HibernateAutoNotificationDAO implements AutoNotificationDAO {

	@Override
	public List<AutoNotification> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllUserIdsByDDT(AutoNotification autoN) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AutoNotification> getAllByDD(AutoNotification autoN) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AutoNotification> getAllByUserId(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int save(AutoNotification autoN) {
		return -1;
	}

	@Override
	public int remove(AutoNotification autoN) {
		return -1;
	}

	@Override
	public boolean isAutoNExist(AutoNotification autoN) {
		// TODO Auto-generated method stub
		return false;
	}

}
