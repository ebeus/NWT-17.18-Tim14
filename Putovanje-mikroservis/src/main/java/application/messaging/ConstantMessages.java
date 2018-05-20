package application.messaging;

public class ConstantMessages {
	public static String MICROSERVICE_NAME = "Putovanje";
	
	public static String TYPE_TRIP_START = "Start trip";
	public static String TYPE_TRIP_END = "End trip";
	public static String TYPE_LOCATION_ERROR = "Location error";
	public static String TYPE_TRIP_DELETE = "Delete error";
	
	public static String STATUS_SUCCESS = "Success";
	public static String STATUS_FAILED = "Failed";
	
	public static String DESC_SUCCESS = "Trip started successfully";
	public static String DESC_START_FAIL_USER_NOT_FOUND = "User not found";
	public static String DESC_START_FAIL_USER_COMMUNICATION = "Can't communicate with users service";
	public static String DESC_START_FAIL_INVALID_START_TIME = "Invalid trip start time";
	
	public static String DESC_STOP_FAIL_TRIP_NOT_FOUND = "Trip not found";
	public static String DESC_STOP_FAIL_INVALID_END_TIME = "Invalid end time";
	public static String DESC_STOP_ALREADY_ENDED = "Trip already ended";
	
	public static String DESC_LOC_INVALID_COORDS = "Invalid coordinates";
	public static String DESC_LOC_TRIP_NOT_FOUND = "Trip not found";
	public static String DESC_LOC_TRIP_ENDED = "Trip already ended";
	public static String DESC_LOC_USER_NOT_FOUND = "User not found";
	
	public static String DESC_TRIP_DEL_NOT_FOUND = "Trip does not exists";
	public static String DESC_TRIP_DEL_USER_NOT_FOUND = "User not found";
	
}
