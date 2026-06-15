package com.example.sanctions;

import com.example.sanctions.config.MatchingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties( MatchingProperties.class )
public class SanctionsApplication {

    public static void main( String[] args ) {
        SpringApplication.run( SanctionsApplication.class, args );
    }
}
