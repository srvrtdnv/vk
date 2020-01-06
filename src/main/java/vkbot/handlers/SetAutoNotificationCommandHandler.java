package vkbot.handlers;

import vkbot.AutoNotification;
import vkbot.Flight;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.sql.SelectSQLRequest;
import vkbot.state.ErrorState;
import vkbot.state.State;

/*
 * обработчик установки автоуведомлений
 */

public class SetAutoNotificationCommandHandler extends MessageHandler {

	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		if (message.getText().equals("000")) {
			ProcessingCenter pCenter = ProcessingCenter.getInstance();
			String userId = message.getUserId();
			String[] arr = state.getName().split(":");
			int direction = Integer.parseInt(arr[0]);
			int day = Integer.parseInt(arr[1]);
			int timeFrom = Integer.parseInt(arr[2]);
			int timeTo = Integer.parseInt(arr[3]);
			if (timeFrom >= 0 && timeTo < 1440) {
				AutoNotification autoN = new AutoNotification().setDay("" + day).setDirection("" + direction).setTimeFrom("" + timeFrom).setTimeTo("" + timeTo).setUserId(userId);
				if (!autoN.isExist()) {
					int result = autoN.post();
					if (result > 0) {
						State nextState = new State("saved state", false).setIsBackButtonOn(false).setIsKeyboardOn(false).setMessage("Автоуведомление включено.").setHandler(new MainMenuCommandHandler().setNext(new UnknownCommandHandler()));
						nextState.get(0).setFullId("saved state");
						pCenter.setState(messenger, userId, nextState);
						return 1;
					} else {
						State nextState = new State("saved state", false).setIsBackButtonOn(false).setIsKeyboardOn(false).setMessage("Что-то пошло не так.").setHandler(new MainMenuCommandHandler().setNext(new UnknownCommandHandler()));
						nextState.get(0).setFullId("saved state");
						pCenter.setState(messenger, userId, nextState);
						return -1;
					}
				}
				else {
					State err = new ErrorState().setMessage("На этот день в этом направлении функция автоуведомления у тебя уже активирована.").setIsBackButtonOn(false);
					err.get(0).setFullId("saved state");
					pCenter.setState(messenger, userId, err);
					return -1;
				}
			}
			/*
			 * еще немножко говнокода
			 * обработка граничных условий
			 * если настраивается автопубликация на промежуток
			 * одна часть которого приходится на один день
			 * а другая часть на другой день
			 */
			
			/*
			 * 
			 * 
			 */
			if (timeFrom < 0) {
				int count = 0;
				if (day > 1) {
					AutoNotification autoN = new AutoNotification().setDay("" + (day - 1)).setDirection("" + direction).setTimeFrom("" + (1440 + timeFrom)).setTimeTo("" + 1439).setUserId(userId);
					SelectSQLRequest request = new SelectSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("*").setWhereFields(" user_id = \"" + userId + "\" AND direction = " + direction + " AND day = " + (day - 1) + " AND time_to > " + (1440 + timeFrom));
					if (!request.execute().next()) {
						autoN.post();
						count++;
					}
				}
				AutoNotification autoN = new AutoNotification().setDay("" + day).setDirection("" + direction).setTimeFrom("" + 0).setTimeTo("" + timeTo).setUserId(userId);
				SelectSQLRequest request = new SelectSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("*").setWhereFields(" user_id = \"" + userId + "\" AND direction = " + direction + " AND day = " + day + " AND time_from < " + timeTo);
				if (!request.execute().next()) {
					autoN.post();
					count++;
				}
				if (count == 2) {
					State nextState = new State("saved state", false).setIsBackButtonOn(false).setIsKeyboardOn(false).setMessage("Автоуведомление включено.\nP.S. опция активирована дважды, т.к. искомый промежуток времени приходится на 2 разных дня (одна часть промежутка - на один день, а другая - на следующий день)\nПодробнее можно узнать, перейдя из главного меню в раздел включенных опций.").setHandler(new MainMenuCommandHandler().setNext(new UnknownCommandHandler()));
					nextState.get(0).setFullId("saved state");
					pCenter.setState(messenger, userId, nextState);
					return 1;
				} else if (count == 1) {
					State nextState = new State("saved state", false).setIsBackButtonOn(false).setIsKeyboardOn(false).setMessage("Автоуведомление включено частично.\nP.S. искомый промежуток времени приходится на 2 разных дня (одна часть промежутка - на один день, а другая - на следующий день). В результате добавления было обнаружено, что либо у тебя уже есть автоуведомление на какой-то день, либо этот день в системе уже/еще не определен.\nПодробнее можно узнать, перейдя из главного меню в раздел включенных опций.").setHandler(new MainMenuCommandHandler().setNext(new UnknownCommandHandler()));
					nextState.get(0).setFullId("saved state");
					pCenter.setState(messenger, userId, nextState);
					return 1;
				} else {
					State err = new ErrorState().setMessage("На этот день в этом направлении функция автоуведомления у тебя уже активирована.").setIsBackButtonOn(false);
					err.get(0).setFullId("saved state");
					pCenter.setState(messenger, userId, err);
					return -1;
				}
			}
			
			/*
			 * 
			 * 
			 * 
			 */
			if (timeTo > 1439) {
				int count = 0;
				if (day < Flight.getDaysCount()) {
					AutoNotification autoN = new AutoNotification().setDay("" + (day + 1)).setDirection("" + direction).setTimeFrom("" + 0).setTimeTo("" + (timeTo % 1440)).setUserId(userId);
					SelectSQLRequest request = new SelectSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("*").setWhereFields(" user_id = \"" + userId + "\" AND direction = " + direction + " AND day = " + (day + 1) + " AND time_from < " + (timeTo % 1440));
					if (!request.execute().next()) {
						autoN.post();
						count++;
					}
				}
				AutoNotification autoN = new AutoNotification().setDay("" + day).setDirection("" + direction).setTimeFrom("" + timeFrom).setTimeTo("" + 1439).setUserId(userId);
				SelectSQLRequest request = new SelectSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("*").setWhereFields(" user_id = \"" + userId + "\" AND direction = " + direction + " AND day = " + day + " AND time_to > " + timeFrom);
				if (!request.execute().next()) {
					autoN.post();
					count++;
				}
				if (count == 2) {
					State nextState = new State("saved state", false).setIsBackButtonOn(false).setIsKeyboardOn(false).setMessage("Автоуведомление включено.\nP.S. опция активирована дважды, т.к. искомый промежуток времени приходится на 2 разных дня (одна часть промежутка - на один день, а другая - на следующий день)\nПодробнее можно узнать, перейдя из главного меню в раздел включенных опций.").setHandler(new MainMenuCommandHandler().setNext(new UnknownCommandHandler()));
					nextState.get(0).setFullId("saved state");
					pCenter.setState(messenger, userId, nextState);
					return 1;
				} else if (count == 1) {
					State nextState = new State("saved state", false).setIsBackButtonOn(false).setIsKeyboardOn(false).setMessage("Автоуведомление включено частично.\nP.S. искомый промежуток времени приходится на 2 разных дня (одна часть промежутка - на один день, а другая - на следующий день). В результате добавления было обнаружено, что либо у тебя уже есть автоуведомление на какой-то день, либо этот день в системе уже/еще не определен.\nПодробнее можно узнать, перейдя из главного меню в раздел включенных опций.").setHandler(new MainMenuCommandHandler().setNext(new UnknownCommandHandler()));
					nextState.get(0).setFullId("saved state");
					pCenter.setState(messenger, userId, nextState);
					return 1;
				} else {
					State err = new ErrorState().setMessage("На этот день в этом направлении функция автоуведомления у тебя уже активирована.").setIsBackButtonOn(false);
					err.get(0).setFullId("saved state");
					pCenter.setState(messenger, userId, err);
					return -1;
				}
			}
		}
		return this.getNext().handle(messenger, message, state);
	}

}
