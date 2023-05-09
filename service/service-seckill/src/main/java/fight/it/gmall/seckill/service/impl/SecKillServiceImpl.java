package fight.it.gmall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.LongString;
import fight.it.gmall.model.activity.OrderRecode;
import fight.it.gmall.model.activity.SeckillGoods;
import fight.it.gmall.model.user.UserRecode;
import fight.it.gmall.mq.service.RabbitService;
import fight.it.gmall.seckill.mapper.SecKillMapper;
import fight.it.gmall.seckill.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    SecKillMapper secKillMapper;
    @Autowired
    RabbitService rabbitService;

    @Override
    public SeckillGoods getSeckillGoods(Long skuId) {
        SeckillGoods seckillGoods =(SeckillGoods) redisTemplate.opsForHash().get("seckill:goods", skuId + "");
        return seckillGoods;
    }

    @Override
    public List<SeckillGoods> findAll() {

        List<SeckillGoods> list = redisTemplate.opsForHash().values("seckill:goods");
        return list;
    }

    @Override
    public void putGoods(Long skuId) {
        QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id",skuId);
        List<SeckillGoods> seckillGoodsList = secKillMapper.selectList(queryWrapper);
        if(null!=seckillGoodsList){
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                //放入库存,有多少库存就放几次
                 for(int i=1 ; i<=seckillGoods.getStockCount();i++){
                     redisTemplate.opsForList().leftPush("seckill:stock:"+seckillGoods.getSkuId()+"",seckillGoods.getSkuId());
                 }
                 //存入秒杀商品表
                 Map<String, Object> map = new HashMap<>();
                 map.put(seckillGoods.getSkuId()+"",seckillGoods);
                 redisTemplate.opsForHash().putAll("seckill:goods",map);
                // 发布入库消息通知所有秒杀微服务
                redisTemplate.convertAndSend("seckillpush",seckillGoods.getSkuId()+":1");
                System.out.println("发布入库消息通知所有秒杀微服务");
            }
        }

    }

    @Override
    public Map<String, Object> secKillOrder(Long skuId ,String userId) {
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("seckill:user:" + userId, 1, 60, TimeUnit.SECONDS);
        Map<String, Object> map = new HashMap<>();
        if(aBoolean){
           UserRecode userRecode = new UserRecode();
           userRecode.setSkuId(skuId);
           userRecode.setUserId(userId);
           rabbitService.sendMessage("exchange.direct.seckill.user","seckill.user", JSON.toJSONString(userRecode));
           map.put("success",true);
        }else{
            map.put("success",false);
        }
        return map;
    }

    @Override
    public String checkTrueOrder(String userId) {

        String orderId =(String) redisTemplate.boundHashOps("seckill:orders:users").get(userId);
        return orderId;
    }

    @Override
    public OrderRecode checkOrderRecode(String userId) {
        OrderRecode orderRecode = (OrderRecode)redisTemplate.boundHashOps("seckill:orders").get(userId);
        return orderRecode;
    }

    @Override
    public OrderRecode getOrderRecode(String userId) {
        OrderRecode orderRecode = (OrderRecode)redisTemplate.boundHashOps("seckill:orders").get(userId);
        return orderRecode;
    }

    @Override
    public void deleteOrderRecode(String userId) {
        redisTemplate.boundHashOps("seckill:orders").delete(userId);
    }

    @Override
    public void getOrderUsers(String userId, String orderId) {
      redisTemplate.boundHashOps("seckill:orders:users").put(userId,orderId);
    }


}
