package vkbot;

import java.io.BufferedReader;
import java.io.FileReader;

import com.vk.api.sdk.callback.longpoll.CallbackApiLongPoll;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.messages.Keyboard;
import com.vk.api.sdk.objects.messages.KeyboardButton;
import com.vk.api.sdk.objects.messages.KeyboardButtonAction;
import com.vk.api.sdk.objects.messages.KeyboardButtonActionType;
import com.vk.api.sdk.objects.messages.KeyboardButtonColor;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.MessageAttachment;
import com.vk.api.sdk.queries.messages.MessagesSendQuery;
import com.vk.api.sdk.queries.messages.MessagesSendQueryWithUserIds;

import vkbot.sql.RowArray;
import vkbot.sql.SelectSQLRequest;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bot extends CallbackApiLongPoll implements SimpleMessenger {

	private GroupActor groupActor;
	private VkApiClient vk;
	private ProcessingCenter pCenter;
	private int actorIndex = 0;
	private ArrayList<GroupActor> actors= new ArrayList<GroupActor>();
	private Map<String, Integer> randIds = new HashMap<String, Integer>();
	
	public Bot(VkApiClient client, GroupActor actor) {
		super(client, actor, 25);
		this.groupActor = actor;
		this.vk = client;
		this.pCenter = ProcessingCenter.getInstance();
	}
	
	private MessageStandardClass convertMessageType (Message message) {
		String text;
		if (message.getPayload() == null) {
			text = message.getText();
		} else if (message.getPayload().contains(".")) {
			text = message.getPayload().replaceAll("\\d\\.", "");
			System.out.println("REGEX: " + text);
			text = text.replaceAll("1\\d*", "");
			System.out.println("REGEX: " + text);
			text = message.getPayload().replaceAll("([\\d&&[^0]]\\d*)$", "");
			text = text.replaceAll("\\d\\.", "");
			System.out.println("REGEX3: " + text);
		} else {
			text = message.getPayload();
		}
		String userId = message.getPeerId().toString();
		Map<String, String> data = new HashMap<String, String>();
		
		for (MessageAttachment attachment : message.getAttachments()) {
			String name = attachment.getType().name();
			switch (name) {
			case "STICKER" :
				data.put(attachment.getSticker().getStickerId().toString(), name);
				break;
			case "PHOTO" :
				data.put(attachment.getPhoto().getSizes().get(5).getUrl().toString(), name);
				break;
			case "GRAFFITI" :
				data.put(attachment.getGraffiti().getUrl().toString(), name);
				break;
			case "AUDIO" :
				data.put("id: " + attachment.getAudio().getId().toString() + "; url: " + attachment.getAudio().getUrl().toString(), name);
				break;
			case "VIDEO" :
				data.put(attachment.getVideo().getId().toString(), name);
				break;
			case "DOC" :
				data.put(attachment.getDoc().getUrl().toString(), name);
				break;
			case "AUDIO_MESSAGE" :
				data.put(attachment.getAudioMessage().getLinkMp3().toString(), name);
				break;
			}
		}
		
		return new MessageStandardClass(text, userId, "VK", data);
	}
	
	
	@Override
	public void messageNew(Integer groupId, Message message) {
		try{ 
			message = vk.messages().getById(groupActor, message.getId()).execute().getItems().get(0);
			pCenter.startProcessing(this, convertMessageType(message));
		} catch (Exception e) {
			System.out.println("a" + e);
		}
	}
	
	
	
	@Override
	public int sendText(MessageStandardClass message) {
		try {
			MessagesSendQuery msgSend = vk.messages().send(getNextActor()).randomId(getNextRandId(message.getUserId())).peerId(Integer.parseInt(message.getUserId())).message(message.getText()).groupId(groupActor.getGroupId());
			if (message.isKeyboardOn()) {
				msgSend.keyboard(createKeyboard(message.getText()));
			} else {
				String str = message.isBackButtonOn() ? "\n0 - Назад" : "";
				str += message.isMainMenuButtonOn() ? "\n00 - Главное меню" : "";
				if (!str.equals("")) msgSend.keyboard(createKeyboard(str));
			}
			int result =  msgSend.execute();
			return result;
		} catch (Exception e) {
			System.out.println(e);
		}
		return -1;
	}

	public void uploadGroupActors(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			reader.readLine();
			String line = reader.readLine();
			while(line != null) {
				actors.add(new GroupActor(groupActor.getGroupId(), line));
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			
		}
	}
	
	public GroupActor getNextActor() {
		int size = actors.size();
		if (size > 0) {
			GroupActor actor = actors.get(actorIndex);
			actorIndex = (actorIndex + 1) % size;
			return actor;
		}
		return groupActor;
	}
	
	public Integer getNextRandId(String userId) {
		if (!this.randIds.containsKey(userId)) {
			SelectSQLRequest request = new SelectSQLRequest("vk_bot", "rand_id", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("user_id = \"" + userId + "\"").addSelectingField("random_id");
			RowArray response = request.execute();
			if (response.next()) {
				this.randIds.put(userId, Integer.parseInt(response.getString("random_id")) + 1);
				return Integer.parseInt(response.getString("random_id")) + 1;
			}
			else { 
				this.randIds.put(userId, 1);
				return 1;
			}
		}
		int res = this.randIds.get(userId) + 1;
		this.randIds.put(userId, res);
		return res;
	}
	
	
	/*
	 * написано коряво
	 * но роботает жи
	 */
	public Keyboard createKeyboard(String text) {
		int arrayInd = 0;
		ArrayList<String> buttonsLabel = new ArrayList<String>();
		ArrayList<String> payloads = new ArrayList<String>();
		List<List<KeyboardButton>> buttons = new ArrayList<List<KeyboardButton>>();
		List<KeyboardButton> but0 = new ArrayList<KeyboardButton>();
		List<KeyboardButton> but1 = new ArrayList<KeyboardButton>();
		List<KeyboardButton> but2 = new ArrayList<KeyboardButton>();
		List<KeyboardButton> but3 = new ArrayList<KeyboardButton>();
		buttons.add(but1);
		buttons.add(but2);
		buttons.add(but3);
		buttons.add(new ArrayList<KeyboardButton>());
		buttons.add(but0);
		Pattern pattern = Pattern.compile("\\n\\d*\\s-\\s*[А-Яа-я\\s]*[А-Яа-я]");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			buttonsLabel.add(matcher.group().replaceAll("\\n\\d*\\s-\\s*", ""));
			payloads.add(matcher.group().replaceAll("[(\\n)(\\s-\\s*[А-Яа-я\\s]*[А-Яа-я])]*", ""));
		}
		if (buttonsLabel.contains("Главное меню")) {
			KeyboardButtonAction kba = new KeyboardButtonAction().setLabel("Главное меню").setPayload("1.001").setType(KeyboardButtonActionType.TEXT);
			KeyboardButton kb = new KeyboardButton().setAction(kba).setColor(KeyboardButtonColor.PRIMARY);
			but0.add(kb);
			buttonsLabel.remove("Главное меню");
		}
		if (buttonsLabel.contains("Назад")) {
			KeyboardButtonAction kba = new KeyboardButtonAction().setLabel("Назад").setPayload("1.01").setType(KeyboardButtonActionType.TEXT);
			KeyboardButton kb = new KeyboardButton().setAction(kba).setColor(KeyboardButtonColor.PRIMARY);
			but0.add(kb);
			buttonsLabel.remove("Назад");
		}
		for (int index = 0; index < buttonsLabel.size(); index++) {
			String label = buttonsLabel.get(index);
			if (payloads.get(index).replaceAll("0", "").trim().equals("")) {
				payloads.set(index, "1." + payloads.get(index) + "1");
			}
			label = label.length() > 40 ? label.substring(0, 37) + "..." : label;
			KeyboardButtonAction kba = new KeyboardButtonAction().setLabel(label).setPayload(payloads.get(index)).setType(KeyboardButtonActionType.TEXT);
			KeyboardButton kb = new KeyboardButton().setAction(kba).setColor(KeyboardButtonColor.PRIMARY);
			buttons.get(arrayInd).add(kb);
			arrayInd = (arrayInd + 1) % buttons.size();
		}
		for (int index = 0; index < buttons.size(); index++) {
			List<KeyboardButton> arr = buttons.get(index);
			if (arr.size() == 0) {
				buttons.remove(arr);
				index--;
			}
		}
		return new Keyboard().setButtons(buttons).setOneTime(true);
	}

	@Override
	public int sendTextWithUserIds(MessageStandardClass message) {
		try {
			List<String> userIds = message.getUserIds();
			Integer[] ids = new Integer[userIds.size()];
			for (int index = 0; index < ids.length; index++) {
				ids[index] = Integer.parseInt(userIds.get(index));
			}
			MessagesSendQueryWithUserIds msgSend = vk.messages().sendWithUserIds(getNextActor(), ids).randomId(getNextRandId(message.getUserId())).message(message.getText()).groupId(groupActor.getGroupId());
			if (message.isKeyboardOn()) {
				msgSend.keyboard(createKeyboard(message.getText()));
			} else {
				String str = message.isBackButtonOn() ? "\n0 - Назад" : "";
				str += message.isMainMenuButtonOn() ? "\n00 - Главное меню" : "";
				if (!str.equals("")) msgSend.keyboard(createKeyboard(str));
			}
			msgSend.execute();
			return 1;
		} catch (Exception e) {
			System.out.println(e);
		}
		return -1;
	}
}
