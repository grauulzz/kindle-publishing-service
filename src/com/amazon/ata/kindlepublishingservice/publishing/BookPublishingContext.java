package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.App;
import java.util.LinkedList;
import java.util.Queue;
import javax.inject.Inject;

public class BookPublishingContext {

    private final Queue<BookPublishRequest> publishRequestQueue;

    @Inject
    public BookPublishingContext() {
        this.publishRequestQueue = new LinkedList<>();
    }

    public void addRequest(BookPublishRequest request) {
        App.logger.info("added a request to queue");
        this.publishRequestQueue.add(request);
    }

    BookPublishRequest nextRequest() {
        App.logger.info("called next request in queue");
        return this.publishRequestQueue.remove();
    }

    BookPublishRequest getNextRequestInQueue() {
        App.logger.info("called getNextRequestInQueue");
        return this.publishRequestQueue.peek();
    }


    boolean hasNextRequest() {
        return !this.publishRequestQueue.isEmpty();
    }
}

//    private final PublishingStatusDao dao = App.component.providePublishingStatusDao();
//    PublishingStatusItem markInProgress(BookPublishRequest request) {
//        // processing logic, updates and saves bookRequest with in progress status
//        PublishingStatusItem p = dao.setPublishingStatus(request.getPublishingRecordId(),
//                PublishingRecordStatus.IN_PROGRESS,
//                request.getBookId(),
//                KindlePublishingUtils.generatePublishingStatusMessage(
//                        PublishingRecordStatus.IN_PROGRESS));
//        App.component.providePublishingStatusDao().save(p);
//        return p;
//    }