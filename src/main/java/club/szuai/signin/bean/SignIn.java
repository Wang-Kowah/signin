package club.szuai.signin.bean;

public class SignIn {
    private Integer id;

    private Integer classId;

    private Integer weekStartTime;

    private Integer week;

    private String signinIds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getWeekStartTime() {
        return weekStartTime;
    }

    public void setWeekStartTime(Integer weekStartTime) {
        this.weekStartTime = weekStartTime;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public String getSigninIds() {
        return signinIds;
    }

    public void setSigninIds(String signinIds) {
        this.signinIds = signinIds == null ? null : signinIds.trim();
    }
}