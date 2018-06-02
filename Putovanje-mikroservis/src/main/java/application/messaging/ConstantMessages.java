package application.messaging;

public class ConstantMessages {
	public static String MICROSERVICE_NAME = "Putovanje";
	
	public static int TYPE_TRIP_START = 5;
	public static int TYPE_TRIP_END = 6;
    public static int TYPE_TRIP_DELETE = 7;

    public static String TYPE_TRIP_START_STRING = "Trip started";

	public static int STATUS_SUCCESS = 1;
	public static int STATUS_FAILED = 2;
	
	public static String DESC_SUCCESS = "Trip started successfully";
	public static String DESC_START_FAIL_USER_NOT_FOUND = "User not found";
	public static String DESC_START_FAIL_USER_COMMUNICATION = "Can't communicate with users service";
	public static String DESC_START_FAIL_INVALID_START_TIME = "Invalid trip start time";
	public static String DESC_START_INVALID_USER_ID = "Invalid user id";
	
	public static String DESC_STOP_FAIL_TRIP_NOT_FOUND = "Trip not found";
	public static String DESC_STOP_FAIL_INVALID_END_TIME = "Invalid end time";
	public static String DESC_STOP_ALREADY_ENDED = "Trip already ended";
	
	public static String DESC_LOC_INVALID_COORDS = "Invalid coordinates";
	public static String DESC_LOC_TRIP_NOT_FOUND = "Trip not found";
	public static String DESC_LOC_TRIP_ENDED = "Trip already ended";
	public static String DESC_LOC_USER_NOT_FOUND = "User not found";
	
	public static String DESC_TRIP_DEL_NOT_FOUND = "Trip does not exists";
	public static String DESC_TRIP_DEL_USER_NOT_FOUND = "User not found";

	public static String DESC_UNAUTHORIZED = "Unauthorized";
}
