package vkbot.handler;

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
		messenger.sendText(new MessageStandardClass("&#10071;Некорректная команда. Попробуй еще раз.&#10071;", message.getUserId(), null, null));
		this.getPCenter().setState(messenger, message.getUserId(), state);
		return -1;
	}

}
