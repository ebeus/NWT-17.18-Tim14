package application;

import javax.persistence.*;
import java.util.Date;
import java.util.TimeZone;

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

    public LogClass(){}

    LogClass(String message, String logSource, Date date) {
        this.message = message;
        this.logSource = logSource;
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format(
                "Log[id=%d, message='%s', logSource='%s', timeStamp='%s']",
                id, message, logSource, date);
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
