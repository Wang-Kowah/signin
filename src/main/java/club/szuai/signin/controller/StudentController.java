package club.szuai.signin.controller;

import club.szuai.signin.bean.Class;
import club.szuai.signin.bean.Student;
import club.szuai.signin.bean.enums.ErrorCode;
import club.szuai.signin.dbmapper.ClassMapper;
import club.szuai.signin.dbmapper.StudentMapper;
import club.szuai.signin.service.ClassService;
import club.szuai.signin.utils.OAUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.Element;
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

    @RequestMapping(value = "/auth")
    @ResponseBody
    public Map<String, Object> checkAuth(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
        String idStr = request.getParameter("id");
        String pwStr = request.getParameter("pw");
        int id;
        try {
            if (StringUtils.isEmpty(idStr) || StringUtils.isEmpty(pwStr)) {
                throw new Exception();
            }
            id=Integer.parseInt(idStr);
        }catch (Exception e){
            logger.error("Parse error,id={},password={}", idStr, pwStr);
            errorCode = ErrorCode.PARAM_ERROR;
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return result;
        }

        Student student = studentMapper.selectByCardId(id);
        if (student != null){
            result.put("name", student.getName());
        }else {
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

    @RequestMapping(value = "/classes")
    @ResponseBody
    public Map<String, Object> getClassList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
        String idStr = request.getParameter("id");
        int id;
        try {
            id=Integer.parseInt(idStr);
        }catch (Exception e){
            logger.error("Parse error,id={}", idStr);
            errorCode = ErrorCode.PARAM_ERROR;
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return result;
        }

        List<Class> classes = classService.getClasses(id);
        if (classes.isEmpty()){
            errorCode = ErrorCode.USER_IS_NOT_EXIST;
        }else {
            result.put("classes", classes);
        }
        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }


}
