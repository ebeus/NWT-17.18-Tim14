package ba.tim14.nwt.nwt_android.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class User implements Parcelable{

    private String username;
    private LatLng location;

    public User(String username, LatLng location){
        this.username = username;
        this.location = location;
    }

    public User() {    }

    protected User(Parcel in) {
        username = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUsername() {        return username; }

    public void setUsername(String username) {        this.username = username; }

    public LatLng getLocation() {        return location; }

    public void setLocation(LatLng location) {        this.location = location; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeParcelable(location, i);
    }
}
