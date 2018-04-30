package cn.sm1234;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan  //@ServletComponentScan:作用让SpringBoot扫描@WebServlet等注解
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
