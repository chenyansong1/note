package cn.sm1234;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.sm1234.dao") // @MapperScan：作用是用于扫描MyBatis的mapper接口的包
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
