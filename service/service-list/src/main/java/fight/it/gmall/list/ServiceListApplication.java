package fight.it.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@ComponentScan({"fight.it.gmall"})
@EnableDiscoveryClient
@EnableFeignClients("fight.it.gmall")
public class ServiceListApplication {

    public static void main(String[] args) {

        SpringApplication.run(ServiceListApplication.class, args);
    }

}
