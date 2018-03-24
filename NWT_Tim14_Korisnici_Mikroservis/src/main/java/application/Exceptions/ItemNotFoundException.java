package application.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(Long itemId, String itemType) {
        super("Could not find " + itemType + " with id: " + itemId + ".");
    }

    public ItemNotFoundException(String itemName, String itemType){
        super("Could not find " +itemType + " with name: " + itemName + ".");
    }
}