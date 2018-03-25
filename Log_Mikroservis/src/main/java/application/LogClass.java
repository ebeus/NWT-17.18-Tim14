package application;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
public class LogClass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long logTypeId;
    private String logTypeName;
    private Long status;
    private String message;
    private String logSource;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    private String user;
    private String tripName;

    protected LogClass(){}

    LogClass(Long logTypeId, String logTypeName, Long status, String message, String logSource, String user, String tripName) {
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
