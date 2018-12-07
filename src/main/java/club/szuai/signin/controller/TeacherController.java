package club.szuai.signin.controller;

import club.szuai.signin.dbmapper.ClassMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/tch")
public class TeacherController {
    private final static Logger logger = LoggerFactory.getLogger(TeacherController.class);

    /**
     * 默认分页数据为10个
     */
    private static final int DEFUALT_UNIT_COUNT_PAGES = 10;

    @Autowired
    private ClassMapper classMapper;

    @RequestMapping("/admin")
    public String html() {
        return "/admin.html";
    }


}
