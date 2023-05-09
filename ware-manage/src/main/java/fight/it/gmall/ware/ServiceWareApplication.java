package fight.it.gmall.ware;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"fight.it.gmall"})
@MapperScan("fight.it.gmall.ware.mapper")
@EnableDiscoveryClient
public class ServiceWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceWareApplication.class, args);
	}

}
