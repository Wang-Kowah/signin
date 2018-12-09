package club.szuai.signin.bean.enums;

public enum ErrorCode {

    SUCCESS(0, "成功"),

    // 系统相关错误码1000
    PARAM_ERROR(1001, "参数错误"),
    TIME_OUT(1002, "请求超时"),
    SYSTEM_ERROR(1003, "系统错误，请稍后再试！"),
    UNKNOWN_ERROR(1004, "未知错误，请稍后再试！"),

    // 登录相关
    LOGIN_FAIL(2001, "登录失败,用户名或者密码错误!"),
    LOGINOUT_ERROR(2003, "退出登录失败!"),

    // 签到相关错误 5000
    LOCATION_TOO_FAR(5001, "定位离上课地点过远，签到失败"),

    // 用户相关错误码6000
    USER_IS_NOT_EXIST(6001, "用户不存在"),

    // 课程相关错误码7000
    COURSE_IS_NOT_EXIST(7001, "课程不存在");

    private int code;
    private String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ErrorCode getErrorCodeByCode(int code) {
        for (ErrorCode error : ErrorCode.values()) {
            if (error.getCode() == code) {
                return error;
            }
        }
        return UNKNOWN_ERROR;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
