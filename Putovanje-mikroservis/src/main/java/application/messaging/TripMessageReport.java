package application.messaging;

public class TripMessageReport {
	private String MessageType;
	private String MessageStatus;
	private String MessageDescription;
	private String MessageService;
	private String Username;
	private String TripName;
	
	public TripMessageReport(String messageType, String messageStatus, String messageDescription, String messageService,
			String username, String tripName) {
		super();
		MessageType = messageType;
		MessageStatus = messageStatus;
		MessageDescription = messageDescription;
		MessageService = messageService;
		Username = username;
		TripName = tripName;
	}

	public String getMessageType() {
		return MessageType;
	}

	public void setMessageType(String messageType) {
		MessageType = messageType;
	}

	public String getMessageStatus() {
		return MessageStatus;
	}

	public void setMessageStatus(String messageStatus) {
		MessageStatus = messageStatus;
	}

	public String getMessageDescription() {
		return MessageDescription;
	}

	public void setMessageDescription(String messageDescription) {
		MessageDescription = messageDescription;
	}

	public String getMessageService() {
		return MessageService;
	}

	public void setMessageService(String messageService) {
		MessageService = messageService;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getTripName() {
		return TripName;
	}

	public void setTripName(String tripName) {
		TripName = tripName;
	}
	
}
