package club.szuai.signin.bean.enums;

public enum ErrorCode {

    SUCCESS(0, "成功"),

    // 系统相关错误码1000
    PARAM_ERROR(1001, "参数错误"),
    TIME_OUT(1002, "系统超时"),
    SYSTEM_ERROR(1003, "系统错误，请稍后再试！"),
    UNKNOWN_ERROR(1004, "未知错误，请稍后再试！"),

    // 登录相关
    LOGIN_FAIL(2001, "登录失败,用户名或者密码错误!"),
    LOGIN_PARAM_ERROR(2002, "登录失败,用户名及手机号必填!"),
    LOGINOUT_ERROR(2003, "退出登录失败!"),

    // 手机验证码相关错误码4000
    MOBILE_EXIST_ERROR(4001, "该手机号码已经注册"),
    SEND_MSM_ERROR(4002, "短信发送失败"),
    MSM_CODE_ERROR(4003, "短信验证码不正确"),
    MOBILE_NOT_EXIST_ERROR(4004, "该手机号码不存在"),

    // 订单相关错误 5000
    ORDER_IS_NOT_EXIST(5001, "订单不存在"),
    ORDER_UID_ILLEGAL(5010, "用户身份不合法"),

    // 用户相关错误码6000
    USER_IS_NOT_EXIST(6001, "用户不存在"),
    ADD_USER_ERROR(6002, "添加用户失败,请确认登录信息"),
    RESCUE_IS_NOT_EXIST(6041, "救援信息不存在"),
    AED_IS_NOT_EXIST(6042, "Aed不存在"),
    USER_LOCATION_BEYOUNG_LIMIT(6051, "用户可添加位置数到达上限"),

    // 课程相关错误码7000
    COURSE_OR_SCHEDULE_IS_NOT_EXIST(7001, "课程或课程计划不存在"),

    // 支付相关错误码
    WECHAT_PAY_OPEN_ORDER_ERROR(8001, "微信下单异常,请稍后再试!");

    private int    code;
    private String msg;

    ErrorCode(int code, String msg){
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
