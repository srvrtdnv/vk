package vkbot.handler;

import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.state.NullState;
import vkbot.state.State;

public class MainMenuCommandHandler extends MessageHandler{

	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		if (message.getText().equals("00")) {
			ProcessingCenter.getInstance().setState(messenger, message.getUserId(), NullState.getInstance().get(0));
			return 1;
		}
		else return getNext().handle(messenger, message, state);
	}

}
