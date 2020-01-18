package vkbot.service;

import java.util.List;

import vkbot.AutoNotification;
import vkbot.Flight;
import vkbot.dao.AutoNotificationDAO;
import vkbot.dao.JDBCAutoNotificationDAO;

public class AutoNotificationService {
	
	public AutoNotificationDAO getDAO() {
		return new JDBCAutoNotificationDAO();
	}
	
	public List<AutoNotification> getAllByUserId(String userId) {
		return getDAO().getAllByUserId(userId);
	}
	
	public List<String> getAllUserIdsByDDT(AutoNotification autoN) {
		return getDAO().getAllUserIdsByDDT(autoN);
	}
	
	public int save(AutoNotification autoN) {
		int count = 0;
		int day = autoN.getDay();
		int timeFrom = autoN.getTimeFrom();
		int timeTo = autoN.getTimeTo();
		
		if (timeFrom >= 0 && timeTo < 1440) {
			if (!isExist(autoN)) {
				getDAO().save(autoN);
				return 3;
			} else {
				return -1;
			}
		}
		
		/*
		 * обработка граничных условий
		 * если настраивается автопубликация на промежуток
		 * одна часть которого приходится на один день
		 * а другая часть на другой день
		 */
		
		if (timeFrom < 0) {
			if (day > 1) {
				autoN.setDay(day - 1).setTimeFrom(1440 + timeFrom).setTimeTo(1439);
				if (!isExist(autoN)) {
					getDAO().save(autoN);
					count++;
				}
			}
			autoN.setDay(day).setTimeFrom(0).setTimeTo(timeTo);
			if (!isExist(autoN)) {
				getDAO().save(autoN);
				count++;
			}
		}
		
		if (timeTo > 1439) {
			if (day < Flight.getDaysCount()) {
				autoN.setDay(day + 1).setTimeFrom(0).setTimeTo(timeTo % 1440);
				if (!isExist(autoN)) {
					getDAO().save(autoN);
					count++;
				}
			}
			autoN.setDay(day).setTimeFrom(timeFrom).setTimeTo(1439);
			if (!isExist(autoN)) {
				getDAO().save(autoN);
				count++;
			}
		}
		return count;
	}
	

	public boolean isExist(AutoNotification autoN) {
		return getDAO().isAutoNExist(autoN);
	}
	
	public boolean isAutoNExist(AutoNotification autoN) {
		return false;
	}
	
	public int remove(AutoNotification autoN) {
		return getDAO().remove(autoN);
	}
}
