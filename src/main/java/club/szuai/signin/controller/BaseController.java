package club.szuai.signin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/")
public class BaseController {

    /**
     * 登录页面
     */
    @RequestMapping("/login")
    public String login(@RequestParam(value = "error", defaultValue = "0") Integer error) {
        if (error == 1) {
            return "/login.html#";
        }
        return "/login.html";
    }

    /**
     * 后台页面
     */
    @RequestMapping("/dashboard/{page}")
    public String dashboard(@PathVariable("page") String page) {
            return "/" + page + ".html";
    }

}