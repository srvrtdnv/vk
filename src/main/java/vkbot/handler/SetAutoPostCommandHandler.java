package vkbot.handler;

import vkbot.MessageStandardClass;
import vkbot.SimpleMessenger;
import vkbot.state.State;

/*
 * обработчик установки автопубликаций
 * (оказался не особо нужным)
 * в данный момент существует просто 
 */

public class SetAutoPostCommandHandler extends MessageHandler {

	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		if (message.getText().equals("000")) {
			
		} else {
			return this.getNext().handle(messenger, message, state);
		}
		// TODO Auto-generated method stub
		return 1;
	}

}
