package vkbot.handlers;


import vkbot.AutoNotification;
import vkbot.AutoPost;
import vkbot.Flight;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.sql.RowArray;
import vkbot.sql.SelectSQLRequest;
import vkbot.state.State;

/*
 *  обработчик-костыль
 *  выдает состояние со списком включенных опций
 *  
 *  решение такое себе
 *  лучше сделать это в логике состояния
 *  и оставить только SelectMenuItemCommandHandler
 */

public class OptionsListCommandHandler extends MessageHandler{
	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		
		if (message.getText().equals("4")) {
			final String userId = message.getUserId();
			State newState = new State("4", "", null, false) {
				@Override
				public String buildText() {
					try {
						ProcessingCenter pCenter = ProcessingCenter.getInstance();
						pCenter.getOptions(userId).clear();
						SelectSQLRequest request = new SelectSQLRequest("vk_bot", "auto_notifications", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).addSelectingField("*").setWhereFields("user_id = " + userId);
						int index = 1;
						
						RowArray rs = request.execute();
						StringBuilder sb1 = new StringBuilder("Настроенные автоуведомления:\n");
						while (rs.next()) {
							int direction = rs.getInt("direction");
							int day = rs.getInt("day");
							sb1.append("Направление: ");
							sb1.append(Flight.getDirectionString(direction));
							sb1.append("\nДень: ");
							sb1.append(Flight.getDayString(day));
							sb1.append("\nВремя: ");
							Integer tFrom = rs.getInt("time_from");
							Integer tFromMinutes = tFrom % 60;
							Integer tTo = rs.getInt("time_to");
							Integer tToMinutes = tTo % 60;
							sb1.append("с " + tFrom / 60 + ":" + (tFromMinutes < 10 ? "0" + tFromMinutes : "" + tFromMinutes) + " до " + tTo / 60 + ":" + (tToMinutes < 10 ? "0" + tToMinutes : "" + tToMinutes));
							sb1.append("\n\nОтправь " + index++ + ", чтобы отключить.\n\n");
							pCenter.addOption(userId, new AutoNotification().setId(rs.getString("id")));
						}
						
						request.setTableName("auto_post");
						rs = request.execute();
						StringBuilder sb2 = new StringBuilder("\nНастроенные автопубликации:\n");
						while (rs.next()) {
							int direction = rs.getInt("direction");
							sb2.append("Направление: ");
							sb2.append(Flight.getDirectionString(direction));
							sb2.append("\nВремя: ");
							Integer time = rs.getInt("time");
							Integer minutes = time % 60;
							sb2.append(time / 60 + ":" +(minutes < 10 ? "0" + minutes : minutes));
							sb2.append("\nРежим: ");
							sb2.append(rs.getInt("frequency") == 0 ? "на текущую неделю" : "еженедельно");
							sb2.append("\nДни: ");
							sb2.append(rs.getString("days"));
							sb2.append("\n\nОтправь " + index++ + ", чтобы отключить.\n\n");
							pCenter.addOption(userId, new AutoPost().setId(rs.getString("id")).setDirection(rs.getString("direction")));
						}
						
						request.setTableName("flights");
						rs = request.execute();
						StringBuilder sb3 = new StringBuilder("\nОпубликованные поездки:\n");
						while (rs.next()) {
							int direction = rs.getInt("direction");
							int day = rs.getInt("day");
							sb3.append("Направление: " + Flight.getDirectionString(direction));
							sb3.append("\nВремя: ");
							Integer time = rs.getInt("time");
							Integer minutes = time % 60;
							sb3.append(time / 60 + ":" + (minutes < 10 ? "0" + minutes : minutes));
							sb3.append("\nДень: " + Flight.getDayString(day));
							sb3.append("\nНомер: ");
							sb3.append(rs.getString("number"));
							sb3.append("\nЗаметка: ");
							sb3.append(rs.getString("note"));
							sb3.append("\n\nОтправь " + index++ + ", чтобы удалить.\n\n");
							pCenter.addOption(userId, new Flight().setDay(day).setDirection(direction).setId(rs.getInt("id")));
						}
						
						String result = "";
						String str1 = sb1.toString();
						if (!str1.replace("Настроенные автоуведомления:\n", "").trim().equals("")) result += str1;
						String str2 = sb2.toString();
						if (!str2.replace("Настроенные автопубликации:\n", "").trim().equals("")) result += str2;
						String str3 = sb3.toString();
						if(!str3.replace("Опубликованные поездки:\n", "").trim().equals("")) result += str3; 
						if(result.trim().equals("")) {
							result = "Включенных опций или опубликованных поездок нет.";
						}
						
						return result;
					} catch (Exception e) {
						ProcessingCenter.logError(e);
					}
					return "error";
				}
			};
			newState.setIsMainMenuButtonOn(false);
			ProcessingCenter.getInstance().setSavedState(userId, newState).setState(messenger, userId, newState);
			return 1;
		}
		return getNext().handle(messenger, message, state);
	}
}
