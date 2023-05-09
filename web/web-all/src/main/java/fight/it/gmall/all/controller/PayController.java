package fight.it.gmall.all.controller;

import fight.it.gmall.model.order.OrderInfo;
import fight.it.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PayController {
    @Autowired
    OrderFeignClient orderFeignClient;
    @RequestMapping("pay.html")
    public String pay(Long orderId, Model model){
      OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId);
      model.addAttribute("orderInfo",orderInfo);
        return "payment/pay";
    }
    @RequestMapping("paySuccess.html")
    public String paySuccess(){

        return "payment/success";
    }
}
