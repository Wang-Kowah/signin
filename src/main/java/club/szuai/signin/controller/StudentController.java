package club.szuai.signin.controller;

import club.szuai.signin.bean.Class;
import club.szuai.signin.bean.Student;
import club.szuai.signin.bean.enums.ErrorCode;
import club.szuai.signin.dbmapper.StudentMapper;
import club.szuai.signin.service.CommonService;
import club.szuai.signin.utils.DateUtil;
import club.szuai.signin.utils.LatAndLongitudeUtil;
import club.szuai.signin.utils.OAUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/stu")
public class StudentController {
    private final static Logger logger = LoggerFactory.getLogger(StudentController.class);

    /**
     * 默认分页数据为10个
     */
    private static final int DEFUALT_UNIT_COUNT_PAGES = 10;

    /**
     * 默认签到范围为500m
     */
    private static final int DEFUALT_SIGN_IN_DISTANCE = 500;

    @Autowired
    private CommonService commonService;

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 获取学生列表
     */
    @RequestMapping(value = "/getList")
    @ResponseBody
    public Map<String, Object> getStudentList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String limitStr = request.getParameter("limit");
        int limit;
        try {
            limit = Integer.parseInt(limitStr);
        } catch (Exception e) {
            limit = DEFUALT_UNIT_COUNT_PAGES;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("limit", limit);
        List<Student> students = studentMapper.getList(params);

        result.put("student_list", students);
        ErrorCode errorCode = ErrorCode.SUCCESS;
        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

    /**
     * 登录模块
     */
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> checkAuth(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
//        String idStr = request.getParameter("id");                //无需用到卡号
        String stuIdStr = request.getParameter("stu_id");
        String pwStr = request.getParameter("pw");
        int card_id, student_id;
        try {
            if (StringUtils.isEmpty(stuIdStr) || StringUtils.isEmpty(pwStr)) {
                throw new Exception();
            }
//            card_id = Integer.parseInt(idStr);
            student_id = Integer.parseInt(stuIdStr);
        } catch (Exception e) {
            logger.error("Parse error,student_id={},password={}", stuIdStr, pwStr);
            errorCode = ErrorCode.PARAM_ERROR;
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return result;
        }

//        Student student = studentMapper.selectByCardId(card_id);
        Map<String, Object> params = new HashMap<>();
        params.put("studentId", student_id);
        params.put("password", pwStr);
        Student student = studentMapper.selectByStuIdAndPassword(params);
        if (student != null) {
            result.put("name", student.getName());
        } else {
            //数据库中找不到该学生时通过OA来验证身份
            logger.debug("Student_id:{} not found,trying to login OA", student_id);
            //实例化httpclient
            OAUtil oaUtil = new OAUtil();
            if (oaUtil.loginOA(stuIdStr, pwStr)) {
                String name = oaUtil.getName();
                result.put("name", name);
                Student newStudent = new Student();
//                newStudent.setCardId(card_id);
                newStudent.setPassword(pwStr);
                newStudent.setCreateTime((int) (System.currentTimeMillis() / 1000));
                newStudent.setName(name);
                newStudent.setStudentId(student_id);
                newStudent.setClassIds("");
                studentMapper.insert(newStudent);
            } else {
                errorCode = ErrorCode.LOGIN_FAIL;
            }
        }
        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

    /**
     * 获取个人课表
     */
    @RequestMapping(value = "/classes")
    @ResponseBody
    public Map<String, Object> getClasses(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
        String idStr = request.getParameter("id");
        int student_id;
        try {
            student_id = Integer.parseInt(idStr);
        } catch (Exception e) {
            errorCode = ErrorCode.PARAM_ERROR;
            logger.error("Parse error,id={}", idStr);
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return result;
        }

        List<Class> classes = commonService.getClasses(student_id);
        if (classes.isEmpty()) {
            errorCode = ErrorCode.COURSE_IS_NOT_EXIST;
        } else {
            result.put("classes", classes);
        }
        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

    /**
     * 定位签到模块(lat:纬度;lng:经度)
     */
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> signIn(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
//        String stuIdStr = request.getParameter("stu_id");
        String classIdStr = request.getParameter("class_id");
        String latStr = request.getParameter("lat");
        String lngStr = request.getParameter("lng");
        String timeStr = request.getParameter("valid");
        int student_id, class_id;
        long timestamp;
        BigDecimal lat, lng;
        try {
//            student_id = Integer.parseInt(stuIdStr);
            class_id = Integer.parseInt(classIdStr);
            lat = new BigDecimal(latStr);
            lng = new BigDecimal(lngStr);
            timestamp = Long.parseLong(timeStr);
        } catch (Exception e) {
//            logger.error("Parse error,student_id={},class_id={},lat={},lng={},timestamp={}", stuIdStr, classIdStr, latStr, lngStr, timeStr);
            logger.error("Parse error,class_id={},lat={},lng={},timestamp={}", classIdStr, latStr, lngStr, timeStr);
            errorCode = ErrorCode.PARAM_ERROR;
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return result;
        }
        //一分钟内请求否则拒绝
        long now = DateUtil.getMinuteBeginTimestamp(System.currentTimeMillis()) / 1000;
        if (timestamp != now) {
            errorCode = ErrorCode.QRCODE_IS_NOT_VALID;
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return result;
        }

        Map<String, Object> classLocation = commonService.getClassroomLocation(class_id);
        if (classLocation.get("error").toString().equals("1")) {
            errorCode = ErrorCode.SYSTEM_ERROR;
        } else {
            try {
                BigDecimal classLat = (BigDecimal) classLocation.get("lat");
                BigDecimal classLng = (BigDecimal) classLocation.get("lng");
                long distance = (long) LatAndLongitudeUtil.getDistance(lat.doubleValue(), lng.doubleValue(), classLat.doubleValue(), classLng.doubleValue());

                if (distance > DEFUALT_SIGN_IN_DISTANCE) {
                    errorCode = ErrorCode.LOCATION_TOO_FAR;
                    logger.info("Sign in fail,stuId={},distance={}");
                }
                result.put("distance", distance);
            } catch (Exception e) {
                logger.error("Can't get classroom's location,class_id={}", classIdStr);
                errorCode = ErrorCode.SYSTEM_ERROR;
            }
        }
        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

    /**
     * 更新签到状态
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updateStatus(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
        String stuIdStr = request.getParameter("stu_id");
        String classIdStr = request.getParameter("class_id");
        int student_id, class_id;
        try {
            student_id = Integer.parseInt(stuIdStr);
            class_id = Integer.parseInt(classIdStr);
        } catch (Exception e) {
            logger.error("Parse error,student_id={},class_id={}", stuIdStr, classIdStr);
            errorCode = ErrorCode.PARAM_ERROR;
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return result;
        }

        try {
            //本地调试时需要允许跨域访问
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET");
            response.setHeader("'Access-Control-Allow-Headers", "x-requested-with,content-type");

            commonService.updateSignInList(class_id, student_id);
            commonService.updateClassIds(class_id, student_id);
        } catch (Exception e) {
            logger.error("Can't update signInList/classIds,student_id={},class_id={}", stuIdStr, classIdStr);
            errorCode = ErrorCode.SYSTEM_ERROR;
        }
        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

}