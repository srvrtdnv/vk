package vkbot.dao;

import java.util.ArrayList;
import java.util.List;

import vkbot.AutoPost;
import vkbot.ProcessingCenter;
import vkbot.sql.DeleteSQLRequest;
import vkbot.sql.InsertSQLRequest;
import vkbot.sql.RowArray;
import vkbot.sql.SelectSQLRequest;

public class JDBCAutoPostDAO implements AutoPostDAO {

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
		List<AutoPost> autoPs = new ArrayList<AutoPost>();
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		SelectSQLRequest request = new SelectSQLRequest("vk_bot", "auto_post", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("*").setWhereFields("user_id = " + userId);
		RowArray rs = request.execute();
		while (rs.next()) {
			int id = rs.getInt("id");
			int direction = rs.getInt("direction");
			int time = rs.getInt("time");
			int freq = rs.getInt("frequency");
			String days = rs.getString("days");
			String note = rs.getString("note");
			String number = rs.getString("number");
			autoPs.add(new AutoPost().setId(id).setDays(days).setDirection(direction).setFrequency(freq).setNote(note).setNumber(number).setTime(time));
		}
		return autoPs;
	}

	@Override
	public int save(AutoPost autoP) {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		InsertSQLRequest request = new InsertSQLRequest("vk_bot", "flights", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName());
		request.setTableName("auto_post");
		request.putValue("time", "" + autoP.getTime());
		request.putValue("user_id", "\"" + autoP.getUserId() + "\"");
		request.putValue("frequency", autoP.getFrequency() + "");
		request.putValue("days", "\"" + autoP.getDays() + "\"");
		request.putValue("direction", autoP.getDirection() + "");
		request.putValue("number", "\"" + autoP.getNumber() + "\"");
		request.putValue("note", "\"" + autoP.getNote() + "\"");
		return request.execute();
	}

	@Override
	public int remove(AutoPost autoP) {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		DeleteSQLRequest request = new DeleteSQLRequest("vk_bot", "auto_post", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("id = " + autoP.getId());
		return request.execute();
	}

	@Override
	public boolean isAutoPostExist(AutoPost autoP) {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		SelectSQLRequest request = new SelectSQLRequest("vk_bot", "auto_post", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("user_id = " + autoP.getUserId() + " AND direction = " + autoP.getDirection()).addSelectingField("*");
		RowArray result = request.execute();
		return result.next();
	}

}
