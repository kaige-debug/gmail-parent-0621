package fight.it.gmall.gateway;

import fight.it.gmall.common.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@ComponentScan(value={"fight.it.gmall"},excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SwaggerConfig.class))
@EnableDiscoveryClient
@EnableFeignClients("fight.it.gmall")
public class ServerGatewayApplication {
public static void main(String[] args) {

        SpringApplication.run(ServerGatewayApplication.class,args);
    }
}
