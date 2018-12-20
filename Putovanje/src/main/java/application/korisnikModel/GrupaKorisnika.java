package application.korisnikModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class GrupaKorisnika {
    private Long id;
    @NotNull
    @NotEmpty
    @Size(min=3,max=20,message="User type name should be between 3 and 20 characters")
    @NotBlank
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
