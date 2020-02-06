package vkbot.handler;

import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.state.ErrorState;
import vkbot.state.NullState;
import vkbot.state.State;

public class PostConfirmingHandler extends MessageHandler {
	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		if (message.getText().equals("1")) {
			String userId = message.getUserId();
			ProcessingCenter pCenter = this.getPCenter();
			
			if (!pCenter.isContainsFlight(userId)) {
				State errState = new ErrorState();
				errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.");
				pCenter.setState(messenger, userId, errState);
				return -1;
			}
			
			int result = pCenter.getIncompletedFlight(userId).post(messenger);
			if (result < 1) {
				messenger.sendText(new MessageStandardClass("Ошибка на сервере. Запись опубликована некорректно. Проверь наличие в опубликованных поездках.", userId, null, null));
			} else {
				messenger.sendText(new MessageStandardClass("Запись добавлена&#9989;\nP.S. помни - водитель в ответе за тех, кого сажает в машину. Пристегивайтесь ремнями безопасности.", userId, null, null));
			}
			pCenter.setState(messenger, userId, NullState.getState(""));
			return 1;
		}
		return this.getNext().handle(messenger, message, state);
	}
}
