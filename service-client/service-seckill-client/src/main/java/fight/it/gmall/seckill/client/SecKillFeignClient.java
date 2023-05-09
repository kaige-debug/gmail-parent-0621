package fight.it.gmall.seckill.client;

import fight.it.gmall.model.activity.OrderRecode;
import fight.it.gmall.model.activity.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("service-activity")
public interface SecKillFeignClient {
    @GetMapping("/api/activity/seckill/getSeckillGoods/{skuId}")
    SeckillGoods getSeckillGoods(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/activity/seckill/findAll")
    List<SeckillGoods> findAll();

    @RequestMapping("/api/activity/seckill/getOrderRecode/{userId}")
    OrderRecode getOrderRecode(@PathVariable("userId")String userId);
}
