package application;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TipKorisnika {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String typeName;

    TipKorisnika(){}

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
}
