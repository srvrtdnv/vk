package vkbot.service;

import java.util.List;

import vkbot.AutoPost;
import vkbot.dao.AutoPostDAO;
import vkbot.dao.JDBCAutoPostDAO;

public class AutoPostService {
	
	public AutoPostDAO getDAO() {
		return new JDBCAutoPostDAO();
	}
	
	public int  save(AutoPost autoP) {
		return getDAO().save(autoP);
	}
	
	public int remove(AutoPost autoP) {
		return getDAO().remove(autoP);
	}
	
	public AutoPost getByUsIdDirection(AutoPost autoP) {
		return null;
	}
	
	public List<AutoPost> getAllByUserId(String userId) {
		return getDAO().getAllByUserId(userId);
	}
	
	public boolean isAutoPostExist(AutoPost autoP) {
		return getDAO().isAutoPostExist(autoP);
	}
}
