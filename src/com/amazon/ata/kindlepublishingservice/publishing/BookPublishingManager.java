package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.activity.GetPublishingStatusActivity;
import com.amazon.ata.kindlepublishingservice.controllers.Controller;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.response.FormatResponse;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import com.amazonaws.util.StringUtils;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class BookPublishingManager implements Runnable {

    private static Queue<BookPublishRequest> publishRequestQueue;
    private static final PublishingStatusDao pDao = App.component.providePublishingStatusDao();
    private static final CatalogDao cDao = App.component.provideCatalogDao();
    private static final GetPublishingStatusActivity pActivity = App.component.provideGetPublishingStatusActivity();
    private static final Controller controller = new Controller();

    @Inject
    public BookPublishingManager() {
        publishRequestQueue = new LinkedList<>();
    }

    public static void addRequest(BookPublishRequest request) {
        App.logger.info("added a request to queue");
        publishRequestQueue.add(request);
    }

    private static BookPublishRequest nextRequest() {
        App.logger.info("called next request in queue");
        return publishRequestQueue.remove();
    }

    private static BookPublishRequest peekNextRequest() {
        App.logger.info("called peek next request");
        return publishRequestQueue.peek();
    }


    private static boolean queueHasNextRequest() {
        return !publishRequestQueue.isEmpty();
    }

    @Override
    public void run() {
        App.logger.info("...");
        while (queueHasNextRequest()) {
            BookPublishRequest request = nextRequest();
            if (request == null) {
                return;
            }
            App.logger.info("processing request");
            String requestBookId = request.getBookId();
            pDao.setPublishingStatus(request.getPublishingRecordId(),
                    PublishingRecordStatus.IN_PROGRESS, requestBookId);

            KindleFormattedBook kindleBook = KindleFormatConverter.format(request);

            // if the request doesn't have a book id generate a new one with KindlePublishingUtils
             cDao.saveIfPresentElseGenerateId(kindleBook);



            Optional<CatalogItemVersion> optionalItem = cDao.isExsitingCatalogItem(requestBookId);

            CatalogItemVersion presentItem = optionalItem.orElseGet(() -> {
                PublishingStatusItem i = pDao.setPublishingStatus(request.getPublishingRecordId(),
                        PublishingRecordStatus.FAILED, requestBookId);
                pDao.save(i);
                throw new BookNotFoundException("Failure to find book with id: " + requestBookId);
            });

            pDao.setPublishingStatus(request.getPublishingRecordId(),
                    PublishingRecordStatus.SUCCESSFUL, presentItem.getBookId());

        }
    }



    private static void publishToCatalogTable(CatalogItemVersion item) {
        cDao.saveItem(item);
    }

    public static Consumer<? super CatalogItemVersion> saveConsumer(PublishingStatusItem item) {
        App.component.provideDynamoDBMapper().save(item);
        return null;
    }

}



//    CatalogItemVersion item = optionalItem.orElseGet(() -> {
//        CatalogItemVersion generatedBookIdItem = new CatalogItemVersion();
//        generatedBookIdItem.setBookId(KindlePublishingUtils.generateBookId());
//        return generatedBookIdItem;
//    });




//    @Override
//    public void run() {
//        App.logger.info("...");
//        while (queueHasNextRequest()) {
//            BookPublishRequest request = nextRequest();
//            BookPublishTask task =  new BookPublishTask(request);
//
//            List<PublishingStatusItem> queued = pDao.statusItems(PublishingRecordStatus.QUEUED);
//
//            List<PublishingStatusItem> inProgress = queued.stream()
//                    .peek(item -> item.setStatus(PublishingRecordStatus.IN_PROGRESS))
//                    .collect(Collectors.toList());
//
//            inProgress.forEach(pDao::save);
//
//            ExecutorService executorService = BookPublishTask.createExecutor();
//            Future<CatalogItemVersion> result = executorService.submit(task);
//
//            try {
//                System.out.println(FormatResponse.toJsonWithColor(result.get()));
//                String bookId = result.get().getBookId();
//            } catch (InterruptedException | ExecutionException e) {
//                System.out.println("Error occurred while executing the submitted task");
//                e.printStackTrace();
//            } finally {
//                List<PublishingStatusItem> successful = inProgress.stream()
//                        .peek(item -> item.setStatus(PublishingRecordStatus.SUCCESSFUL))
//                        .collect(Collectors.toList());
//
//                successful.forEach(pDao::save);
//                executorService.shutdown();
//            }
//        }
//    }


//    @Override
//    public void run() {
//        App.logger.info("...");
//        while (queueHasNextRequest()) {
//            BookPublishRequest request = nextRequest();
//            BookPublishTask task =  new BookPublishTask(request);
//
//            List<PublishingStatusItem> queued = pDao.statusItems(PublishingRecordStatus.QUEUED);
//
//            List<PublishingStatusItem> inProgress = queued.stream()
//                    .peek(item -> item.setStatus(PublishingRecordStatus.IN_PROGRESS))
//                    .collect(Collectors.toList());
//
//            inProgress.forEach(pDao::save);
//
//            ExecutorService executorService = BookPublishTask.createExecutor();
//            Future<CatalogItemVersion> result = executorService.submit(task);
//
//
//            try {
//                result.get();
//            } catch (InterruptedException | ExecutionException e) {
//                System.out.println("Error occurred while executing the submitted task");
//                e.printStackTrace();
//            } finally {
//
//                List<PublishingStatusItem> successful = inProgress.stream().parallel()
//                        .peek(item -> item.setStatus(PublishingRecordStatus.SUCCESSFUL))
//                        .peek(item -> {
//                            try {
//                                item.setBookId(result.get().getBookId());
//                            } catch (InterruptedException | ExecutionException e) {
//                                e.printStackTrace();
//                            }
//                        })
//                        .collect(Collectors.toList());
//
//                successful.forEach(pDao::save);
//                executorService.shutdown();
//            }
//        }
//    }





















