package com.amazon.ata.kindlepublishingservice.publishing;

import java.util.LinkedList;
import java.util.Queue;


public class BookPublishingManager {
    private static final Queue<BookPublishRequest> publishRequestQueue = new LinkedList<>();

    /**
     * Add request.
     *
     * @param request the request
     */
    public static void addRequest(BookPublishRequest request) {
        publishRequestQueue.add(request);
    }

    /**
     * Next request book publish request.
     *
     * @return the book publish request
     */
    static BookPublishRequest nextRequest() {
        return publishRequestQueue.remove();
    }

    /**
     * Queue has next request boolean.
     *
     * @return the boolean
     */
    static boolean queueHasNextRequest() {
        return !publishRequestQueue.isEmpty();
    }
}




