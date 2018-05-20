package application.Responses;

import com.google.gson.Gson;

public class LogMessage {
    private int messageType;
    private int messageStatus;
    private String messageDescription;
    private String messageMicroservice;
    private String username;

    public LogMessage(int messageType, int messageStatus, String messageDescription, String messageMicroservice, String username) {
        this.messageType = messageType;
        this.messageStatus = messageStatus;
        this.messageDescription = messageDescription;
        this.messageMicroservice = messageMicroservice;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
