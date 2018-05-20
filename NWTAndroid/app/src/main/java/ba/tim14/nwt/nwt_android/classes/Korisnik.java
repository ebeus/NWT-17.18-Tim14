package ba.tim14.nwt.nwt_android.classes;

/**
 * Created by elvedin on 20/05/2018.
 *
 */

public class Korisnik {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private Long userTypeId;
    private Long userGroupId;
    private Long deviceId;
    private TipKorisnika userType;
    private GrupaKorisnika userGroup;
    private Uredjaj device;

    public Korisnik(){}

    public Korisnik(String firstName, String lastName, String userName, String password, Long userTypeId, Long userGroupId, Long deviceId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.userTypeId = userTypeId;
        this.userGroupId = userGroupId;
        this.deviceId = deviceId;
    }

    public void updateFields(Korisnik k){
        this.firstName = k.firstName;
        this.lastName = k.lastName;
        this.userName = k.userName;
        this.password = k.password;
        this.userTypeId = k.userTypeId;
        this.userGroupId = k.userGroupId;
        this.deviceId = k.deviceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(Long userTypeId) {
        this.userTypeId = userTypeId;
    }

    public Long getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Long userGroupId) {
        this.userGroupId = userGroupId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public TipKorisnika getUserType() {
        return userType;
    }

    public void setUserType(TipKorisnika userType) {
        this.userType = userType;
    }

    public GrupaKorisnika getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(GrupaKorisnika userGroup) {
        this.userGroup = userGroup;
    }

    public Uredjaj getDevice() {
        return device;
    }

    public void setDevice(Uredjaj device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "Korisnik{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", userTypeId=" + userTypeId +
                ", userGroupId=" + userGroupId +
                ", deviceId=" + deviceId +
                '}';
    }
}