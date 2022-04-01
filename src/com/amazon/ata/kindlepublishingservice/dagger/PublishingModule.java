package com.amazon.ata.kindlepublishingservice.dagger;

import com.amazon.ata.kindlepublishingservice.publishing.BookPublishTask;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublisher;

import dagger.Module;
import dagger.Provides;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Singleton;

/**
 * The type Publishing module.
 */
@Module
public class PublishingModule {

    /**
     * Provide book publisher.
     *
     * @param scheduledExecutorService the scheduled executor service
     * @param bookPublishTask          the book publish task
     *
     * @return the book publisher
     */
    @Provides
    @Singleton
    public BookPublisher provideBookPublisher(ScheduledExecutorService scheduledExecutorService,
                                              BookPublishTask bookPublishTask) {
        return new BookPublisher(scheduledExecutorService, bookPublishTask);
    }

    /**
     * Provide book publisher scheduler scheduled executor service.
     *
     * @return the scheduled executor service
     */
    @Provides
    @Singleton
    public ScheduledExecutorService provideBookPublisherScheduler() {
        return Executors.newScheduledThreadPool(1);
    }
}
