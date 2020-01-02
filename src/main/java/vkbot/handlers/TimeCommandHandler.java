package vkbot.handlers;


import java.util.List;

import vkbot.Fleight;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.sql.RowArray;
import vkbot.sql.SelectSQLRequest;
import vkbot.state.FleightInfoState;
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
			int hours = Integer.parseInt(array[0].split(":")[0]);
			int minutes = Integer.parseInt(array[0].split(":")[1]);
			int accuracyPlus = 30;
			int accuracyMinus = 30;
			
			if (hours < 0 || hours > 23 || minutes > 59 || minutes < 0) {
				return this.getNext().handle(messenger, message, state);
			}
			
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
			int time = hours * 60 + minutes;
			Fleight fleight;
			switch (state.getFullId())  {
			case "1.1.1":
				fleight = pCenter.getIncompletedFleight(userId).setTime(time);
				pCenter.setState(messenger, userId, new State("1.1.1.1", false) {
					@Override
					public String buildText() {
						String text = "Введи номер телефона.";
						SelectSQLRequest request = new SelectSQLRequest("vk_bot", "user_ids", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("user_id = " + userId).addSelectingField("saved_number");
						RowArray result = request.execute();
						if (result.next() && (result.getString("saved_number") != null)) {
							String number = result.getString("saved_number");
							text += "\nРанее использованный номер: " + number+ "\n1 - Использовать сохраненный номер";
						}
						return text;
					}
				});
				break;
			case "2.1.1":
				fleight = pCenter.getIncompletedFleight(userId).setTime(time).setAccuracyMinus(accuracyMinus).setAccuracyPlus(accuracyPlus);
				newState = buildResultState(fleight).setPrevState(state);
				pCenter.setSavedState(userId, newState).setState(messenger, userId, newState);
				break;
			}
		} catch (Exception e) {
			return this.getNext().handle(messenger, message, state);
		}
		
		return 1;
	}
	
	public State buildResultState(Fleight fleight) {
		List<FleightInfoState> flightsList = fleight.find();
		State resultState = new State("saved state", false) {
			@Override
			public String buildText() {
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

		resultState.setName(fleight.getDirection() + ":" + fleight.getDay() + ":" + fleight.getTimeFrom() + ":" + fleight.getTimeTo());
		resultState.setHandler(new BackCommandHandler().setNext(new UnknownCommandHandler()).setNext(new MainMenuCommandHandler()).setNext(new SetAutoNotificationCommandHandler()));
		resultState.get(0).setFullId("saved state");
		
		for (FleightInfoState fis : flightsList) {
			resultState.addState(fis);
		}
		if (resultState.getStatesArraySize() == 1) {
			resultState.setMessage("В заданный промежуток времени поездки отсутствуют");
		} else {
			resultState.setMessage("Ниже представлены все найденные поездки:");
		}
		resultState.setNextHandler(new SetAutoNotificationCommandHandler()).get(0).setFullId("saved state");
		return resultState;
	}
	

}
