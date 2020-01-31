package vkbot.handler;

import vkbot.Flight;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.state.ErrorState;
import vkbot.state.State;

public class PhoneNumberHandler extends MessageHandler {
	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		final ProcessingCenter pCenter = ProcessingCenter.getInstance();
		final String userId = message.getUserId();
		
		if (!pCenter.isContainsFlight(userId)) {
			State errState = new ErrorState();
			errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.");
			pCenter.setState(messenger, userId, errState);
			return -1;
		}
		
		Flight flight = pCenter.getIncompletedFlight(userId);
		flight.setNumber(message.getText());
		pCenter.setState(messenger, userId, state.get(1));
		return 1;
	}
}
