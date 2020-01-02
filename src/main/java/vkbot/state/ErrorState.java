package vkbot.state;

import vkbot.handlers.*;

public class ErrorState extends State {

	public ErrorState() {
		super ("saved state", "", new MainMenuCommandHandler().setNext(new UnknownCommandHandler()), false);
		
	}
	
	@Override
	public String buildText() {
		return this.getMessage();
	}
}
