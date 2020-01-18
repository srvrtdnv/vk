package vkbot.dao;

import java.util.List;

import vkbot.AutoNotification;
import vkbot.Flight;

public interface AutoNotificationDAO {
	List<AutoNotification> getAll();
	/*
	 * DDT - day, direction, time
	 */
	List<String> getAllUserIdsByDDT(AutoNotification autoN);
	List<AutoNotification> getAllByDD(AutoNotification autoN);
	List<AutoNotification> getAllByUserId(String userId);
	int save(AutoNotification autoN);
	int remove(AutoNotification autoN);
	boolean isAutoNExist(AutoNotification autoN);
}
