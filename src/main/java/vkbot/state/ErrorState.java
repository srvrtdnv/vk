package vkbot.state;

import vkbot.handler.*;

public class ErrorState extends State {

	public ErrorState() {
		super ("saved state", "", new MainMenuCommandHandler().setNext(new UnknownCommandHandler()), false);
		
	}
	
	@Override
	public String buildText(String userId) {
		return this.getMessage();
	}
}
