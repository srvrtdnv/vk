package vkbot.dao;

import java.util.List;

import vkbot.AutoPost;

public class HibernateAutoPostDAO extends AutoPostDAO {

	@Override
	public List<AutoPost> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllUserIdsByDDT(AutoPost autoP) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AutoPost> getAllByUserId(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int save(AutoPost autoP) {
		return -1;
	}

	@Override
	public int remove(AutoPost autoP) {
		return -1;
	}

	@Override
	public boolean isAutoPostExist(AutoPost autoP) {
		// TODO Auto-generated method stub
		return false;
	}

}
