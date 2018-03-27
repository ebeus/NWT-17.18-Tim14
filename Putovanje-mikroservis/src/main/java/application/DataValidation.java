package application;

import java.lang.Math;

public class DataValidation {
	
	public static Boolean validateCoordinates(Double lat, Double lng) {
		if(Math.abs(lat) > 90.0 || Math.abs(lng) > 180.0)
			return false;
		return true;
	}
	
}
