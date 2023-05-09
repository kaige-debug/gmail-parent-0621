package fight.it.gmall.order.controller;

import fight.it.gmall.cart.client.CartFeignClient;
import fight.it.gmall.common.result.Result;
import fight.it.gmall.model.cart.CartInfo;
import fight.it.gmall.model.order.OrderDetail;
import fight.it.gmall.model.order.OrderInfo;
import fight.it.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("api/order")
@RestController
public class OrderApiController {
    @Autowired
    CartFeignClient cartFeignClient;
    @Autowired
    OrderService orderService;
    @RequestMapping("trade")
     public List<OrderDetail> trade(HttpServletRequest request){
        String userId = request.getHeader("userId");
        List<CartInfo> cartInfoList = cartFeignClient.cartList(userId);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartInfo cartInfo : cartInfoList) {

            if(cartInfo.getIsChecked().equals(1)){
                OrderDetail orderDetail = new OrderDetail();

                orderDetail.setSkuNum(cartInfo.getSkuNum());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setOrderPrice(cartInfo.getCartPrice());
                orderDetails.add(orderDetail);
            }

        }
        return orderDetails;
    }

    @RequestMapping("getTrade/{userId}")
    public String getTrade(@PathVariable("userId") String userId){

        String tradeNo = orderService.getTrade(userId);
        return tradeNo;
    }

    @RequestMapping("submitOrder")
    String submitOrder(@RequestBody OrderInfo order){
        return orderService.submitOrder(order);
    }

    @RequestMapping("/auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo order ,HttpServletRequest request,String tradeNo){
        String userId = request.getHeader("userId");
        //验证订单是否为当前订单
        Boolean b = orderService.checkout(userId,tradeNo);
        if(b){
            order.setUserId(Long.parseLong(userId));
            String orderId =  orderService.submitOrder(order);
            return Result.ok(orderId);
        }else{
            return Result.fail();
        }

    }

    @RequestMapping("getOrderInfoById/{orderId}")
    public OrderInfo getOrderInfoById(@PathVariable("orderId") Long orderId){
       OrderInfo orderInfo= orderService.getOrderInfoById(orderId);
        return orderInfo;
    }
}
