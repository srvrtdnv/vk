package vkbot.dao;

import java.util.List;

import vkbot.AutoPost;
import vkbot.ProcessingCenter;


public abstract class AutoPostDAO {
	private ProcessingCenter pCenter = ProcessingCenter.getInstance();
	
	abstract List<AutoPost> getAll();
	/*
	 * DDT - day, direction, time
	 */
	abstract List<String> getAllUserIdsByDDT(AutoPost autoP);
	abstract boolean isAutoPostExist(AutoPost autoP);
	abstract List<AutoPost> getAllByUserId(String userId);
	abstract int save(AutoPost autoP);
	abstract int remove(AutoPost autoP);
	
	public ProcessingCenter getPCenter() {
		return pCenter;
	}
	
	public void setPCenter(ProcessingCenter pCenter) {
		this.pCenter = pCenter;
	}
}
