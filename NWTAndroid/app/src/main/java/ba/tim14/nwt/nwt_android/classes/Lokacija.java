package ba.tim14.nwt.nwt_android.classes;

/**
 * Created by serio on 24/05/2018.
 */

import java.sql.Timestamp;

public class Lokacija {
    private long id;
    private Timestamp timestamp;
    private Double latitude;
    private Double longitude;
    protected Lokacija() {}

    public Lokacija(Timestamp timestamp, Double latitude, Double longitude) {
        super();
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Lokacija{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}