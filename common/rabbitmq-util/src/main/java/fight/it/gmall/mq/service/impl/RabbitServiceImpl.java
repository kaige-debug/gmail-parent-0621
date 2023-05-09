package fight.it.gmall.mq.service.impl;

import fight.it.gmall.mq.service.RabbitService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RabbitServiceImpl implements RabbitService {
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * convertAndSend() 转换并发送
     * @param exchange
     * @param routingKey
     * @param message
     */
    @Override
    public void sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }

    @Override
    public void SendDeadMessage(String exchange_dead, String routing_dead_2, Object message, long ttl, TimeUnit seconds) {
        rabbitTemplate.convertAndSend(exchange_dead,routing_dead_2,message,messageProcessor -> {
           messageProcessor.getMessageProperties().setExpiration(ttl*1000+"");
            byte[] body = messageProcessor.getBody();
            String str = new String(body);
            return messageProcessor;
        });
    }

    @Override
    public void SendDelayedMessage(String exchange_delay, String routing_delay, Object message, long ttl, TimeUnit seconds) {
        rabbitTemplate.convertAndSend(exchange_delay,routing_delay,message,messageProcessor->{
            messageProcessor.getMessageProperties().setDelay(Integer.parseInt(ttl+"")*1000);
            byte[] body = messageProcessor.getBody();
            String str = new String(body);
            System.out.println(str);
            return messageProcessor;
        } );
    }


}
