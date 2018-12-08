package club.szuai.signin.controller;

import club.szuai.signin.bean.Class;
import club.szuai.signin.bean.Student;
import club.szuai.signin.bean.enums.ErrorCode;
import club.szuai.signin.dbmapper.StudentMapper;
import club.szuai.signin.service.ClassService;
import club.szuai.signin.utils.LatAndLongitudeUtil;
import club.szuai.signin.utils.OAUtils;
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
     * 默认签到范围为50m
     */
    private static final int DEFUALT_SIGN_IN_DISTANCE = 50;

    @Autowired
    private ClassService classService;

    @Autowired
    private StudentMapper studentMapper;

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
    @RequestMapping(value = "/auth")
    @ResponseBody
    public Map<String, Object> checkAuth(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
        String idStr = request.getParameter("id");
        String pwStr = request.getParameter("pw");
        int card_id;
        try {
            if (StringUtils.isEmpty(idStr) || StringUtils.isEmpty(pwStr)) {
                throw new Exception();
            }
            card_id = Integer.parseInt(idStr);
        } catch (Exception e) {
            logger.error("Parse error,id={},password={}", idStr, pwStr);
            errorCode = ErrorCode.PARAM_ERROR;
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return result;
        }

        Student student = studentMapper.selectByCardId(card_id);
        if (student != null) {
            result.put("name", student.getName());
        } else {
            //数据库中找不到该学生时通过OA来验证身份
            logger.debug("Card_id:{} not found,trying to login OA", card_id);
            //实例化httpclient
            OAUtils oaUtils = new OAUtils();
            if (oaUtils.loginOA(idStr, pwStr)) {
                result.put("name", oaUtils.getName());
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

        List<Class> classes = classService.getClasses(student_id);
        if (classes.isEmpty()) {
            errorCode = ErrorCode.USER_IS_NOT_EXIST;
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
        String stuIdStr = request.getParameter("stu_id");
        String classIdStr = request.getParameter("class_id");
        String latStr = request.getParameter("lat");
        String lngStr = request.getParameter("lng");
        int student_id, class_id;
        BigDecimal lat;
        BigDecimal lng;
        try {
            student_id = Integer.parseInt(stuIdStr);
            class_id = Integer.parseInt(classIdStr);
            lat = new BigDecimal(latStr);
            lng = new BigDecimal(lngStr);
        } catch (Exception e) {
            logger.error("Parse error,student_id={},class_id={},lat={},lng={}", stuIdStr, classIdStr, latStr, lngStr);
            errorCode = ErrorCode.PARAM_ERROR;
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return result;
        }

        Map<String, Object> classLocation = classService.getClassroomLocation(class_id);
        if (classLocation.get("error").toString().equals("1")) {
            errorCode = ErrorCode.SYSTEM_ERROR;
        } else {
            try {
                BigDecimal classLat = (BigDecimal) classLocation.get("lat");
                BigDecimal classLng = (BigDecimal) classLocation.get("lng");
                long distance = (long) LatAndLongitudeUtil.getDistance(lat.doubleValue(), lng.doubleValue(), classLat.doubleValue(), classLng.doubleValue());

                if (distance > DEFUALT_SIGN_IN_DISTANCE) {
                    errorCode = ErrorCode.LOCATION_TOO_FAR;
                }else {
                    classService.updateSignInList(class_id,student_id);
                }
                result.put("distance",distance);
            } catch (Exception e) {
                logger.error("Can't get classroom's location,class_id={}", classIdStr);
                errorCode = ErrorCode.SYSTEM_ERROR;
            }
        }
        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

}
