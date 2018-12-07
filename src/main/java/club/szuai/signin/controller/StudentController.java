package club.szuai.signin.controller;

import club.szuai.signin.bean.Student;
import club.szuai.signin.bean.enums.ErrorCode;
import club.szuai.signin.dbmapper.ClassMapper;
import club.szuai.signin.dbmapper.StudentMapper;
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
    private ClassMapper classMapper;

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
        List<Student> events = studentMapper.getList(params);

        result.put("student_list", events);
        ErrorCode errorCode = ErrorCode.SUCCESS;
        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

    @RequestMapping(value = "/loginOA")
    @ResponseBody
    public Map<String, Object> checkLoginOA(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
        String id = request.getParameter("id");
        String pw = request.getParameter("pw");
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(pw)) {
            logger.error("Parse error,id={},password={}", id, pw);
            errorCode = ErrorCode.PARAM_ERROR;
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return result;
        }
        //实例化httpclient
        OAUtils oaUtils = new OAUtils();
        if (oaUtils.loginOA(id, pw)) {
            result.put("name", oaUtils.getName());
        } else {
            errorCode = ErrorCode.LOGIN_FAIL;
        }
        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

}
