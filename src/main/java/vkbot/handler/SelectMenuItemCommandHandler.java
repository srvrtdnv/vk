package vkbot.handler;

import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.state.State;


/*
 * обработчик выбора пунктов меню
 */

public class SelectMenuItemCommandHandler extends MessageHandler {

	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		try {
			int menuItemNumber = Integer.parseInt(message.getText());
			if (menuItemNumber < state.getStatesArraySize() && menuItemNumber > 0) {
				this.getPCenter().setState(messenger, message.getUserId(), state.get(menuItemNumber));
				return 1;
			}
			else return getNext().handle(messenger, message, state);
		} catch (Exception e) {
			this.getPCenter().logError(e);
		}
		return this.getNext().handle(messenger, message, state);
	}

}
