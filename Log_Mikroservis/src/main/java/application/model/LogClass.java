package application.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name="log")
public class LogClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(name="logTypeId", insertable = true, updatable = false)
    private Long logTypeId;

    //@Column(name="status", insertable = true, updatable = false)
    private Long status;

    @NotNull
    @Size(min=1,max=50,message="Message must be min=1 and max=50 chars long")
    private String message;

    @NotNull
    @Size(min=1,max=50,message="LogSource must be min=1 and max=50 chars long")
    private String source;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Size(min=1,max=50,message="User name must be min=1 and max=50 chars long")
    private String user;

    @Size(min=1,max=50,message="Trip name must be min=1 and max=50 chars long")
    private String tripName;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = LogTypeClass.class)
   // @OneToOne(fetch = FetchType.EAGER, targetEntity = LogTypeClass.class)
    @JoinColumn(name = "logTypeId", insertable = false, updatable = false)
    private LogTypeClass logType;


    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = LogStatusClass.class)
   // @OneToOne(fetch = FetchType.EAGER, targetEntity = LogStatusClass.class)
    @JoinColumn(name = "status", insertable = false, updatable = false)
    private LogStatusClass logStatus;


    protected LogClass(){}

    public LogClass(Long logTypeId, Long status, String message, String source, String user, String tripName) {
        this.logTypeId = logTypeId;
        this.status = status;
        this.message = message;
        this.source = source;
        this.date = Calendar.getInstance().getTime();
        this.user = user;
        this.tripName = tripName;
    }

    @Override
    public String toString() {
        return String.format(
                "Log[id=%d, logTypeId=%d, " +
                        "status=%d," +
                        "message='%s'," +
                        "source='%s'," +
                        "date='%s'," +
                        "user='%s'," +
                        "tripName='%s']",
                id,logTypeId,status, message, source, date, user , tripName);
    }

    public Long getId() {   return id;   }

    public void setId(Long id) {        this.id = id;    }

    public Long getLogTypeId() {        return logTypeId;    }

    public void setLogTypeId(Long logTypeId) {        this.logTypeId = logTypeId;    }

    public Long getStatus() {        return status;    }

    public void setStatus(Long status) {        this.status = status;    }

    public String getMessage() {        return message;    }

    public void setMessage(String message) {        this.message = message;    }

    public String getSource() {        return source;    }

    public void setSource(String source) {        this.source = source;    }

    public Date getDate() {        return date;    }

    public void setDate(Date date) {        this.date = date;    }

    public String getUser() {        return user;    }

    public void setUser(String user) {        this.user = user;    }

    public String getTripName() {        return tripName;    }

    public void setTripName(String tripName) {        this.tripName = tripName;    }



}
