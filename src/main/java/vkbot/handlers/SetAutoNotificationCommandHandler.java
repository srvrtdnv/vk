package vkbot.handlers;

import vkbot.AutoNotification;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
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
				State err = new ErrorState().setMessage("На этот день в этом направлении функция автоуведомления у тебя уже активирована").setIsBackButtonOn(false);
				err.get(0).setFullId("saved state");
				pCenter.setState(messenger, userId, err);
				return -1;
			}
		}
		return this.getNext().handle(messenger, message, state);
	}

}
