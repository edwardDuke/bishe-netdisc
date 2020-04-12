package com.bishe.netdisc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.bishe.netdisc.mapper")
@SpringBootApplication
public class NetDiscApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetDiscApplication.class, args);
    }

}
