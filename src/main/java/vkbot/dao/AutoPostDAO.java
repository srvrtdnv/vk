package vkbot.dao;

import java.util.List;

import vkbot.AutoPost;


public interface AutoPostDAO {
	List<AutoPost> getAll();
	/*
	 * DDT - day, direction, time
	 */
	List<String> getAllUserIdsByDDT(AutoPost autoP);
	boolean isAutoPostExist(AutoPost autoP);
	List<AutoPost> getAllByUserId(String userId);
	int save(AutoPost autoP);
	int remove(AutoPost autoP);
}
