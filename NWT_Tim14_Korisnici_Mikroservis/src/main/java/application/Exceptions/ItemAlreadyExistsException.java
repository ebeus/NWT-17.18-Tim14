package application.Exceptions;

public class ItemAlreadyExistsException extends RuntimeException {

    public ItemAlreadyExistsException(String itemName, String itemType) {
        super(itemType +" with name: '"+ itemName +"' already exists");
    }
}
