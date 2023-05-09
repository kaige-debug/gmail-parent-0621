package fight.it.gmall.seckill.service;

import fight.it.gmall.model.activity.OrderRecode;
import fight.it.gmall.model.activity.SeckillGoods;

import java.util.List;
import java.util.Map;

public interface SecKillService {
    SeckillGoods getSeckillGoods(Long skuId);

    List<SeckillGoods> findAll();

    void putGoods(Long skuId);

    Map<String, Object> secKillOrder(Long  skuId ,String userId);

    String checkTrueOrder(String userId);

    OrderRecode checkOrderRecode(String userId);

    OrderRecode getOrderRecode(String userId);

    void deleteOrderRecode(String userId);

    void getOrderUsers(String userId, String orderId);
}
