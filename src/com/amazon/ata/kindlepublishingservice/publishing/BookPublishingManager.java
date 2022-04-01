package com.amazon.ata.kindlepublishingservice.publishing;

import java.util.LinkedList;
import java.util.Queue;
import javax.inject.Inject;


public class BookPublishingManager {
    private final Queue<BookPublishRequest> publishRequestQueue = new LinkedList<>();

    /**
     * Instantiates a new Book publishing manager.
     */
    @Inject
    public BookPublishingManager() {
    }

    /**
     * Add request.
     *
     * @param request the request
     */
    public void addRequest(BookPublishRequest request) {
        publishRequestQueue.add(request);
    }

    /**
     * Next request book publish request.
     *
     * @return the book publish request
     */
    BookPublishRequest nextRequest() {
        return publishRequestQueue.remove();
    }

    /**
     * Queue has next request boolean.
     *
     * @return the boolean
     */
    boolean queueHasNextRequest() {
        return !publishRequestQueue.isEmpty();
    }
}




