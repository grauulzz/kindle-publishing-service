package com.amazon.ata.kindlepublishingservice.dagger;

import com.amazon.ata.kindlepublishingservice.publishing.BookPublisher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * The ATAKindlePublishingServiceManager controls initializing, starting, verifying, and stopping the
 * ATAKindlePublishingService.
 */
@Singleton
public class ATAKindlePublishingServiceManager {

    private static final Logger log = LogManager.getLogger(ATAKindlePublishingServiceManager.class);

    private final BookPublisher bookPublisher;

    /**
     * Construct a ATAKindlePublishingServiceManager.
     *
     * @param bookPublisher handles asynchronous publishing of books
     */
    @Inject
    ATAKindlePublishingServiceManager(BookPublisher bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    /**
     * Initialize.
     */
    public void initialize() {
    }

    /**
     * Verify.
     *
     * @throws Exception the exception
     */
    public void verify() throws Exception {
    }

    /**
     * Start.
     *
     * @throws Exception the exception
     */
    public void start() throws Exception {
        bookPublisher.start();
    }

    /**
     * Stop.
     *
     * @throws Exception the exception
     */
    public void stop() throws Exception {
        bookPublisher.stop();
    }
}
