package org.example.ltwaicodemother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.ltwaicodemother.mapper")
public class LtwAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(LtwAiCodeMotherApplication.class, args);
    }

}
