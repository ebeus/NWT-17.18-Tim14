package application.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class KorisnikNotFoundException extends RuntimeException {

    public KorisnikNotFoundException(Long userId) {
        super("Could not find user '" + userId + "'.");
    }

    public KorisnikNotFoundException(String userName){
        super("Could not find user '" + userName + "'.");
    }
}