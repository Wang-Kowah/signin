package club.szuai.signin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("club.szuai.signin.dbmapper")
public class SignInApplication {

    public static void main(String[] args) {
        SpringApplication.run(SignInApplication.class, args);
    }
}
