package vkbot.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vkbot.Flight;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.state.NullState;
import vkbot.state.State;

public class FastFlightFindCommandHandler extends MessageHandler {
	
	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		final ProcessingCenter pCenter = this.getPCenter();
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
		String regEx = "кто едет\\s" + directionNamesForRegEx.toString().toLowerCase() + "\\s(сегодня||завтра)\\sв\\s\\d{1,2}[.:]\\d{2}(\\s([\\+-]30)){0,2}$";
		Matcher matcher = Pattern.compile(regEx).matcher(text);
		if (matcher.find()) {
			String[] arr = text.split(" ");
			String direction = arr[2] + " " + arr[3];
			String day = arr[4];
			if (arr[6].split("[.:]").length > 2) this.getNext().handle(messenger, message, state);
			int hours = Integer.parseInt(arr[6].split("[.:]")[0]);
			int minutes = Integer.parseInt(arr[6].split("[.:]")[1]);
			if ((hours > 23) || (hours < 0) || (minutes < 0) || (minutes > 59)) {
				return this.getNext().handle(messenger, message, state);
			}
			Flight flight = new Flight().setDay(day).setDirection(direction).setTime(hours * 60 + minutes).setUserId(userId).setAccuracyMinus(30).setAccuracyPlus(30);
			for (int index = 8; index < arr.length; index++) {
				String str = arr[index];
				int number;
				if (str.contains("-")) {
					number = Integer.parseInt(str.substring(1, str.length()));
					flight.setAccuracyMinus(number);
				}
				if (str.contains("+")) {
					number = Integer.parseInt(str.substring(1, str.length()));
					flight.setAccuracyPlus(number);
				}
			}
			pCenter.addIncompletedFlight(userId, flight);
			State newState = new TimeCommandHandler().buildResultState(flight).setPrevState(NullState.getState("2.1.1"));
			pCenter.setSavedState(userId, newState).setState(messenger, userId, newState);
			return 1;
		} 
		return this.getNext().handle(messenger, message, state);
	}

}
