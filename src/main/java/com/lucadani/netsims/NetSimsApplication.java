package com.lucadani.netsims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NetSimsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetSimsApplication.class, args);
    }

}
