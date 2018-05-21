package application.messaging;

public class TripMessageReport {
	private int MessageType;
	private int MessageStatus;
	private String MessageDescription;
	private String MessageMicroservice;
	private String Username;
	private String TripName;
	
	public TripMessageReport(int messageType, int messageStatus, String messageDescription, String messageMicroservice,
			String username, String tripName) {
		super();
		MessageType = messageType;
		MessageStatus = messageStatus;
		MessageDescription = messageDescription;
		MessageMicroservice = messageMicroservice;
		Username = username;
		TripName = tripName;
	}

	public int getMessageType() {
		return MessageType;
	}

	public void setMessageType(int messageType) {
		MessageType = messageType;
	}

	public int getMessageStatus() {
		return MessageStatus;
	}

	public void setMessageStatus(int messageStatus) {
		MessageStatus = messageStatus;
	}

	public String getMessageDescription() {
		return MessageDescription;
	}

	public void setMessageDescription(String messageDescription) {
		MessageDescription = messageDescription;
	}

	public String getMessageMicroservice() {		return MessageMicroservice; }

	public void setMessageMicroservice(String messageMicroservice) {		MessageMicroservice = messageMicroservice; }

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
