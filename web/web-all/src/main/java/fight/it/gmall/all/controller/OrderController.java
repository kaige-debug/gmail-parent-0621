package fight.it.gmall.all.controller;

import fight.it.gmall.model.order.OrderDetail;
import fight.it.gmall.model.user.UserAddress;
import fight.it.gmall.order.client.OrderFeignClient;
import fight.it.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    OrderFeignClient orderFeignClient;
    @Autowired
    UserFeignClient userFeignClient;
    @RequestMapping("trade.html")
    public String trade(HttpServletRequest request, Model model){
        String userId = request.getHeader("userId");
        List<OrderDetail> orderDetailList = orderFeignClient.trade();
        List<UserAddress> userAddressesList = userFeignClient.findUserAddressByUserId(userId);
        System.out.println(userAddressesList);
        model.addAttribute("detailArrayList",orderDetailList);
        model.addAttribute("userAddressList",userAddressesList);
        model.addAttribute("totalAmount",getTotalAmount(orderDetailList));
        //后台生成订单号
        String tradeNo = orderFeignClient.getTrade(userId);
        model.addAttribute("tradeNo",tradeNo);
        return "order/trade";
    }

    private BigDecimal getTotalAmount(List<OrderDetail> orderDetailList) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OrderDetail orderDetail : orderDetailList) {
            BigDecimal orderPrice = orderDetail.getOrderPrice();
            totalAmount = totalAmount.add(orderPrice);
        }
        return totalAmount;
    }




    @RequestMapping("myOrder.html")
    public String myOrder(){
        return "order/myOrder";
    }
}
