package fight.it.gmall.pay.receiver;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import fight.it.gmall.mq.service.RabbitService;
import fight.it.gmall.pay.service.PaymentService;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class PaymentConsumer {
    @Autowired
    PaymentService paymentService;
    @Autowired
    RabbitService rabbitService;
    @SneakyThrows
    @RabbitListener(queues="queue.delay.1")



    public void paymentConsumer(Channel channel, Message message,String mapJson){
      Map<String,Object> map = new HashMap<>();
      Map<String,Object> jsonMap= JSON.parseObject(mapJson, map.getClass());
        Integer count =(Integer)jsonMap.get("count");
        String out_trade_no =(String)jsonMap.get("out_trade_no");

      Map<String, Object> checkMap = paymentService.checkPayment(out_trade_no);
      count--;
        System.out.println("count"+count);
        String trade_status = (String)checkMap.get("trade_status");
        Boolean success = (Boolean) checkMap.get("success");
      if(count>0){
          if(success==false ||trade_status.equals("WAIT_BUYER_PAY") ){
             jsonMap.put("count",count);
             System.out.println("当前状态"+trade_status+"继续询问");
             rabbitService.SendDelayedMessage("exchange.delay","routing.delay",JSON.toJSONString(jsonMap),10l, TimeUnit.SECONDS);
          }else{
              System.out.println("当前支付状态"+trade_status+"不再继续询问");

              String status = "未支付";//paymentService.getPaymentStatus(paymentInfo);
              if(!status.equals("PAID")){
                  // 修改支付状态，并且发送支付成功队列// 更新支付信息，更新订单信息(更新未订单已支付)
               }
          }
      }else{
          System.out.println("查询次数为零，不再查询");
      }
      //确认消费
      channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }


}
