package application.MScommunication;

import application.model.LogClass;
import application.repositories.LogClassRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    private static final Logger log = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    LogClassRepository logClassRepository;

    public void receiveMessage(String message) {
        log.info("Received message [" + message + "]");

        logClassRepository.save(getRecievedLog(message));

    }

    private LogClass getRecievedLog(String message) {
        JSONObject obj = new JSONObject(message);

        int messageType = obj.getInt("messageType");
        int messageStatus = obj.getInt("messageStatus");
        String messageDescription = obj.getString("messageDescription");
        String messageMicroservice = obj.getString("messageMicroservice");
        String username = obj.getString("username");
        String tripName = "NO DATA";
        if(messageType > 3 && messageType < 6)
            tripName = obj.getString("tripName");

        return new LogClass((long) messageType,(long) messageStatus,messageDescription, messageMicroservice,username,tripName);
    }

}
