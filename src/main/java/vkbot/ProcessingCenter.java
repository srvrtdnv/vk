package vkbot;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vkbot.handlers.*;
import vkbot.sql.RowArray;
import vkbot.sql.SelectSQLRequest;
import vkbot.state.MaintenanceState;
import vkbot.state.NullState;
import vkbot.state.State;

public class ProcessingCenter {
	private volatile boolean isMaintenance = false, isHereIncompletedProcess = false;
	private static volatile ProcessingCenter instance;
	private HashMap<String, String> usersState = new HashMap<String, String>();
	private MessageHandler commonHandler;
	private String passFileName, url, driver;
	private HashMap<String, Fleight> incompletedFlights = new HashMap<String, Fleight>();
	private HashMap<String, State> savedLists = new HashMap<String, State>();
	private HashMap<String, List<Deletable>> options = new HashMap<String, List<Deletable>>();
	private HashMap<String, State> savedStates = new HashMap<String, State>();
	
	private ProcessingCenter() {
		commonHandler = new BackCommandHandler();
		commonHandler.setNext(new MainMenuCommandHandler());
		commonHandler.setNext(new SelectMenuItemCommandHandler());
		commonHandler.setNext(new UnknownCommandHandler());
	}
	
	public void clearAllMaps() {
		this.incompletedFlights.clear();
		this.options.clear();
		this.savedLists.clear();
		this.savedStates.clear();
		this.usersState.clear();
	}
	
	public void startProcessing(SimpleMessenger messenger, MessageStandardClass message) throws SQLException {
		try {
			if (isMaintenance) {
				this.setState(messenger, message.getUserId(), new MaintenanceState());
				return;
			}
			this.isHereIncompletedProcess = true;
			String userId = message.getUserId();
			String stateId = "";
			State state;
			if (usersState.containsKey(userId)) {
				stateId = usersState.get(userId);
			} else {
				SelectSQLRequest request = new SelectSQLRequest().addSelectingField("*").setWhereFields("user_id = " + "\"" + userId + "\"");
				request.setDatabase("vk_bot").setDriver(this.driver).setPassFileName(this.passFileName).setUsername("root").setTableName("user_ids").setUrl(this.url);
				RowArray result = request.execute();
				stateId = result.next() ? result.getString("state") : "";
				usersState.put(userId, stateId);
			}
			if (stateId.equals("saved state")) state = this.savedStates.get(userId);
			else state = NullState.getState(stateId);
			MessageHandler handler = state.getHandler();
			handler.handle(messenger, message, state);
			this.isHereIncompletedProcess = false;
		} catch (Exception e) {
			System.out.println("processing center.startProcessing : " + e + "\n");
		}
	}

	public void addIncompletedFleight(String userId, Fleight fleight) {
		this.incompletedFlights.put(userId, fleight);
	}
	
	public void addOption(String userId, Deletable option) {
		if (!this.options.containsKey(userId)) options.put(userId, new ArrayList<Deletable>());
		this.options.get(userId).add(option);
	}
	
	public void setState(SimpleMessenger messenger, String userId, State state) {
		String fullId = state.getFullId();
		String text = state.buildText();
		if (fullId.equals("saved state")) this.savedStates.put(userId, state);
		if (state.isBackButtonOn()) {
			text += "\n0 - Назад";
		}
		if (state.isMainMenuButtonOn()) {
			text += "\n00 - Главное меню";
		}
		if (fullId.equals("flights list")) {
			this.savedLists.put(userId, state);
		}
		usersState.put(userId, fullId);
		String messageText = text;
		messenger.sendText(new MessageStandardClass(messageText, userId, "vk", null).setIsKeyboardOn(state.isKeyboardOn()).setIsBackButtonOn(state.isBackButtonOn()).setIsMainMenuButtonOn(state.isMainMenuButtonOn()));
	}
	
	public void setStateWithUserIds(SimpleMessenger messenger, List<String> userIds, State state) {
		State prevState;
		State newState;
		String prevStateId = "";
		String text = state.buildText();
		for (String userId : userIds) {
			if (usersState.containsKey(userId)) {
				prevStateId = usersState.get(userId);
			} else {
				SelectSQLRequest request = new SelectSQLRequest().addSelectingField("state").setWhereFields("user_id = " + userId );
				request.setDatabase("vk_bot").setDriver(this.driver).setPassFileName(this.passFileName).setUsername("root").setTableName("user_ids").setUrl(this.url);
				RowArray result = request.execute();
				prevStateId = result.next() ? result.getString("state") : "";
			}
			if (prevStateId.equals("saved state")) prevState = this.savedStates.get(userId);
			else prevState = NullState.getState(prevStateId);
			newState = new State("saved state", false).setPrevState(prevState).setHandler(state.getHandler()).setMessage(text);
			newState.get(0).setFullId("saved state");
			savedStates.put(userId, newState);
			usersState.put(userId, "saved state");
		}
		if (state.isBackButtonOn()) {
			text += "\n0 - Назад";
		}
		if (state.isMainMenuButtonOn()) {
			text += "\n00 - Главное меню";
		}
		messenger.sendTextWithUserIds(new MessageStandardClass(text, userIds, "vk", null).setIsKeyboardOn(state.isKeyboardOn()).setIsBackButtonOn(state.isBackButtonOn()).setIsMainMenuButtonOn(state.isMainMenuButtonOn()));
	}
	
	
	public ProcessingCenter setNextHandler (MessageHandler handler) {
		commonHandler.setNext(handler);
		return this;
	}
	
	public ProcessingCenter setPassFileName(String passFileName) {
		this.passFileName = passFileName;
		return this;
	}

	public ProcessingCenter setUrl(String url) {
		this.url = url;
		return this;
	}

	public ProcessingCenter setDriver(String driver) {
		this.driver = driver;
		return this;
	}

	public ProcessingCenter setSavedState(String userId, State state) {
		this.savedStates.put(userId, state);
		return this;
	}
	
	public ProcessingCenter setIsMaintenance(boolean isMaintenance) {
		this.isMaintenance = isMaintenance;
		return this;
	}
	
	public boolean isMaintenance() {
		return this.isMaintenance;
	}
	
	public ProcessingCenter setIsHereIncompletedProcess(boolean isHereIncompletedProcess) {
		this.isHereIncompletedProcess = isHereIncompletedProcess;
		return this;
	}
	
	public boolean isHereIncompletedProcess() {
		return this.isHereIncompletedProcess;
	}
	
	public boolean isContainsFleight(String userId) {
		return this.incompletedFlights.containsKey(userId);
	}
	
	public List<Deletable> getOptions (String userId) {
		List<Deletable> list = this.options.get(userId);
		if (list == null) {
			list = new ArrayList<Deletable>();
			this.options.put(userId, list);
		}
		return this.options.get(userId);
	}
	
	public HashMap<String, String> getStates() {
		return this.usersState;
	}
	
	public MessageHandler getHandler() {
		return this.commonHandler;
	}
	
	public static ProcessingCenter getInstance() {
		if (ProcessingCenter.instance == null) {
			synchronized (ProcessingCenter.class) {
				if (ProcessingCenter.instance == null) ProcessingCenter.instance = new ProcessingCenter();
			}
		}
		return ProcessingCenter.instance;
	}

	public String getPassFileName() {
		return passFileName;
	}


	public Fleight getIncompletedFleight(String userId) {
		return incompletedFlights.get(userId);
	}

	public String getUrl() {
		return url;
	}

	public String getDriver() {
		return driver;
	}

	public State getSavedState(String userId) {
		return this.savedStates.get(userId);
	}
	
}
