package vkbot.handler;

import java.util.List;

import vkbot.Deletable;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.state.State;

public class DeleteOptionCommandHandler extends MessageHandler {

	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		try {
			Integer index = Integer.parseInt(message.getText());
			ProcessingCenter pCenter = ProcessingCenter.getInstance();
			String userId = message.getUserId();
			List<Deletable> optionsList = pCenter.getOptions(userId);
			optionsList.get(index - 1).deleteFromTable();
			pCenter.setState(messenger, userId, state);
			return 1;
		} catch (Exception e) {
			ProcessingCenter.logError(e);
		}
		return this.getNext().handle(messenger, message, state);
	}

}
