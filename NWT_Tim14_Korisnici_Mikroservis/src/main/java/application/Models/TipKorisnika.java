package application.Models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="tip_korisnika")
public class TipKorisnika {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotEmpty
    @Size(min=3,max=20,message="User type name should be between 3 and 20 characters")
    @NotBlank
    private String typeName;

    public TipKorisnika(){}

    public TipKorisnika(String typeName) {
        this.typeName = typeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }


    @Override
    public String toString() {
        return String.format(
                "TipKorisnika[id=%d, typeName='%s']",
                id,typeName
        );
    }

//    @OneToMany(mappedBy = "tip_korisnika_fk")
//    public Set<Korisnik> getKorisnici() {
//        return korisnici;
//    }
//
//    public void setKorisnici(Set<Korisnik> korisnici) {
//        this.korisnici = korisnici;
//    }
}
