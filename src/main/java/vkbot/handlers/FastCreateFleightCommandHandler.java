package vkbot.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vkbot.Fleight;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.sql.RowArray;
import vkbot.sql.SelectSQLRequest;
import vkbot.state.ErrorState;
import vkbot.state.State;

public class FastCreateFleightCommandHandler extends MessageHandler {
	
	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		final ProcessingCenter pCenter = ProcessingCenter.getInstance();
		final String userId = message.getUserId();
		String text = message.getText().trim().toLowerCase().replaceAll("\\s+", " ");
		String[] directionNames = Fleight.getDirectionNames().trim().replaceAll("\\d+\\s-\\s", "").split("\n");
		StringBuilder directionNamesForRegEx = new StringBuilder("(");
		for (String direction : directionNames) {
			directionNamesForRegEx.append(direction.replaceAll("\\s", "\\\\s"));
			directionNamesForRegEx.append("||");
		}
		directionNamesForRegEx.replace(directionNamesForRegEx.length() - 2, directionNamesForRegEx.length(), "");
		directionNamesForRegEx.append(")");
		String regEx = "еду\\s" + directionNamesForRegEx.toString().toLowerCase() + "\\s(сегодня||завтра)\\sв\\s\\d{1,2}:\\d{2}$";
		Matcher matcher = Pattern.compile(regEx).matcher(text);
		if (matcher.find()) {
			String[] arr = text.split(" ");
			String direction = arr[1] + " " + arr[2];
			String day = arr[3];
			int hours = Integer.parseInt(arr[5].split(":")[0]);
			int minutes = Integer.parseInt(arr[5].split(":")[1]);
			if ((hours > 23) || (hours < 0) || (minutes < 0) || (minutes > 59)) {
				return this.getNext().handle(messenger, message, state);
			}
			Fleight fleight = new Fleight().setDay(day).setDirection(direction).setTime(hours * 60 + minutes).setUserId(userId);
			if (fleight.isExist()) {
				State err = new ErrorState().setMessage("На этот день в данном направлении у тебя уже есть поездка.").setIsBackButtonOn(false);
				err.get(0).setFullId("saved state");
				pCenter.setSavedState(userId, err);
				pCenter.setState(messenger, userId, err);
				return -1;
			}
			pCenter.addIncompletedFleight(userId, fleight);
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
			return 1;
		} 
		return this.getNext().handle(messenger, message, state);
	}

}
