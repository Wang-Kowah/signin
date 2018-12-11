package club.szuai.signin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/")
public class BaseController {

    @RequestMapping("/login")
    public String login(@RequestParam(value = "error" ,defaultValue = "0") Integer error) {
        if (error == 1){
            return "/login.html#";
        }
        return "/login.html";
    }

}