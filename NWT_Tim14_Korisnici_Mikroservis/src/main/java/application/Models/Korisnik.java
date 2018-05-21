package application.Models;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name="korisnik")
public class Korisnik {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotEmpty
    @Size(min=3,max=20,message="First name should be between 3 and 20 characters")
    @NotBlank
    private String firstName;
    @NotNull
    @NotEmpty
    @Size(min=3,max=20,message="Last name should be between 3 and 20 characters")
    @NotBlank
    private String lastName;
    @NotNull
    @NotEmpty
    @Size(min=3,max=20,message="Username should be between 3 and 20 characters")
    @NotBlank
    private String userName;
    private String password;
    private Long userTypeId;
    private Long userGroupId;
    private Long deviceId;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity=TipKorisnika.class )
    @JoinColumn(name = "tip_korisnika_id")
    private TipKorisnika userType;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity=GrupaKorisnika.class )
    @JoinColumn(name = "grupa_korisnika_id")
    private GrupaKorisnika userGroup;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity=Uredjaj.class )
    @JoinColumn(name = "uredjaj_id")
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
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String string="";
        try {
            string = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return string;
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

}
