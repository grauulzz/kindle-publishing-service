package com.amazon.ata.kindlepublishingservice;

import com.amazon.ata.kindlepublishingservice.dagger.ATAKindlePublishingServiceManager;
import com.amazon.ata.kindlepublishingservice.dagger.ApplicationComponent;
import com.amazon.ata.kindlepublishingservice.dagger.DaggerApplicationComponent;
import com.amazon.ata.kindlepublishingservice.models.response.FormatResponse;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import java.util.Arrays;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {
    public static final ApplicationComponent component = DaggerApplicationComponent.create();
    public static Logger logger;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

        ATAKindlePublishingServiceManager publishingManager = component.provideATAKindlePublishingServiceManager();
        try {
            publishingManager.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println(FormatResponse.toJson(ctx.getBeanDefinitionNames()));
        };
    }
}
