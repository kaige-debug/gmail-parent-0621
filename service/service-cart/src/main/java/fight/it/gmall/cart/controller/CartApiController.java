package fight.it.gmall.cart.controller;

import fight.it.gmall.cart.service.CartService;
import fight.it.gmall.common.result.Result;
import fight.it.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/cart")
public class CartApiController {

    @Autowired
    CartService cartService;
    @RequestMapping("addCart")
    public void addCart(@RequestBody CartInfo cartInfo,HttpServletRequest request){
        String userId = request.getHeader("userId");
        cartInfo.setUserId(userId);
        cartService.addCart(cartInfo);
    }
    @RequestMapping("cartList")
    public Result cartList(HttpServletRequest request){
        String userId = request.getHeader("userId");

        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> list = cartService.cartList(cartInfo);
        return Result.ok(list);
    }

    @RequestMapping("cartList/{userId}")
    public  List<CartInfo> cartList(@PathVariable ("userId") String userId){
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);

        List<CartInfo> list  =cartService.cartList(cartInfo);
       return list;
    }
}
