package com.amazon.ata.kindlepublishingservice.publishing;

import java.util.LinkedList;
import java.util.Queue;

public class BookPublishingManager {

    public Queue<BookPublishRequest> publishRequestQueue;

    public BookPublishingManager() {
        this.publishRequestQueue = new LinkedList<>();
    }

    public void addRequest(BookPublishRequest request) {
        this.publishRequestQueue.add(request);
    }

    public BookPublishRequest nextRequest() {
        return this.publishRequestQueue.remove();
    }

}
