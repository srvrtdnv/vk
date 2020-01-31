package vkbot.handler;

import vkbot.Flight;
import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.state.ErrorState;
import vkbot.state.State;

/*
 * обработчик ввода дней недели для автопубликации
 * если введено корректно
 * то создается состояние с данными поездки
 * для дальнейшего подтверждения
 */
public class WeekDaysCommandHandler extends MessageHandler {
	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		String text = message.getText();
		String userId = message.getUserId();
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		
		if (!pCenter.isContainsFlight(userId)) {
			State errState = new ErrorState();
			errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.");
			pCenter.setState(messenger, userId, errState);
			return -1;
		}
		if (text.replaceAll("[([пП][нНтТ])([вВчЧ][тТ])([сС][рРбБ])([вВ][сС])[\\s]]", "").trim().equals("") ) {
			final Flight flight = pCenter.getIncompletedFlight(userId);
			flight.setAutoPostDays(text.toLowerCase());
			
			State postConfirmingState = new State("1.1.1.1.1.1.1.1.1", false) {
				@Override
				public String buildText(String userId) {
					StringBuilder result = new StringBuilder("Проверь и потверди.\n");
					result.append(flight.getFullInfo());
					result.append("\n1 - Подтвердить");
					return result.toString();
				}
			};
			
			pCenter.setSavedState(userId, postConfirmingState);
			pCenter.setState(messenger, userId, state.get(1));
			return 1;
		}
		return this.getNext().handle(messenger, message, state);
	}
}
