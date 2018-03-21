package application;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class GrupaKorisnika {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String groupName;

    public GrupaKorisnika() {}

    public GrupaKorisnika(String groupName) {
        this.groupName = groupName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
