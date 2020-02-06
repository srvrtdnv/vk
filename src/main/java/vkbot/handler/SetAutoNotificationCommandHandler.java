package vkbot.handler;

import vkbot.AutoNotification;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.state.NullState;
import vkbot.state.State;

/*
 * обработчик установки автоуведомлений
 */

public class SetAutoNotificationCommandHandler extends MessageHandler {

	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		if (message.getText().equals("000")) {
			ProcessingCenter pCenter = this.getPCenter();
			String userId = message.getUserId();
			String[] arr = state.getName().split(":");
			int direction = Integer.parseInt(arr[0]);
			int day = Integer.parseInt(arr[1]);
			int timeFrom = Integer.parseInt(arr[2]);
			int timeTo = Integer.parseInt(arr[3]);
			
			AutoNotification autoN = new AutoNotification().setUserId(userId).setDay(day).setDirection(direction).setTimeFrom(timeFrom).setTimeTo(timeTo);
			
			switch (autoN.post()) {
			case -1:
			case 0:
				messenger.sendText(new MessageStandardClass("На этот день в этом направлении функция автоуведомления у тебя уже активирована&#8252;", userId, null, null));
				pCenter.setState(messenger, userId, NullState.getState(""));
				break;
			case 1:
				messenger.sendText(new MessageStandardClass("Автоуведомление включено частично.\nP.S. искомый промежуток времени приходится на 2 разных дня (одна часть промежутка - на один день, а другая - на следующий день). В результате добавления было обнаружено, что либо у тебя уже есть автоуведомление на какой-то день, либо этот день в системе уже/еще не определен.\nПодробнее можно узнать, перейдя из главного меню в раздел включенных опций.", userId, null, null));
				pCenter.setState(messenger, userId, NullState.getState(""));
				break;
			case 2:
				messenger.sendText(new MessageStandardClass("Автоуведомление включено&#9989;\nP.S. опция активирована дважды, т.к. искомый промежуток времени приходится на 2 разных дня (одна часть промежутка - на один день, а другая - на следующий день)\nПодробнее можно узнать, перейдя из главного меню в раздел включенных опций.", userId, null, null));
				pCenter.setState(messenger, userId, NullState.getState(""));
				break;
			case 3:
				messenger.sendText(new MessageStandardClass("Автоуведомление включено&#9989;", userId, null, null));
				pCenter.setState(messenger, userId, NullState.getState(""));
				break;
			}
			
			return 1;
		}
		return this.getNext().handle(messenger, message, state);
	}

}
