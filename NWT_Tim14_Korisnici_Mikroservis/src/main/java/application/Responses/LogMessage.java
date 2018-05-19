package application.Responses;

import com.google.gson.Gson;

public class LogMessage {
    private int messageType;
    private int messageStatus;
    private String messageDescription;
    private String messageMicroservice;
    private String messageUsername;

    public LogMessage(int messageType, int messageStatus, String messageDescription, String messageMicroservice, String messageUsername) {
        this.messageType = messageType;
        this.messageStatus = messageStatus;
        this.messageDescription = messageDescription;
        this.messageMicroservice = messageMicroservice;
        this.messageUsername = messageUsername;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageDescription() {
        return messageDescription;
    }

    public void setMessageDescription(String messageDescription) {
        this.messageDescription = messageDescription;
    }

    public String getMessageMicroservice() {
        return messageMicroservice;
    }

    public void setMessageMicroservice(String messageMicroservice) {
        this.messageMicroservice = messageMicroservice;
    }

    public String getMessageUsername() {
        return messageUsername;
    }

    public void setMessageUsername(String messageUsername) {
        this.messageUsername = messageUsername;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
