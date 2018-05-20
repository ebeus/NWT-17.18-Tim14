package ba.tim14.nwt.nwt_android.classes;

/**
 * Created by serio on 20/05/2018.
 */

public class TipKorisnika {
    private Long id;
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
        return "TipKorisnika{" +
                "id=" + id +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
