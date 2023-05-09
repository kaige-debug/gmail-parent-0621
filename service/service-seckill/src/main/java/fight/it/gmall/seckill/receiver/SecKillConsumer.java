package fight.it.gmall.seckill.receiver;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import fight.it.gmall.model.activity.OrderRecode;
import fight.it.gmall.model.activity.SeckillGoods;
import fight.it.gmall.model.user.UserRecode;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SecKillConsumer {
    @Autowired
    RedisTemplate redisTemplate;

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = "exchange.direct.seckill.user",autoDelete = "false"),
            value =  @Queue(value = "queue.seckill.user",autoDelete = "false"),
            key ={"seckill.user"}
    ))
    public void seckillConsumer(Channel channel, Message message,String json){
        UserRecode userRecode = JSON.parseObject(json, UserRecode.class);
        System.out.println("抢单");
        Object stockObject = redisTemplate.opsForList().rightPop("seckill:stock:" + userRecode.getSkuId());
        try {
            if(null == stockObject){
                  redisTemplate.convertAndSend("seckillpush",userRecode.getSkuId()+"0");
            }else{
                 //生成预定单
                //String   seckillSkuId =(String)stockObject;
                OrderRecode orderRecode = new OrderRecode();
                orderRecode.setNum(1);
                orderRecode.setUserId(userRecode.getUserId());
                SeckillGoods seckillGoods =(SeckillGoods) redisTemplate.opsForHash().get("seckill:goods", userRecode.getSkuId() + "");
                orderRecode.setSeckillGoods(seckillGoods);

                redisTemplate.boundHashOps("seckill:orders").put(userRecode.getUserId(), orderRecode);
            }
        } catch (Exception e){
            // 消息回滚
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),true,false);
        } finally {
            //消息手动消费确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }

    }
}
