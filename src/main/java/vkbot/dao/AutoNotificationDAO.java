package vkbot.dao;

import java.util.List;

import vkbot.AutoNotification;
import vkbot.Flight;
import vkbot.ProcessingCenter;

public abstract class AutoNotificationDAO {
	private ProcessingCenter pCenter = ProcessingCenter.getInstance();
	
	abstract List<AutoNotification> getAll();
	/*
	 * DDT - day, direction, time
	 */
	abstract List<String> getAllUserIdsByDDT(AutoNotification autoN);
	abstract List<AutoNotification> getAllByDD(AutoNotification autoN);
	abstract List<AutoNotification> getAllByUserId(String userId);
	abstract int save(AutoNotification autoN);
	abstract int remove(AutoNotification autoN);
	abstract boolean isAutoNExist(AutoNotification autoN);
	
	public ProcessingCenter getPCenter() {
		return pCenter;
	}
	
	public void setPCenter(ProcessingCenter pCenter) {
		this.pCenter = pCenter;
	}
	
}
