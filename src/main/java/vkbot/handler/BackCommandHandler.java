package vkbot.handler;

import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.state.State;

public class BackCommandHandler extends MessageHandler {
	
	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		if (message.getText().equals("0")) {
			ProcessingCenter.getInstance().setState(messenger, message.getUserId(), state.getPrevState());
			return 1;
		}
		else return getNext().handle(messenger, message, state);
	}
	
}
