package fight.it.gmall.all.controller;

import fight.it.gmall.cart.client.CartFeignClient;
import fight.it.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CartController {
    @Autowired
    CartFeignClient cartFeignClient;
    @RequestMapping("addCart.html")
    public String cart(Long skuId,Long skuNum ,CartInfo cartInfo ){

        cartFeignClient.addCart(cartInfo);
        return "redirect:/cart/addCart.html?skuNum"+cartInfo.getSkuNum();
    }
    @RequestMapping("cart/cart.html")
    public String cartList(){
        return "cart/index";
    }
}
