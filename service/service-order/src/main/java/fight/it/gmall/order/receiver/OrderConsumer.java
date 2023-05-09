package fight.it.gmall.order.receiver;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import fight.it.gmall.model.order.OrderDetail;
import fight.it.gmall.model.order.OrderInfo;
import fight.it.gmall.model.payment.PaymentInfo;
import fight.it.gmall.mq.service.RabbitService;
import fight.it.gmall.order.service.OrderService;
import fight.it.gmall.ware.bean.WareOrderTask;
import fight.it.gmall.ware.bean.WareOrderTaskDetail;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class OrderConsumer {

    @Autowired
    OrderService orderService;
    @Autowired
    RabbitService rabbitService;
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = "exchange_payment_pay",autoDelete = "false"),
            value= @Queue(value = "queue_payment_pay", autoDelete = "false"),
            key = {"routing_payment_pay"}
    ))
    public void orderConsumer(Channel channel, Message message,String mapJson){
        PaymentInfo paymentInfo = JSON.parseObject(mapJson, PaymentInfo.class);
        Long orderId = orderService.updateOrderPay(paymentInfo);
        if(null!=orderId&&orderId>0){
            // 给库存发通知，锁定商品
            OrderInfo orderInfo = orderService.getOrderInfoById(orderId);
            List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();

            WareOrderTask wareOrderTask = new WareOrderTask();
            wareOrderTask.setDeliveryAddress(orderInfo.getDeliveryAddress());
            wareOrderTask.setPaymentWay(orderInfo.getPaymentWay());
            wareOrderTask.setCreateTime(new Date());
            wareOrderTask.setConsigneeTel(orderInfo.getConsigneeTel());
            wareOrderTask.setConsignee(orderInfo.getConsignee());
            wareOrderTask.setOrderId(orderId+"");
            List<WareOrderTaskDetail> wareOrderTaskDetails = new ArrayList<>();
            for (OrderDetail orderDetail : orderDetailList) {
                WareOrderTaskDetail wareOrderTaskDetail = new WareOrderTaskDetail();

                wareOrderTaskDetail.setSkuName(orderDetail.getSkuName());
                wareOrderTaskDetail.setSkuNum(orderDetail.getSkuNum());
                wareOrderTaskDetail.setSkuId(orderDetail.getSkuId()+"");
                wareOrderTaskDetails.add(wareOrderTaskDetail);
            }
            wareOrderTask.setDetails(wareOrderTaskDetails);
            rabbitService.sendMessage("exchange.direct.ware.stock","ware.stock",JSON.toJSONString(wareOrderTask));
      }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
