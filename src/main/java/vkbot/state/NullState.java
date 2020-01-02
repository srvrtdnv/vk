package vkbot.state;

import java.util.Arrays;


//еще один синглтон
public class NullState extends State {
	private static volatile NullState instance;
	
	private NullState() {
		super("Null", "Null", null, null, false);
	}
	
	public static NullState getInstance() {
		if (NullState.instance == null) {
			synchronized (NullState.class) {
				if (NullState.instance == null) {
					NullState.instance = new NullState();
				}
			}
		}
		return NullState.instance;
	}
	
	//можно объявить метод в State'е
	//и делать через рекурсию
	public static State getState(String stateId) {
		if (stateId.equals("")) return getInstance().get(0);
		String[] arr = stateId.replaceAll("(^\\.*)||(\\.*$)", "").split("\\.");
		State result = getInstance().get(0);
		for (String index : arr) {
			result = result.get(Integer.parseInt(index));
		}
		return result;
	}
	
	public void addStateWithFullId(State state) {
		String[] array = state.getFullId().split("\\.");
		State currentState = this.get(0);
		for (int index = 0; index < array.length - 1; index++) {
			currentState = currentState.get(Integer.parseInt(array[index]));
		}
		currentState.addState(Integer.parseInt(array[array.length - 1]), state);
	}
	
	@Override
	public State addState(State state) {
		if (state.getFullId().equals("")) super.addState(state);
		else this.addStateWithFullId(state);
		return this;
	}
}
