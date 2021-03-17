package com.onlyonegames.eternalfantasia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.util.TimeZone;


@EnableJpaAuditing
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
public class EternalfantasiaApplication {
    @PostConstruct
    public void init(){
        // Setting Spring Boot SetTimeZone
        //TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
    public static boolean IS_DIRECT_WRIGHDB = true;
    public static void main(String[] args) {
        SpringApplication.run(EternalfantasiaApplication.class, args);
    }
}
