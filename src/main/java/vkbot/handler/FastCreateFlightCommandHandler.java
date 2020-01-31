package vkbot.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vkbot.Flight;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.service.FlightService;
import vkbot.state.ErrorState;
import vkbot.state.NullState;
import vkbot.state.State;

public class FastCreateFlightCommandHandler extends MessageHandler {
	
	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		final ProcessingCenter pCenter = ProcessingCenter.getInstance();
		final String userId = message.getUserId();
		String text = message.getText().trim().toLowerCase().replaceAll("\\s+", " ");
		String[] directionNames = Flight.getDirectionNames().trim().replaceAll("\\d+\\s-\\s", "").split("\n");
		StringBuilder directionNamesForRegEx = new StringBuilder("(");
		for (String direction : directionNames) {
			directionNamesForRegEx.append(direction.replaceAll("\\s", "\\\\s"));
			directionNamesForRegEx.append("||");
		}
		directionNamesForRegEx.replace(directionNamesForRegEx.length() - 2, directionNamesForRegEx.length(), "");
		directionNamesForRegEx.append(")");
		String regEx = "еду\\s" + directionNamesForRegEx.toString().toLowerCase() + "\\s(сегодня||завтра)\\sв\\s\\d{1,2}[.:]\\d{2}$";
		Matcher matcher = Pattern.compile(regEx).matcher(text);
		if (matcher.find()) {
			String[] arr = text.split(" ");
			String direction = arr[1] + " " + arr[2];
			String day = arr[3];
			if (arr[5].split("[.:]").length > 2) this.getNext().handle(messenger, message, state);
			int hours = Integer.parseInt(arr[5].split("[.:]")[0]);
			int minutes = Integer.parseInt(arr[5].split("[.:]")[1]);
			if ((hours > 23) || (hours < 0) || (minutes < 0) || (minutes > 59)) {
				return this.getNext().handle(messenger, message, state);
			}
			Flight flight = new Flight().setDay(day).setDirection(direction).setTime(hours * 60 + minutes).setUserId(userId);
			if (new FlightService().isFlightExist(flight)) {
				State err = new ErrorState().setMessage("На этот день в данном направлении у тебя уже есть поездка&#8252;").setIsBackButtonOn(false);
				err.get(0).setFullId("saved state");
				pCenter.setSavedState(userId, err);
				pCenter.setState(messenger, userId, err);
				return -1;
			}
			pCenter.addIncompletedFlight(userId, flight);
			pCenter.setState(messenger, userId, NullState.getState("1.1.1.1"));
			return 1;
		} 
		return this.getNext().handle(messenger, message, state);
	}

}
