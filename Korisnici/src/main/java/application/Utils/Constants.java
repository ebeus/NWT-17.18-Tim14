package application.Utils;

public class Constants {

    public static final String USERS_ROUTING_KEY="user.";
    public static final String USERS_QUEUE="users-queue";
    public static final String TOPIC_EXCHANGE_NAME="users-exchange";

    public static final String USER_REGISTERED = "Korisnik registrovan!";
    public static final String USER_NOT_REGISTERED = "Korisnik nije registrovan!";
    public static final String MESSAGING_USER_EXISTS = "Korisnik vec postoji";
    public static final String MESSAGING_MICROSERVICE = "Korisnik mikroservis";
    public static final String USER_CHANGED = "Korisnik promijenjen!";
    public static final String USER_DELETED = "Korisnik obrisan!";
    public static final String USER_NOT_DELETED = "Korisnik nije obrisan!";
    public static final String USER_SIGNED_IN = "Korisnik signed-in";
    public static final String USER_SIGNED_OUT = "Korisnik signed-out";

    public static final String ALL_USERS_CALLED = "Prikazi sve korisnike";

    public static final int MESSAGING_USER_SIGN_IN = 1;
    public static final int MESSAGING_USER_SIGN_OUT = 2;
    public static final int MESSAGING_USER_ADD = 3;
    public static final int MESSAGING_USER_DELETE = 4;
    public static final int MESSAGING_EVERYTHING_OK = 1;
    public static final int MESSAGING_SOMETHING_WRONG = 2;

    public static final String MESSAGE_UNAUTHORIZED = "UNAUTHORIZED";
    public static final String MESSAGE_DOES_NOT_EXIST = "DOES NOT EXIST";

}
