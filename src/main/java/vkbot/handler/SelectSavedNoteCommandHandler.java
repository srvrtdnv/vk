package vkbot.handler;

import vkbot.MessageStandardClass;
import vkbot.ProcessingCenter;
import vkbot.SimpleMessenger;
import vkbot.sql.RowArray;
import vkbot.sql.SelectSQLRequest;
import vkbot.state.State;

public class SelectSavedNoteCommandHandler extends MessageHandler {
	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		if (message.getText().equals("1")) {
			ProcessingCenter pCenter = this.getPCenter();
			SelectSQLRequest request = new SelectSQLRequest("vk_bot", "user_ids", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("user_id = " + message.getUserId()).addSelectingField("saved_note");
			RowArray result = request.execute();
			if (result.next() && (result.getString("saved_note") != null)) {
				String note = result.getString("saved_note");
				return this.getNext().handle(messenger, message.setText(note), state);
			}
		}
		return this.getNext().handle(messenger, message, state);
	}
}
