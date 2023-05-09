package fight.it.gmall.user.controller;

import fight.it.gmall.common.result.Result;
import fight.it.gmall.model.user.UserAddress;
import fight.it.gmall.model.user.UserInfo;
import fight.it.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/passport/")

public class UserApiController {
    @Autowired
    UserService userService;
    @RequestMapping("login")
    public Result login(@RequestBody UserInfo userInfo, HttpServletRequest request){
     userInfo= userService.login(userInfo);

     if(null!=userInfo){
         return Result.ok(userInfo);
     }else{
         return Result.fail();
     }

    }
    @RequestMapping("verify/{token}")
    public Map<String,Object> verify(@PathVariable("token") String token){
              Map<String,Object> map = new HashMap<>();
              UserInfo userInfo = userService.verify(token);
              map.put("user",userInfo);
        return map;
    }
    @RequestMapping("findUserAddressByUserId/{userId}")
    public List<UserAddress> findUserAddressByUserId(@PathVariable("userId") String userId){

        List<UserAddress> userAddressList= userService.findUserAddressByUserId(userId);
        return userAddressList;

    }
}
