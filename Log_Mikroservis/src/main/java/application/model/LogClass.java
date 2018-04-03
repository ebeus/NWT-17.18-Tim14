package application.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.Date;

@Entity
public class LogClass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Long logTypeId;

    @NotNull
    @Size(min=1,max=50,message="LogTypeName must be min=1 and max=50 chars long")
    private String logTypeName;
    /*  logTypeId  |    logTypeName
          1        |      Sign in
          2        |      Sign up
          3        |      Register
          4        |     Started trips
          5        |     Stopped trips   */

    @NotNull
    private Long status;
    /*     status
        1 = uspješno
        2 = neuspješno    */

    @NotNull
    @Size(min=1,max=50,message="Message must be min=1 and max=50 chars long")
    private String message;

    @NotNull
    @Size(min=1,max=50,message="LogSource must be min=1 and max=50 chars long")
    private String logSource;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Size(min=1,max=50,message="User name must be min=1 and max=50 chars long")
    private String user;

    @Size(min=1,max=50,message="Trip name must be min=1 and max=50 chars long")
    private String tripName;

    protected LogClass(){}

    public LogClass(Long logTypeId, String logTypeName, Long status, String message, String logSource, String user, String tripName) {
        this.logTypeId = logTypeId;
        this.logTypeName = logTypeName;
        this.status = status;
        this.message = message;
        this.logSource = logSource;
        this.date = Calendar.getInstance().getTime();
        this.user = user;
        this.tripName = tripName;
    }

    @Override
    public String toString() {
        return String.format(
                "Log[id=%d, logTypeId=%d, " +
                        "logTypeName=%s," +
                        "status=%d," +
                        "message='%s'," +
                        "logSource='%s'," +
                        "date='%s'," +
                        "user='%s'," +
                        "tripName='%s']",
                id,logTypeId, logTypeName,status, message, logSource, date, user , tripName);
    }

    public Long getId() {   return id;   }

    public void setId(Long id) {        this.id = id;    }

    public Long getLogTypeId() {        return logTypeId;    }

    public void setLogTypeId(Long logTypeId) {        this.logTypeId = logTypeId;    }

    public String getLogTypeName() {        return logTypeName;    }

    public void setLogTypeName(String logTypeName) {        this.logTypeName = logTypeName;    }

    public Long getStatus() {        return status;    }

    public void setStatus(Long status) {        this.status = status;    }

    public String getMessage() {        return message;    }

    public void setMessage(String message) {        this.message = message;    }

    public String getLogSource() {        return logSource;    }

    public void setLogSource(String logSource) {        this.logSource = logSource;    }

    public Date getDate() {        return date;    }

    public void setDate(Date date) {        this.date = date;    }

    public String getUser() {        return user;    }

    public void setUser(String user) {        this.user = user;    }

    public String getTripName() {        return tripName;    }

    public void setTripName(String tripName) {        this.tripName = tripName;    }



}
