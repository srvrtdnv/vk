package vkbot.handler;

import vkbot.Flight;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.state.State;

public class FlightSearchingDirectionHandler extends MessageHandler {
	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		ProcessingCenter pCenter = this.getPCenter();
		String userId = message.getUserId();
		String text = message.getText();
		try {
			int direction = Integer.parseInt(text);
			if (direction <= Flight.getDirectionsCount() && direction > 0) {
				Flight flight = new Flight().setDirection(Integer.parseInt(text)).setUserId(userId);
				pCenter.addIncompletedFlight(userId, flight);
				pCenter.setState(messenger, userId, state.get(1));
				return 1;
			}
		} catch (Exception e) {
			this.getPCenter().logError(e);
		}
		return this.getNext().handle(messenger, message, state);
	}
}
