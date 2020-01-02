package vkbot.state;

import java.util.ArrayList;

import vkbot.handlers.MessageHandler;

public class State {
	private boolean isMenuItem, isKeyboardOn = true, isMainMenuButtonOn = true, isBackButtonOn = true;
	private int stateId;
	private String message, fullId, name;
	private State prevState;
	private MessageHandler handler;
	private ArrayList<State> states = new ArrayList<State>();
	
	{
			if (this instanceof UnknownMessageState) states.add(this);
			//else states.add(new UnknownMessage)
	}
	
	
	public State(String fullId, boolean isMenuItem) {
		this.fullId = fullId;
		this.isMenuItem = isMenuItem;
		if (this instanceof UnknownMessageState) states.add(this);
		else if (!(this instanceof NullState)) addState(new UnknownMessageState(fullId + ".0", null));
	}
	
	public State(String fullId, String message, State prevState, MessageHandler handler, boolean isMenuItem) {
		this.message = message;
		this.prevState = prevState;
		this.handler = handler;
		this.fullId = fullId;
		this.isMenuItem = isMenuItem;
		if (this instanceof UnknownMessageState) states.add(this);
		else if (!(this instanceof NullState)) addState(new UnknownMessageState(fullId + ".0", null));
	}
	
	public State(String fullId, String message, MessageHandler handler, boolean isMenuItem) {
		this.message = message;
		this.handler = handler;
		this.fullId = fullId;
		this.isMenuItem = isMenuItem;
		if (this instanceof UnknownMessageState) states.add(this);
		else if (!(this instanceof NullState)) addState(new UnknownMessageState(fullId + ".0", null));
	}
	
	public String buildText() {
		StringBuilder sb = new StringBuilder(message);
		int itemNumber = 1;
		for (int i = 1; i < this.getStatesArraySize(); i++) {
			if (this.get(i).isMenuItem) {
				sb.append("\n" + itemNumber++ + " - ");
				sb.append(this.get(i).getName());
			}
		}
		//if ((this.getName() == null) || (!this.getName().equals("Главное меню"))) sb.append("\n0 - Назад\n00 - Главное меню");
		return sb.toString();
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getStateId() {
		return this.stateId;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public MessageHandler getHandler() {
		return this.handler;
	}
	
	public String getFullId() {
		return this.fullId;
	}
	
	public State getPrevState() {
		return this.prevState;
	}
	
	public boolean isKeyboardOn() {
		return this.isKeyboardOn;
	}
	
	public boolean isMainMenuButtonOn() {
		return this.isMainMenuButtonOn;
	}
	
	public boolean isBackButtonOn() {
		return this.isBackButtonOn;
	}
	
	public boolean isMenuItem() {
		return this.isMenuItem;
	}
	
	public State get(int index) {
		return states.get(index);
	}
	
	public int getStatesArraySize() {
		return states.size();
	}
	
	public State setName(String name) {
		this.name = name;
		return this;
	}
	
	public State setPrevState(State prevState) {
		this.prevState = prevState;
		return this;
	}
	
	public State setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public State setHandler(MessageHandler handler) {
		this.handler = handler;
		return this;
	}
	
	public State setNextHandler(MessageHandler handler) {
		this.handler.setNext(handler);
		return this;
	}
	
	public State setFullId(String fullId) {
		this.fullId = fullId;
		return this;
	}
	
	public State setIsKeyboardOn(boolean bool) {
		this.isKeyboardOn = bool;
		return this;
	}
	
	public State setIsMainMenuButtonOn(boolean bool) {
		this.isMainMenuButtonOn = bool;
		return this;
	}
	
	public State setIsBackButtonOn(boolean bool) {
		this.isBackButtonOn = bool;
		return this;
	}
	
	public State addState(State state) {
		states.add(state);
		state.setPrevState(this);
		return this;
	}
	
	public State addState(int index, State state) {
		states.add(index, state);
		state.setPrevState(this);
		return this;
	}
	
}
