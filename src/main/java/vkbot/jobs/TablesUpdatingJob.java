package vkbot.jobs;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import vkbot.Fleight;
import vkbot.ProcessingCenter;
import vkbot.sql.DeleteSQLRequest;
import vkbot.sql.RowArray;
import vkbot.sql.SelectSQLRequest;
import vkbot.sql.UpdateSQLRequest;

public class TablesUpdatingJob implements Job {
	

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		pCenter.setIsMaintenance(true);
		while(pCenter.isHereIncompletedProcess()) {

		}
		DeleteSQLRequest deleteRequest = new DeleteSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("day = 1");
		deleteRequest.execute();
		deleteRequest.setTableName("flights");
		deleteRequest.execute();
		UpdateSQLRequest updRequest = new UpdateSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).putValue("day", "day - 1");
		updRequest.execute();
		updRequest.setTableName("flights");
		updRequest.execute();
		SelectSQLRequest selectRequest = new SelectSQLRequest("vk_bot", "auto_post", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("*");
		RowArray response = selectRequest.execute();
		if (new Date().getDay() == 0) {
			deleteRequest.setWhereFields("frequency = 1").setTableName("auto_post");
		}
		String nextDay = "";
		Date date = new Date();
		date.setTime(date.getTime() + 24 * 3600 * 1000);
		switch (date.getDay()) {
		case 0:
			nextDay = "вс";
			break;
		case 1:
			nextDay = "пн";
			break;
		case 2:
			nextDay = "вт";
			break;
		case 3:
			nextDay = "ср";
			break;
		case 4:
			nextDay = "чт";
			break;
		case 5:
			nextDay = "пт";
			break;
		case 6:
			nextDay = "сб";
			break;
		}
		while (response.next()) {
			String days = response.getString("days").toLowerCase();
			if (days.contains(nextDay)) {
				Fleight flight = new Fleight().setDirection(response.getInt("direction")).setDay(2).setTime(response.getInt("time")).setUserId(response.getString("user_id")).setNumber(response.getString("number")).setNote(response.getString("note"));
				flight.post();
			}
		}
		pCenter.clearAllMaps();
		pCenter.setIsMaintenance(false);
		System.out.println("Tables updating complete");
	}

}
