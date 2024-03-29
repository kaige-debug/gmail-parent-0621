package fight.it.gmall.cart.client;

import fight.it.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value="service-cart")
public interface CartFeignClient {

    @RequestMapping("api/cart/addCart")
    void addCart(@RequestBody CartInfo cartInfo);

    @RequestMapping("api/cart/cartList/{userId}")
    List<CartInfo> cartList(@PathVariable("userId") String userId);

}
