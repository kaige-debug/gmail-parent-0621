package fight.it.gmall.all.controller;

import fight.it.gmall.common.util.MD5;
import fight.it.gmall.model.activity.OrderRecode;
import fight.it.gmall.model.activity.SeckillGoods;
import fight.it.gmall.model.order.OrderDetail;
import fight.it.gmall.model.order.OrderInfo;
import fight.it.gmall.model.user.UserAddress;
import fight.it.gmall.seckill.client.SecKillFeignClient;
import fight.it.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SecKillController {
    @Autowired
    SecKillFeignClient secKillFeignClient;
    @Autowired
    UserFeignClient userFeignClient;

    @GetMapping("seckill/queue.html")
    public String secKill(Long skuId, String skuIdStr,Model model, HttpServletRequest request){
        String userId = request.getHeader("userId");
        String skuIdCheckStr = MD5.encrypt(userId);
        // 检查抢购码
        if(null!=skuIdStr && skuIdCheckStr.equals(skuIdStr)){
            model.addAttribute("skuId",skuId);
            model.addAttribute("skuIdStr",skuIdStr);
            return "seckill/queue";
        }else{
            return "seckill/fail";
        }

    }
    @GetMapping("seckill/{skuId}.html")
    public String getItem(@PathVariable("skuId") Long skuId,Model model){
       SeckillGoods seckillGoods =  secKillFeignClient.getSeckillGoods(skuId);
       model.addAttribute("item",seckillGoods);
        return "seckill/item";
    }
    @GetMapping("seckill.html")
    public String  index(Model model){
       List<SeckillGoods> seckillGoodsList =secKillFeignClient.findAll();
       model.addAttribute("list",seckillGoodsList);
       return "seckill/index";
    }
    @GetMapping("seckill/trade.html")
    public String trade(Model model ,HttpServletRequest request){

        String userId = request.getHeader("userId");
        OrderRecode orderRecode = secKillFeignClient.getOrderRecode(userId);
        List<UserAddress> userAddressListByUserId = userFeignClient.findUserAddressByUserId(userId);


        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setDeliveryAddress(userAddressListByUserId.get(0).getUserAddress());
        orderInfo.setConsignee(userAddressListByUserId.get(0).getConsignee());
        orderInfo.setConsigneeTel(userAddressListByUserId.get(0).getPhoneNum());
        // 订单详情
        List<OrderDetail> orderDetails = new ArrayList<>();
        OrderDetail orderDetail = new OrderDetail();
        SeckillGoods seckillGoods = orderRecode.getSeckillGoods();
        orderDetail.setSkuNum(seckillGoods.getNum());
        orderDetail.setOrderPrice(seckillGoods.getPrice());
        orderDetail.setImgUrl(seckillGoods.getSkuDefaultImg());
        orderDetail.setSkuName(seckillGoods.getSkuName());
        orderDetail.setSkuId(seckillGoods.getSkuId());
        orderDetails.add(orderDetail);

        model.addAttribute("detailArrayList",orderDetails);
        model.addAttribute("userAddressList",userAddressListByUserId);
        model.addAttribute("order",orderInfo);
        model.addAttribute("totalAmount",seckillGoods.getPrice());

        return "seckill/trade";
    }
}
