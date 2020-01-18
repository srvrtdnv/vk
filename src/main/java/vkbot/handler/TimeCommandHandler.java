package vkbot.handler;


import java.util.List;

import vkbot.Flight;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.state.FlightInfoState;
import vkbot.state.State;

/*
 * обработчик ввода времени
 * 
 * не очень нравится решение со свитчем
 */

public class TimeCommandHandler extends MessageHandler {

	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		try {
			State newState;
			final ProcessingCenter pCenter = ProcessingCenter.getInstance();
			final String userId = message.getUserId();
			String text = message.getText().replaceAll("\\s+", " ");
			String[] array = text.split(" ");
			if (array[0].split("[.:]").length > 2) this.getNext().handle(messenger, message, state);
			int hours = Integer.parseInt(array[0].split("[.:]")[0]);
			int minutes = Integer.parseInt(array[0].split("[.:]")[1]);
			int accuracyPlus = 30;
			int accuracyMinus = 30;
			
			
			if (array.length > 1) {
				if (array[1].contains("+")) {
					accuracyPlus = Integer.parseInt(array[1].replace("+", ""));
				} else if (array[1].contains("-")) {
					accuracyMinus = Integer.parseInt(array[1].replace("-", ""));
				} else {
					this.getNext().handle(messenger, message, state);
				}
			}
			if (array.length == 3) {
				if (array[2].contains("+")) {
					accuracyPlus = Integer.parseInt(array[2].replace("+", ""));
				} else if (array[2].contains("-")) {
					accuracyMinus = Integer.parseInt(array[2].replace("-", ""));
				} else {
					this.getNext().handle(messenger, message, state);
				}
			}
			if (hours < 0 || hours > 23 || minutes > 59 || minutes < 0 || accuracyMinus > 120 || accuracyPlus > 120) {
				return this.getNext().handle(messenger, message, state);
			}
			int time = hours * 60 + minutes;
			Flight fleight;
			switch (state.getFullId())  {
			case "1.1.1":
				fleight = pCenter.getIncompletedFlight(userId).setTime(time);
				pCenter.setState(messenger, userId, state.get(1));
				break;
			case "2.1.1":
				fleight = pCenter.getIncompletedFlight(userId).setTime(time).setAccuracyMinus(accuracyMinus).setAccuracyPlus(accuracyPlus);
				newState = buildResultState(fleight).setPrevState(state);
				pCenter.setSavedState(userId, newState).setState(messenger, userId, newState);
				break;
			}
		} catch (Exception e) {
			ProcessingCenter.logError(e);
			return this.getNext().handle(messenger, message, state);
		}
		
		return 1;
	}
	
	public State buildResultState(Flight flight) {
		List<FlightInfoState> flightsList = flight.find();
		State resultState = new State("saved state", false) {
			@Override
			public String buildText(String userId) {
				StringBuilder sb = new StringBuilder(super.getMessage());
				int itemNumber = 1;
				for (int i = 1; i < this.getStatesArraySize(); i++) {
					if (this.get(i).isMenuItem()) {
						sb.append("\n" + itemNumber++ + ") ");
						sb.append(this.get(i).getName());
					}
				}
				sb.append("\n\n000 - Включить автоуведомление (будет настроено на заданное ранее время)");
				return sb.toString();
			}
		};

		resultState.setName(flight.getDirection() + ":" + flight.getDay() + ":" + flight.getTimeFrom() + ":" + flight.getTimeTo());
		resultState.setHandler(new BackCommandHandler().setNext(new UnknownCommandHandler()).setNext(new MainMenuCommandHandler()).setNext(new SetAutoNotificationCommandHandler()));
		resultState.get(0).setFullId("saved state");
		
		for (FlightInfoState fis : flightsList) {
			resultState.addState(fis);
		}
		if (resultState.getStatesArraySize() == 1) {
			resultState.setMessage("В заданный промежуток времени поездки отсутствуют.");
		} else {
			resultState.setMessage("Ниже представлены все найденные поездки (если тебя просят отправить предоплату - перед тобой скорее всего мошенник):");
		}
		resultState.setNextHandler(new SetAutoNotificationCommandHandler()).get(0).setFullId("saved state");
		return resultState;
	}
	

}
