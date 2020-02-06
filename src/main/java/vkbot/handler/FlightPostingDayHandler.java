package vkbot.handler;

import vkbot.Flight;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.service.FlightService;
import vkbot.state.ErrorState;
import vkbot.state.State;

public class FlightPostingDayHandler extends MessageHandler {
	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		ProcessingCenter pCenter = this.getPCenter();
		String userId = message.getUserId();
		
		if (!pCenter.isContainsFlight(userId)) {
			State errState = new ErrorState();
			errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.");
			pCenter.setState(messenger, userId, errState);
			return -1;
		}
		
		try {
			Integer day = Integer.parseInt(message.getText());
			if (day <= Flight.getDaysCount() && day > 0) {
				Flight flight = pCenter.getIncompletedFlight(userId);
				flight.setDay(day);
				if (new FlightService().isFlightExist(flight)) {
					State err = new ErrorState().setMessage("На этот день в данном направлении у тебя уже есть поездка&#8252;").setIsBackButtonOn(false);
					err.get(0).setFullId("saved state");
					pCenter.setSavedState(userId, err);
					pCenter.setState(messenger, userId, err);
					return -1;
				}
				pCenter.setState(messenger, message.getUserId(), state.get(1));
				return 1;
			}
		} catch (Exception e) {
			this.getPCenter().logError(e);
		}
		return this.getNext().handle(messenger, message, state);
	}
}
