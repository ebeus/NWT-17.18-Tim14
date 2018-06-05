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
    private String email;
    private Long userTypeId;
    private Long userGroupId;
    private Long deviceId;
    private TipKorisnika userType;
    private GrupaKorisnika userGroup;

    public Korisnik(){}

    public Korisnik(String firstName, String lastName, String userName, String password, String email, Long userTypeId, Long userGroupId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.userTypeId = userTypeId;
        this.userGroupId = userGroupId;
    }

    public void updateFields(Korisnik k){
        this.firstName = k.firstName;
        this.lastName = k.lastName;
        this.userName = k.userName;
        this.password = k.password;
        this.email = k.email;
        this.userTypeId = k.userTypeId;
        this.userGroupId = k.userGroupId;
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

    public String getEmail() {        return email; }

    public void setEmail(String email) {        this.email = email; }

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

    @Override
    public String toString() {
        return "Korisnik{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", userTypeId=" + userGroupId +
                ", userGroupId=" + userTypeId +
                '}';
    }
}