package ba.tim14.nwt.nwt_android.classes;

public class Trip {

    private String date;
    private String duration;
    private String kilometers;

    public Trip(String date, String duration, String kilometers) {
        this.date = date;
        this.duration = duration;
        this.kilometers = kilometers;
    }

    public String getDate() {        return date;    }

    public void setDate(String date) {        this.date = date;    }

    public String getDuration() {        return duration;    }

    public void setDuration(String duration) {        this.duration = duration;    }

    public String getKilometers() {        return kilometers;    }

    public void setKilometers(String kilometers) {        this.kilometers = kilometers;    }
}
