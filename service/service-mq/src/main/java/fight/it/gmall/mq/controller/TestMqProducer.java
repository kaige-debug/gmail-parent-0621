package fight.it.gmall.mq.controller;

import fight.it.gmall.common.result.Result;

import fight.it.gmall.mq.config.DeadLetterMqConfig;
import fight.it.gmall.mq.config.DelayedMqConfig;
import fight.it.gmall.mq.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@RestController
public class TestMqProducer {
    @Autowired
    RabbitService rabbitService;

    /**
     * 三个参数，交换机，路由 ，消息、
     * @param message
     * @return
     */
    @RequestMapping("api/mq/testSendMessage/{message}")
    public Result testSeedMessage(@PathVariable("message") String message){
        rabbitService.sendMessage("confirm.exchange","confirm.routing",message);
        return Result.ok();
    }
    @RequestMapping("api/mq/testSendDeadMessage/{message}")
    public Result SendDeadMessage(@PathVariable("message") String message){
        rabbitService.SendDeadMessage(DeadLetterMqConfig.exchange_dead,DeadLetterMqConfig.routing_dead_2,message,10l, TimeUnit.SECONDS);
        return Result.ok();
    }
    @RequestMapping("api/mq/testSendDelayedMessage/{message}")
    public Result testSendDelayedMessage(@PathVariable("message") String message){
        rabbitService.SendDelayedMessage(DelayedMqConfig.exchange_delay,DelayedMqConfig.routing_delay,message,10l,TimeUnit.SECONDS);
       return Result.ok();
    }
}
