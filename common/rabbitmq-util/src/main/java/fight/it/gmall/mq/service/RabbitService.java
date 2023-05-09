package fight.it.gmall.mq.service;

import java.util.concurrent.TimeUnit;

public interface RabbitService {
    void sendMessage(String exchange, String routingKey, Object message);


    void SendDeadMessage(String exchange_dead, String routing_dead_2, Object message, long l, TimeUnit seconds);

    void SendDelayedMessage(String exchange_delay, String routing_delay, Object message, long l, TimeUnit seconds);
}
