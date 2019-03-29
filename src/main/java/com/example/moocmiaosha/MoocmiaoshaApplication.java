package com.example.moocmiaosha;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.example.moocmiaosha"})
@MapperScan("com.example.moocmiaosha.dao")
public class MoocmiaoshaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoocmiaoshaApplication.class, args);
    }

}
