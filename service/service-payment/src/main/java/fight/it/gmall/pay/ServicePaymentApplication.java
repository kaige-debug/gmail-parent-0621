package fight.it.gmall.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"fight.it.gmall"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages= {"fight.it.gmall"})
public class ServicePaymentApplication {

public static void main(String[] args) {
      SpringApplication.run(ServicePaymentApplication.class, args);
   }

}
