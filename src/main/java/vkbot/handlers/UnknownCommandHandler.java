package vkbot.handlers;

import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.state.State;

/*
 * завершающий обработчик
 * выдает сообщение о неподдерживаемой команде
 */

public class UnknownCommandHandler extends MessageHandler {

	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		ProcessingCenter.getInstance().setState(messenger, message.getUserId(), state.get(0));
		return -1;
	}

}
