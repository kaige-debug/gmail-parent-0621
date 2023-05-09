package fight.it.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PassportController {

    @RequestMapping("login.html")
    public String index(String originUrl, Model model){
        if(StringUtils.isEmpty(originUrl)){
            originUrl = "http://www.gmall.com";

        }
        model.addAttribute("originUrl",originUrl);
        return "login";
    }
//    @RequestMapping("register.html")
//    public String register(){
//        return "register";
//    }
}
