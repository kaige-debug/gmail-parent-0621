package fight.it.gmall.user.client;

import fight.it.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@FeignClient("service-user")
public interface UserFeignClient {
   @RequestMapping("/api/user/passport/verify/{token}")
   Map<String,Object> verify(@PathVariable ("token") String token);

   @RequestMapping("/api/user/passport/findUserAddressByUserId/{userId}")
   List<UserAddress> findUserAddressByUserId(@PathVariable("userId") String userId);
}
