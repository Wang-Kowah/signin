package club.szuai.signin.controller;

import club.szuai.signin.bean.Class;
import club.szuai.signin.bean.SignIn;
import club.szuai.signin.bean.Teacher;
import club.szuai.signin.bean.enums.ErrorCode;
import club.szuai.signin.config.SystemParams;
import club.szuai.signin.dbmapper.SignInMapper;
import club.szuai.signin.dbmapper.TeacherMapper;
import club.szuai.signin.service.CommonService;
import club.szuai.signin.utils.DateUtil;
import club.szuai.signin.utils.OAUtil;
import club.szuai.signin.utils.QRCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.HttpCookie;
import java.util.*;

@Controller
@RequestMapping(value = "/tch")
public class TeacherController {
    private final static Logger logger = LoggerFactory.getLogger(TeacherController.class);

    /**
     * 默认分页数据为10个
     */
    private static final int DEFUALT_UNIT_COUNT_PAGES = 10;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SignInMapper signInMapper;

    @Autowired
    private SystemParams systemParams;

    @Autowired
    private TeacherMapper teacherMapper;

    /**
     * 管理页面
     */
    @RequestMapping("/Admin")
    public String admin(HttpSession session) {
        if (session.getAttribute("name") != null || session.getAttribute("id") != null) {
            return "/admin.html";
        } else {
            return "redirect:/login.html";
        }
    }

    /**
     * 后台登录
     */
    @RequestMapping(value = "/adminLogin", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView adminLogin(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
        String idStr = request.getParameter("username");
        String pwStr = request.getParameter("password");
        String hiddenInput = request.getParameter("authuser");
        int teacher_id;
        try {
            if (StringUtils.isEmpty(idStr) || StringUtils.isEmpty(pwStr) || !hiddenInput.equals("teacher")) {
                throw new Exception();
            }
            teacher_id = Integer.parseInt(idStr);
        } catch (Exception e) {
            logger.error("Parse error,id={},password={}", idStr, pwStr);
            errorCode = ErrorCode.PARAM_ERROR;
            result.put("retcode", errorCode.getCode());
            result.put("msg", errorCode.getMsg());
            return new ModelAndView("redirect:/login.html#");
        }

        String name;
        Teacher teacher = teacherMapper.selectByPrimaryKey(teacher_id);
        if (teacher != null) {
            if (teacher.getPassword().equals(pwStr)) {
                name = teacher.getName();
            } else {
                return new ModelAndView("redirect:/login.html#");
            }
        } else {
            //数据库中找不到该老师时通过OA来验证身份
            logger.info("Card_id:{} not found,trying to login OA", teacher_id);
            //实例化httpclient
            OAUtil oaUtil = new OAUtil();
            if (oaUtil.loginOA(idStr, pwStr)) {
                name = oaUtil.getName();
                Teacher newTeacher = new Teacher();
                newTeacher.setTeacherId(teacher_id);
                newTeacher.setPassword(pwStr);
                newTeacher.setCreateTime((int) (System.currentTimeMillis() / 1000));
                newTeacher.setName(name);
                newTeacher.setClassIds("");
                teacherMapper.insert(newTeacher);
            } else {
                return new ModelAndView("redirect:/login.html#");
            }
        }
        if (errorCode.equals(ErrorCode.SUCCESS)) {
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(10 * 60);
            session.setAttribute("name", name);
            session.setAttribute("id", teacher_id);
        }
        return new ModelAndView("redirect:/tch/Admin");
    }

    /**
     * 获取教师列表
     */
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

        List<Class> classes = commonService.getTeachingClasses(teacher_id);
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
     * 获取点名名单
     */
    @RequestMapping(value = "/nameList")
    @ResponseBody
    public Map<String, Object> getNameList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
        String idStr = request.getParameter("class_id");
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

        Map<String, Object> nameMap = commonService.getNameList(class_id);
        if (nameMap.get("error").toString().equals("1")) {
            errorCode = ErrorCode.COURSE_IS_NOT_EXIST;
        } else {
            List<String> signInList = commonService.getSignInList(class_id);
            result.put("idlist", nameMap.get("idList"));
            result.put("namelist", nameMap.get("nameList"));
            result.put("signinlist", signInList);
        }
        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

    /**
     * 获取点名历史记录
     */
    @RequestMapping(value = "/history")
    @ResponseBody
    public Map<String, Object> getSignInHistory(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
        String idStr = request.getParameter("class_id");
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

        Map<String, Object> params = new HashMap<>();
        params.put("classId", class_id);
        List<SignIn> signInHistory = signInMapper.selectByClassId(params);
        result.put("signinlist", signInHistory);

        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

    /**
     * 获取签到二维码
     */
    @RequestMapping(value = "/QRCode")//,method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getQRCode(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        ErrorCode errorCode = ErrorCode.SUCCESS;
        String idStr = request.getParameter("class_id");
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

        try {
            //本地调试时需要允许跨域访问
//            response.setHeader("Access-Control-Allow-Origin", "*");
//            response.setHeader("Access-Control-Allow-Methods", "GET");
//            response.setHeader("'Access-Control-Allow-Headers", "x-requested-with,content-type");

            ServletOutputStream stream = response.getOutputStream();

            long timestamp = DateUtil.getMinuteBeginTimestamp(System.currentTimeMillis()) / 1000;
            String url = class_id + "&" + timestamp;
//            System.out.println(url);

            QRCodeUtil.generateToStream(600, 600, url, "png", stream, systemParams.getImageUri());

            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            errorCode = ErrorCode.SYSTEM_ERROR;
        }

        result.put("retcode", errorCode.getCode());
        result.put("msg", errorCode.getMsg());
        return result;
    }

}