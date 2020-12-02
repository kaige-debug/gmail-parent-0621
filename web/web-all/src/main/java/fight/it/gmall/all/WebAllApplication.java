package fight.it.gmall.all;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@ComponentScan({"fight.it.gmall"})
@EnableFeignClients("fight.it.gmall")
public class WebAllApplication {

    public static void main(String[] args) {

        SpringApplication.run(WebAllApplication.class, args);
    }

}
