package vkbot;

public interface SimpleMessenger {
	public int sendText(MessageStandardClass message);
	public int sendTextWithUserIds(MessageStandardClass message);
}
