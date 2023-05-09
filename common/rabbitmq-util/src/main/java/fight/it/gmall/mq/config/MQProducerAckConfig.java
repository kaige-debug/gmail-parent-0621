package fight.it.gmall.mq.config;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MQProducerAckConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {
    @Autowired
    RabbitTemplate  rabbitTemplate;
    @PostConstruct
    public void init(){
      rabbitTemplate.setReturnCallback(this);
      rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 消息发送成功的回调函数
     *
     * @param correlationData
     * @param ack
     * @param exception
     */
    @Override
    public void confirm(@Nullable CorrelationData correlationData, boolean ack, @Nullable String exception) {
        System.out.println("消息结果发送确认");
    }

    /**
     * 消息消费不成功回执
     * @param message  消息
     * @param callBackFlag
     * @param callBackText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int callBackFlag, String callBackText, String exchange, String routingKey) {
        System.out.println("消息结果投递确认");
    }
}
