package club.szuai.signin.controller;

import club.szuai.signin.bean.Class;
import club.szuai.signin.bean.Student;
import club.szuai.signin.bean.Teacher;
import club.szuai.signin.bean.enums.ErrorCode;
import club.szuai.signin.dbmapper.ClassMapper;
import club.szuai.signin.dbmapper.TeacherMapper;
import club.szuai.signin.service.ClassService;
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
@RequestMapping(value = "/tch")
public class TeacherController {
    private final static Logger logger = LoggerFactory.getLogger(TeacherController.class);

    /**
     * 默认分页数据为10个
     */
    private static final int DEFUALT_UNIT_COUNT_PAGES = 10;

    @Autowired
    private ClassService classService;

    @Autowired
    private TeacherMapper teacherMapper;

    @RequestMapping("/admin")
    public String html() {
        return "/admin.html";
    }

    @RequestMapping(value = "/getList")
    @ResponseBody
    public Map<String, Object> getTeacherList(HttpServletRequest request, HttpServletResponse response) {
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
        List<Teacher> teachers = teacherMapper.getList(params);

        result.put("teacher_list", teachers);
        ErrorCode errorCode = ErrorCode.SUCCESS;
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

        List<Class> classes = classService.getTeachingClasses(id);
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