//            try {
//                    App.logger.info("starting task");
//                    CatalogItemVersion item = result.get();
//                    publishToCatalogTable(item);
//                CatalogItemVersion publishItem = task.call();
//                if (publishItem != null) {
//                    publishToCatalogTable(publishItem);
//                }
//                publishToCatalog();
//                    } catch (Exception e) {
//                    App.logger.error("error in publishing", e);
//                    }


//    @Override
//    public void run() {
//        App.logger.info("...");
//        if (queueIsNotEmpty()) {
//            BookPublishRequest request = nextRequest();
//            App.logger.info("\n processing request \n" + FormatResponse.toJsonWithColor(request));
//            List<PublishingStatusItem> queued = pDao.statusItems(PublishingRecordStatus.QUEUED);
//            List<PublishingStatusItem> inProgress = queued.stream()
//                    .peek(item -> item.setStatus(PublishingRecordStatus.IN_PROGRESS))
//                    .collect(Collectors.toList());
//            inProgress.forEach(pDao::save);
//
//            publishToCatalog(request);
//
//            Optional<CatalogItemVersion> c = cDao.isExsitingCatalogItem(request.getBookId());
//            if (c.isPresent()) {
//                CatalogItemVersion cItem = c.get();
//                App.logger.info("\n catalog item found: \n " + FormatResponse.toJsonWithColor(cItem));
//                List<PublishingStatusItem> successful = inProgress.stream()
//                        .peek(item -> item.setStatus(PublishingRecordStatus.SUCCESSFUL))
//                        .collect(Collectors.toList());
//                successful.forEach(pDao::save);
//            }
//            KindleFormatConverter.format(request);
//        }
//    }



//    private static void saveToPublishingStatusTable(BookPublishRequest request) {
//        PublishingStatusItem item = new PublishingStatusItem();
//        item.setStatus(PublishingRecordStatus.SUCCESSFUL);
//        item.setBookId(request.getBookId());
//        pDao.save(item);
//    }

//    GetPublishingStatusResponse response = pActivity.execute(new GetPublishingStatusRequest(id));
//    List<PublishingStatusRecord> statuses = response.getPublishingStatusHistory();

//    Optional<PublishingStatusItem> item = pDao.queryItemsByPublishingRecordId(id);
//
//            if (item.isPresent()) {
//        PublishingStatusItem newItem = item.get();
//        newItem.setStatus(PublishingRecordStatus.IN_PROGRESS);
//        pDao.save(newItem);
//
//    }



//            if (StringUtils.isNullOrEmpty(bookId)) {
//                    PublishingStatusItem i = pDao.setPublishingStatus(id,  PublishingRecordStatus.IN_PROGRESS, bookId);
//                    pDao.save(i);
//                    }
//
//                    PublishingStatusItem i = pDao.setPublishingStatus(id,  PublishingRecordStatus.SUCCESSFUL, bookId);
//                    pDao.save(i);







//                newItem.setStatusMessage(KindlePublishingUtils.generatePublishingStatusMessage(PublishingRecordStatus.IN_PROGRESS));

//            if (bookId != null) {
//                Optional<PublishingStatusItem> item = pDao.queryItemsByBookId(bookId);
//                if (item.isPresent()) {
//                    this.publishToCatalog(this.request);
////                    this.saveToPublishingStatusTable(this.request);
//                    PublishingStatusItem newitem = item.get();
//                    newitem.setStatusMessage(PublishingStatus.SUCCESSFUL);
//                    newitem.setStatusMessage(KindlePublishingUtils.generatePublishingStatusMessage(PublishingRecordStatus.SUCCESSFUL));
//                    pDao.save(newitem);
//                }
//            }


//    @Override
//    public void run() {
//        App.logger.info("...");
//        if (queueIsNotEmpty()) {
//            BookPublishRequest request = nextRequest();
//            App.logger.info(" \n processing request \n " + FormatResponse.toJsonWithColor(request));
//            GetPublishingStatusResponse response = pActivity.execute(GetPublishingStatusRequest
//                    .builder().withPublishingRecordId(request.getPublishingRecordId())
//                    .build());
//
//            App.logger.info(" \n response: \n " + FormatResponse.toJsonWithColor(response));
//            List<PublishingStatusRecord> history = response.getPublishingStatusHistory();
//
//            if (request.getBookId() != null) {
//                history.add(PublishingStatusRecord.builder()
//                        .withStatus(PublishingStatus.IN_PROGRESS)
//                        .withBookId(request.getBookId()).build());
//
//            }
//        }
//    }


//    PublishingStatusItem markInProgress(BookPublishRequest request) {
//        String id = request.getPublishingRecordId();
//        String bookId = request.getBookId();
//        PublishingStatusItem pItem = pDao.queryItemsById(id);
//        if (pItem != null) {
//            // create a new item with that status
//            pItem.setStatus(PublishingRecordStatus.IN_PROGRESS);
//            pItem.setStatusMessage(KindlePublishingUtils.generatePublishingStatusMessage(
//                    PublishingRecordStatus.IN_PROGRESS));
//
//            pDao.save(pItem);
//        }
//        // processing logic, updates and saves bookRequest with in progress status
//        PublishingStatusItem p = pDao.setPublishingStatus(request.getPublishingRecordId(),
//                PublishingRecordStatus.IN_PROGRESS,
//                request.getBookId(),
//                KindlePublishingUtils.generatePublishingStatusMessage(
//                        PublishingRecordStatus.IN_PROGRESS));
//        App.component.providePublishingStatusDao().save(p);
//        return p;
//    }

