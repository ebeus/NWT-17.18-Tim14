package ba.tim14.nwt.nwt_android.classes;

/**
 * Created by serio on 20/05/2018.
 */

public class GrupaKorisnika {
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