package application.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.*;

@Entity
public class Uredjaj {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @NotNull
    @NotEmpty
    @Size(min=3,max=20,message="Device name should be between 3 and 20 characters")
    @NotBlank
    private String deviceName;
    @NotNull
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
        return String.format(
                "Uređaj[id=%d, deviceName='%s', deviceTypeId=%d]",
                id,deviceName,deviceTypeId
        );
    }
}
