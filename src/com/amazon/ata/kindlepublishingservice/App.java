package com.amazon.ata.kindlepublishingservice;

import com.amazon.ata.kindlepublishingservice.dagger.ATAKindlePublishingServiceManager;
import com.amazon.ata.kindlepublishingservice.dagger.ApplicationComponent;
import com.amazon.ata.kindlepublishingservice.dagger.DaggerApplicationComponent;

import java.net.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


/**
 * The type App.
 */
@SpringBootApplication
public class App {
    /**
     * The constant component.
     */
    public static final ApplicationComponent component = DaggerApplicationComponent.create();
    /**
     * The constant logger.
     */
    public static final Logger logger = LoggerFactory.getLogger(App.class);


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

        ATAKindlePublishingServiceManager publishingManager = component.provideATAKindlePublishingServiceManager();
        try {
            publishingManager.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}


