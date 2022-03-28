package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.App;

import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import java.util.LinkedList;
import java.util.Queue;


public class BookPublishingManager {

    private static final Queue<BookPublishRequest> publishRequestQueue = new LinkedList<>();
    private static final Queue<SubmitBookForPublishingRequest> submitRequestQueue = new LinkedList<>();

//    public BookPublishingManager() {
//        publishRequestQueue = new LinkedList<>();
//    }

    public static void addRequest(BookPublishRequest request) {
        App.logger.info("added a request to queue");
        publishRequestQueue.add(request);
    }

    public static BookPublishRequest nextRequest() {
        App.logger.info("called next request in queue");
        return publishRequestQueue.remove();
    }

    public static BookPublishRequest peekNextRequest() {
        App.logger.info("called peek next request");
        return publishRequestQueue.peek();
    }


    public static boolean queueHasNextRequest() {
        return !publishRequestQueue.isEmpty();
    }

    public static void addRequest(SubmitBookForPublishingRequest request) {
        App.logger.info("added a request to queue");
        submitRequestQueue.add(request);
    }

    public static SubmitBookForPublishingRequest nextSubmitRequest() {
        App.logger.info("called next request in queue");
        return submitRequestQueue.remove();
    }

    public static SubmitBookForPublishingRequest peekNextSubmitRequest() {
        App.logger.info("called peek next request");
        return submitRequestQueue.peek();
    }


    public static boolean queueHasNextSubmitRequest() {
        return !publishRequestQueue.isEmpty();
    }

}

//    @Override
//    public void run() {
//        App.logger.info("...");
//        while (queueHasNextRequest()) {
//            BookPublishRequest request = nextRequest();
//            if (request == null) {
//                return;
//            }
//            App.logger.info("processing request");
//            String requestBookId = request.getBookId();
//            pDao.setPublishingStatus(request.getPublishingRecordId(),
//                    PublishingRecordStatus.IN_PROGRESS, requestBookId);
//
//            KindleFormattedBook kindleBook = KindleFormatConverter.format(request);
//
//            // if the request doesn't have a book id generate a new one with KindlePublishingUtils
//            cDao.saveIfPresentElseGenerateId(kindleBook);
//
//
//
//            Optional<CatalogItemVersion> optionalItem = cDao.isExsitingCatalogItem(requestBookId);
//
//            CatalogItemVersion presentItem = optionalItem.orElseGet(() -> {
//                PublishingStatusItem i = pDao.setPublishingStatus(request.getPublishingRecordId(),
//                        PublishingRecordStatus.FAILED, requestBookId);
//                pDao.save(i);
//                throw new BookNotFoundException("Failure to find book with id: " + requestBookId);
//            });
//
//            pDao.setPublishingStatus(request.getPublishingRecordId(),
//                    PublishingRecordStatus.SUCCESSFUL, presentItem.getBookId());
//
//        }
//    }


