package zyz.free;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@MapperScan("zyz.free.data.mapper")
public class DreamApplication {
    public static void main(String[] args) {
        SpringApplication.run(DreamApplication.class, args);
    }
}
