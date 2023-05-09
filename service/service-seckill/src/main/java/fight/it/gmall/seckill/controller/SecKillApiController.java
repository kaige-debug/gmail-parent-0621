package fight.it.gmall.seckill.controller;

import fight.it.gmall.common.result.Result;
import fight.it.gmall.common.result.ResultCodeEnum;
import fight.it.gmall.common.util.MD5;
import fight.it.gmall.model.activity.OrderRecode;
import fight.it.gmall.model.activity.SeckillGoods;
import fight.it.gmall.model.order.OrderInfo;
import fight.it.gmall.order.client.OrderFeignClient;
import fight.it.gmall.seckill.service.SecKillService;
import fight.it.gmall.seckill.util.CacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity/seckill")

public class SecKillApiController {
    @Autowired
    SecKillService secKillService;
    @Autowired
    OrderFeignClient orderFeignClient;

    @RequestMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo orderInfo ,HttpServletRequest request){
        String userId = request.getHeader("userId");
        orderInfo.setUserId(Long.parseLong(userId));

        String orderId = orderFeignClient.submitOrder(orderInfo);

        //删除预订单
        secKillService.deleteOrderRecode(userId);
        //生成已提交点单
        secKillService.getOrderUsers(userId,orderId);
        return Result.ok();
    }
    @RequestMapping("getSeckillGoods/{skuId}")
    public SeckillGoods getSeckillGoods(@PathVariable("skuId") Long skuId){
        SeckillGoods seckillGoods =  secKillService.getSeckillGoods(skuId);

        return seckillGoods;
    }
    @GetMapping("findAll")
    public List<SeckillGoods> findAll(){
      List<SeckillGoods> seckillGoodsList =  secKillService.findAll();
        return seckillGoodsList;
    }
    @RequestMapping("putGoods/{skuId}")
    public void putGoods(@PathVariable("skuId")Long skuId){
        secKillService.putGoods(skuId);
    }

    @RequestMapping("auth/seckillOrder/{skuId}")
    public Result secKillOrder(@PathVariable("skuId") Long skuId,String skuIdStr, HttpServletRequest request){
        String userId = request.getHeader("userId");
        Map<String,Object> map= secKillService.secKillOrder(skuId,userId);
        return Result.ok();
    }
    @RequestMapping("auth/getSeckillSkuIdStr/{skuId}")
    public Result getSeckillSkuIdStr(@PathVariable("skuId") Long skuId,HttpServletRequest request){

        String status=(String) CacheHelper.get(skuId +"");

        if(null!=status && status.equals("1")){
            String userId = request.getHeader("userId");
            String skuIdStr = MD5.encrypt(userId);
            return Result.ok(skuIdStr);
        }else{
            return Result.fail();
        }
    }
    @RequestMapping("/auth/checkOrder/{skuId}")
    public Result checkOrder(@PathVariable("skuId") Long skuId,HttpServletRequest request){
        String userId = request.getHeader("userId");
        String orderId = secKillService.checkTrueOrder(userId);
        //是否已经下单 "seckill:orders:users"
        if(null!=orderId){
            return Result.build(orderId, ResultCodeEnum.SECKILL_ORDER_SUCCESS);
        }
        //是否预订单
        OrderRecode orderRecode= secKillService.checkOrderRecode(userId);
        if(null!= orderRecode){
            return Result.build(orderRecode,ResultCodeEnum.SECKILL_SUCCESS);
        }
        //是否已售罄
        String skuIdFromCacheHelper  =(String) CacheHelper.get(skuId + "");
        if(null==skuIdFromCacheHelper || skuIdFromCacheHelper.equals("0")){
           return Result.build(null,ResultCodeEnum.SECKILL_FINISH);
        }
        //正在排队
        return Result.build(null,ResultCodeEnum.SECKILL_RUN);
    }
    @RequestMapping("getOrderRecode/{userId}")
    public OrderRecode getOrderRecode(@PathVariable("userId") String userId){
        OrderRecode orderRecode= secKillService.getOrderRecode(userId);
        return orderRecode;
    }

}
