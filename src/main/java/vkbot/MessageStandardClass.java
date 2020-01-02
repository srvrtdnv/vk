package vkbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageStandardClass {
	private String text, userId, service;
	private List<String> userIds = new ArrayList<String>();;
	private Map<String, String> data;
	private boolean isKeyboardOn = true,
					isMainMenuButtonOn = true,
					isBackButtonOn = true;
	
	public MessageStandardClass(String text, String userId, String service, Map<String, String> data) {
		this.text = text;
		this.userId = userId;
		this.service = service;
		this.data = data;
	}
	public MessageStandardClass(String text, List<String> userIds, String service, Map<String, String> data) {
		this.text = text;
		this.userIds = userIds;
		this.service = service;
		this.data = data;
	}
	
	public MessageStandardClass setIsKeyboardOn(boolean isKeyboardOn) {
		this.isKeyboardOn = isKeyboardOn;
		return this;
	}
	
	public MessageStandardClass setIsMainMenuButtonOn(boolean isMainMenuButtonOn) {
		this.isMainMenuButtonOn = isMainMenuButtonOn;
		return this;
	}
	
	public MessageStandardClass setIsBackButtonOn(boolean isBackButtonOn) {
		this.isBackButtonOn = isBackButtonOn;
		return this;
	}
	
	public MessageStandardClass setText(String text) {
		this.text = text;
		return this;
	}
	
	public boolean isTextExist() {
		return text.equals("") ? false : true;
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
	
	public Map<String, String> getData() {
		return this.data;
	}
	
	public String getUserId() {
		return this.userId;
	}
	
	public List<String> getUserIds() {
		return this.userIds;
	}
	
	public String getService() {
		return service;
	}
	
	public String getText() {
		return this.text;
	}
}
