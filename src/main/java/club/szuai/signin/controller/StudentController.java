package club.szuai.signin.controller;

import club.szuai.signin.bean.Student;
import club.szuai.signin.bean.enums.ErrorCode;
import club.szuai.signin.dbmapper.ClassMapper;
import club.szuai.signin.dbmapper.StudentMapper;
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
        Map<String, Object> result = new HashMap<String, Object>();
        String limitStr = request.getParameter("limit");
        int limit;
        try {
            limit = Integer.parseInt(limitStr);
        } catch (Exception e) {
            limit = DEFUALT_UNIT_COUNT_PAGES;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("limit", limit);
        List<Student> events = studentMapper.getList(params);

        result.put("student_list", events);
        ErrorCode errorCode = ErrorCode.SUCCESS;
        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

}
