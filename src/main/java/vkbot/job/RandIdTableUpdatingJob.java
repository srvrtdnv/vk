package vkbot.job;

import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import vkbot.ProcessingCenter;
import vkbot.sql.InsertOnDuplicateKeySQLRequest;

public class RandIdTableUpdatingJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			ProcessingCenter pCenter = ProcessingCenter.getInstance();
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			HashMap<String, Integer> randIds = (HashMap) dataMap.get("randIdMap");
			if (randIds != null && randIds.size() > 0) {
				InsertOnDuplicateKeySQLRequest request = new InsertOnDuplicateKeySQLRequest("vk_bot", "rand_id", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName());
				String primColumnName = "user_id", valueColumnName = "random_id";
				for (Map.Entry<String, Integer> pair : randIds.entrySet()) {
					request.setPrimData(primColumnName, pair.getKey()).putValue(valueColumnName, "" + pair.getValue()).execute();
				}
			}
		} catch (Exception e) {
			ProcessingCenter.logError(e);
		}
	}

}
