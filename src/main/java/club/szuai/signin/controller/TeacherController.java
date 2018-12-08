package club.szuai.signin.controller;

import club.szuai.signin.bean.Class;
import club.szuai.signin.bean.Teacher;
import club.szuai.signin.bean.enums.ErrorCode;
import club.szuai.signin.dbmapper.TeacherMapper;
import club.szuai.signin.service.ClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    /**
     * 获取教师任课列表
     */
    @RequestMapping(value = "/classes")
    @ResponseBody
    public Map<String, Object> getTeachingClasses(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
        String idStr = request.getParameter("id");
        int teacher_id;
        try {
            teacher_id = Integer.parseInt(idStr);
        } catch (Exception e) {
            logger.error("Parse error,id={}", idStr);
            errorCode = ErrorCode.PARAM_ERROR;
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return result;
        }

        List<Class> classes = classService.getTeachingClasses(teacher_id);
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
     * 获取点名名单
     */
    @RequestMapping(value = "/nameList")
    @ResponseBody
    public Map<String, Object> getNameList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
        String idStr = request.getParameter("id");
        int class_id;
        try {
            class_id = Integer.parseInt(idStr);
        } catch (Exception e) {
            logger.error("Parse error,id={}", idStr);
            errorCode = ErrorCode.PARAM_ERROR;
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return result;
        }

        Map<String, Object> nameMap = classService.getNameList(class_id);
        if (nameMap.get("error").toString().equals("1")) {
            errorCode = ErrorCode.COURSE_IS_NOT_EXIST;
        } else {
            List<String> signInList = classService.getSignInList(class_id);
            result.put("idlist", nameMap.get("idList"));
            result.put("namelist", nameMap.get("nameList"));
            result.put("signinlist",signInList);
        }
        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

    //TODO 不再通过发送请求来更新，直接在判断签到成功时后台更新
//    /**
//     * 更新签到名单
//     */
//    @RequestMapping(value = "/update", method = RequestMethod.POST)
//    @ResponseBody
//    public Map<String, Object> updateSignInList(HttpServletRequest request, HttpServletResponse response) {
//        Map<String, Object> result = new HashMap<>();
//        ErrorCode errorCode = ErrorCode.SUCCESS;
//        String stuIdStr = request.getParameter("stu_id");
//        String classIdStr = request.getParameter("class_id");
//        int student_id, class_id;
//        try {
//            student_id = Integer.parseInt(stuIdStr);
//            class_id = Integer.parseInt(classIdStr);
//        } catch (Exception e) {
//            logger.error("Parse error,student_id={},class_id={}", stuIdStr, classIdStr);
//            errorCode = ErrorCode.PARAM_ERROR;
//            result.put("retcode", errorCode.getCode());
//            result.put("msg", errorCode.getMsg());
//            return result;
//        }
//
//        //TODO 实现签到名单逻辑
//        boolean updated = classService.updateSignInList(class_id, student_id);
//        if (!updated) {
//            logger.error("Update sign_in list failed,class_id={},student_id={}", classIdStr, student_id);
//            errorCode = ErrorCode.SYSTEM_ERROR;
//        }
//
//        result.put("retcode", errorCode.getCode());
//        result.put("msg", errorCode.getMsg());
//        return result;
//    }


}
