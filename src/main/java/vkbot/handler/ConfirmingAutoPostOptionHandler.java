package vkbot.handler;

import vkbot.AutoPost;
import vkbot.Flight;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.service.AutoPostService;
import vkbot.state.ErrorState;
import vkbot.state.State;

public class ConfirmingAutoPostOptionHandler extends MessageHandler {
	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		ProcessingCenter pCenter = this.getPCenter();
		String userId = message.getUserId();
		Flight flight = pCenter.getIncompletedFlight(userId);
		
		if (!pCenter.isContainsFlight(userId)) {
			State errState = new ErrorState();
			errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.");
			pCenter.setState(messenger, userId, errState);
			return -1;
		}
		
		if (message.getText().equals("1")) {
			flight.setAutoPostOn(true);
			AutoPost autoP = new AutoPost().setDays(flight.getAutoPostDays()).setDirection(flight.getDirection()).setFrequency(flight.getFrequency()).setTime(flight.getTime()).setUserId(flight.getUserId()).setNote(flight.getNote()).setNumber(flight.getNumber());
			if (new AutoPostService().isAutoPostExist(autoP)) {
				State err = new ErrorState().setMessage("Автопубликация в этом направлении уже активирована&#8252;").setPrevState(state).setNextHandler(new BackCommandHandler());
				flight.setAutoPostOn(false);
				pCenter.setSavedState(userId, err);
				pCenter.setState(messenger, userId, err);
				return -1;
			}
			pCenter.setState(messenger, userId, state.get(1));
			return 1;
		} else if (message.getText().equals("2")) {
			pCenter.setState(messenger, userId, state.get(2));
			return 1;
		}
		
		return this.getNext().handle(messenger, message, state);
	}
}
