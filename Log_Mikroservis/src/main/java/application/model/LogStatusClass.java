package application.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/*     status
    1 = uspješno
    2 = neuspješno    */
@Entity
@Table(name="status")
public class LogStatusClass implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String statusName;

    public LogStatusClass() {    }

    public LogStatusClass(@NotNull String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return String.format(
                "LogType[id=%d, statusName='%s']",
                id,statusName);
    }

    public Long getId() {        return id; }

    public String getStatusName() {        return statusName; }

    public void setStatusName(String statusName) { this.statusName = statusName; }

}
