package application.Models;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.validator.constraints.Length;

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
    @Length(max=100)
    private String password;
    private String email;

    @ManyToOne(targetEntity = TipKorisnika.class)
    @JoinColumn(name = "tip_korisnika_id")
    private TipKorisnika userType;

    @ManyToOne(targetEntity = GrupaKorisnika.class)
    @JoinColumn(name = "grupa_korisnika_id")
    private GrupaKorisnika userGroup;


    public Korisnik(){}

    public Korisnik(String firstName, String lastName, String userName, String password, String email,
    		TipKorisnika tip, GrupaKorisnika grupa) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.userType = tip;
        this.userGroup = grupa;
        this.email = email;
    }

    public void updateFields(Korisnik k){
        this.firstName = k.firstName;
        this.lastName = k.lastName;
        this.userName = k.userName;
        this.password = k.password;
        this.email = k.email;
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

    public String getEmail() {  return email; }

    public void setEmail(String email) { this.email = email;    }



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

}
