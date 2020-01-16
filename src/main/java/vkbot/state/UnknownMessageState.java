package vkbot.state;

import vkbot.handler.BackCommandHandler;
import vkbot.handler.MainMenuCommandHandler;
import vkbot.handler.MessageHandler;
import vkbot.handler.UnknownCommandHandler;

public class UnknownMessageState extends State {
	
	public static final String UNKNOWN_COMMAND = "Некорректная команда. Отправь цифру 0, чтобы вернуться назад и попробовать еще раз, или 00 - чтобы выйти в главное меню.";
	
	{
		setHandler(new BackCommandHandler().setNext(new MainMenuCommandHandler()).setNext(new UnknownCommandHandler()));
	}
	
	public UnknownMessageState(String fullId, State prevState, MessageHandler handler) {
		super(fullId, UNKNOWN_COMMAND, prevState, handler, false);
	}
	
	public UnknownMessageState(String fullId, MessageHandler handler) {
		super(fullId, UNKNOWN_COMMAND, handler, false);
	}

}
