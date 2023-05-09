package fight.it.gmall.user;

import fight.it.gmall.common.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(value = {"fight.it.gmall"},excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SwaggerConfig.class))
@EnableDiscoveryClient
public class ServiceUserApplication {

public static void main(String[] args) {
      SpringApplication.run(ServiceUserApplication.class, args);
   }

}
