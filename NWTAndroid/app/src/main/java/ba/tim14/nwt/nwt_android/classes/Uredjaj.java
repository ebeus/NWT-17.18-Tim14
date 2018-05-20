package ba.tim14.nwt.nwt_android.classes;

/**
 * Created by serio on 20/05/2018.
 */

public class Uredjaj {
    private Long id;
    private String deviceName;
    private Long deviceTypeId;

    public Uredjaj() { }

    public Uredjaj(String deviceName, Long deviceTypeId) {
        this.deviceName = deviceName;
        this.deviceTypeId = deviceTypeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Long getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(Long deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    @Override
    public String toString() {
        return "Uredjaj{" +
                "id=" + id +
                ", deviceName='" + deviceName + '\'' +
                ", deviceTypeId=" + deviceTypeId +
                '}';
    }
}