package vkbot.handlers;

import vkbot.MessageStandardClass;
import vkbot.SimpleMessenger;
import vkbot.state.State;

/*
 * синглтон с потокозащищенностью
 * +
 * класс-заглушка
 * 
 * ну просто шоб было
 */
public class NullHandler extends MessageHandler {
	private static MessageHandler instance;
	
	{
		setNext(this);
	}
	
	private NullHandler() {
		
	}
	
	public static MessageHandler getInstance() {
		if (instance == null) {
			synchronized (NullHandler.class) {
				if (instance == null) instance = new NullHandler();
			}
		}
		return instance;
	}

	@Override
	public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
		// TODO Auto-generated method stub
		return -1;
	}
}
