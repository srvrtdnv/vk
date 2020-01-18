package vkbot.handler;

import vkbot.*;
import vkbot.state.State;

public abstract class MessageHandler {
	private MessageHandler next;
		
	public MessageHandler setNext(MessageHandler next) {
		if (this.next == null) {
			this.next = next;
			return this;
		}
		else if (this.next instanceof UnknownCommandHandler) {
			next.setNext(this.next);
			this.next = next;
			return this;
		}
		this.next.setNext(next);
		return this;
	}
	
	public MessageHandler getNext() {
		return this.next;
	}
	
	public abstract int handle(SimpleMessenger messenger, MessageStandardClass message, State state);
}
