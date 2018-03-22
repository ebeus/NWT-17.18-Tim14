package application;

import javax.persistence.*;
import java.util.Date;

@Entity
public class LogClass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long status;
    private String message;
    private String logSource;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamp;
    private String user;
    private Long size;

    protected LogClass() { }

    LogClass(String message, String logSource, Date timeStamp) {
        this.message = message;
        this.logSource = logSource;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return String.format(
                "Log[id=%d, message='%s', logSource='%s', timeStamp='%s']",
                id, message, logSource, timeStamp);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLogSource() {
        return logSource;
    }

    public void setLogSource(String logSource) {
        this.logSource = logSource;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }



}
