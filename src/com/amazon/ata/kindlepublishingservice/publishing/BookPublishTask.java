package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.amazon.ata.kindlepublishingservice.App.component;


/**
 * The type Book publish task.
 */
public class BookPublishTask implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(BookPublisher.class);
    private final CatalogDao catalogDao;
    private final PublishingStatusDao publishingStatusDao;

    /**
     * Instantiates a new Book publish task.
     *
     * @param catalogDao          the catalog dao
     * @param publishingStatusDao the publishing status dao
     */
    @Inject
    public BookPublishTask(CatalogDao catalogDao, PublishingStatusDao publishingStatusDao) {
        this.catalogDao = catalogDao;
        this.publishingStatusDao = publishingStatusDao;
    }

    @Override
    public void run() {
        LOGGER.info("Book publish task");
        while (BookPublishingManager.queueHasNextRequest()) {
            BookPublishRequest request = BookPublishingManager.nextRequest();
            if (request == null) {
                return;
            }
            String publishingRecordId = request.getPublishingRecordId();
            String requestBookId = request.getBookId();

            publishingStatusDao.markInProgress(request, requestBookId);

            if (requestBookId != null) {
                CompletableFuture<CatalogItemVersion> getBookFromCatalog =
                        CompletableFuture.supplyAsync(() -> catalogDao.getBookFromCatalog(requestBookId));
                try {
                    CatalogItemVersion item = waitForExistingItemHandling(publishingRecordId,
                            publishingRecordId, getBookFromCatalog);

                    catalogDao.publishNewVersion(request,  item);
                    publishingStatusDao.markSuccessful(publishingRecordId, item);

                } catch (InterruptedException | ExecutionException e) {
                    publishingStatusDao.markFailed(publishingRecordId, requestBookId);
                    e.printStackTrace();
                }
            }

            String bookId = KindlePublishingUtils.generateBookId();
            catalogDao.publish(request, bookId);

            try {
                CompletableFuture.supplyAsync(() -> catalogDao.load(bookId))
                        .thenCompose(item -> {
                            try {
                                item.get().setVersion(1);
                                catalogDao.saveItem(item.get());
                                publishingStatusDao.markSuccessful(publishingRecordId, item.get());
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            publishingStatusDao.markFailed(publishingRecordId, bookId);
                            throw new BookNotFoundException("Something went terribly wrong");
                        });
            } catch (DynamoDBMappingException | BookNotFoundException e) {
                App.logger.error(e.getMessage());
            }
        }
    }

    private CatalogItemVersion waitForExistingItemHandling(String publishingRecordId,
                                                           String requestBookId,
                                                           CompletableFuture<CatalogItemVersion> future)
            throws ExecutionException, InterruptedException {

        future.whenComplete((catalogItemVersion, throwable) -> {
            if (throwable != null) {
                publishingStatusDao.markFailed(publishingRecordId, requestBookId);
                throw new BookNotFoundException(
                        String.format("Book [%s] not found publishing record [%s] marked failed",
                                requestBookId, publishingRecordId));
            }
            markInactiveAfterRemoveBookResponse(catalogItemVersion,publishingRecordId);
        });
        return future.get();
    }

    private void markInactiveAfterRemoveBookResponse(CatalogItemVersion catalogItemVersion, String publishingRecordId) {
        CompletableFuture.supplyAsync(() -> component.provideRemoveBookFromCatalogActivity()
                        .execute(RemoveBookFromCatalogRequest.builder()
                                .withBookId(catalogItemVersion.getBookId()).build()))
                .thenAccept(removeBookFromCatalogResponse -> {
                    if (removeBookFromCatalogResponse.getStatusCode() == 200) {
                        publishingStatusDao.markSuccessful(publishingRecordId,
                                catalogItemVersion);
                    }
                    publishingStatusDao.markFailed(publishingRecordId,
                            catalogItemVersion.getBookId());
                    throw new BookNotFoundException("Something went terribly wrong");
                });
    }
}






///**
// * The type Book publish task.
// */
//public class BookPublishTask implements Runnable {
//    private static final Logger LOGGER = LogManager.getLogger(BookPublisher.class);
//    private final CatalogDao catalogDao;
//    private final PublishingStatusDao publishingStatusDao;
//
//    /**
//     * Instantiates a new Book publish task.
//     *
//     * @param catalogDao          the catalog dao
//     * @param publishingStatusDao the publishing status dao
//     */
//    @Inject
//    public BookPublishTask(CatalogDao catalogDao, PublishingStatusDao publishingStatusDao) {
//        this.catalogDao = catalogDao;
//        this.publishingStatusDao = publishingStatusDao;
//    }
//
//    @Override
//    public void run() {
//        LOGGER.info("Book publish task");
//        while (BookPublishingManager.queueHasNextRequest()) {
//            BookPublishRequest request = BookPublishingManager.nextRequest();
//            if (request == null) {
//                return;
//            }
//            String publishingRecordId = request.getPublishingRecordId();
//            String requestBookId = request.getBookId();
//
//            publishingStatusDao.markInProgress(request, requestBookId);
//
//            if (requestBookId != null) {
//                CompletableFuture<CatalogItemVersion> getBookFromCatalog =
//                        CompletableFuture.supplyAsync(() -> catalogDao.load(requestBookId))
//                                .thenApplyAsync(c -> {
//                                    markResponse(c, publishingRecordId);
//                                    return c;
//                                });
//                try {
//                    CatalogItemVersion item = waitForExistingItemHandling(publishingRecordId,
//                            publishingRecordId, getBookFromCatalog);
//
//                    publishNewVersion(request, publishingRecordId, item);
//                    publishingStatusDao.markSuccessful(publishingRecordId, item);
//
//                } catch (InterruptedException | ExecutionException e) {
//                    publishingStatusDao.markFailed(publishingRecordId, requestBookId);
//                    e.printStackTrace();
//                }
//            }
//
//            // bookId is null so create a new book publish submission
//            String bookId = KindlePublishingUtils.generateBookId();
//            catalogDao.publish(request, bookId);
//
//            try {
//                CompletableFuture.supplyAsync(() -> catalogDao.load(bookId))
//                        .thenApply(item -> {
//                            if (item != null) {
//                                item.setVersion(1);
//                                catalogDao.saveItem(item);
//                                publishingStatusDao.markSuccessful(publishingRecordId, item);
//                            }
//                            publishingStatusDao.markFailed(publishingRecordId, bookId);
//                            throw new BookNotFoundException("Something went terribly wrong");
//                        });
//            } catch (DynamoDBMappingException | BookNotFoundException e) {
//                App.logger.error(e.getMessage());
//            }
//        }
//    }
//
//    private CatalogItemVersion waitForExistingItemHandling(String publishingRecordId,
//                                                           String requestBookId,
//                                                           CompletableFuture<CatalogItemVersion> future)
//            throws ExecutionException, InterruptedException {
//
//        future.whenComplete((catalogItemVersion, throwable) -> {
//            if (throwable != null) {
//                publishingStatusDao.markFailed(publishingRecordId, requestBookId);
//                throw new BookNotFoundException(
//                        String.format("Book [%s] not found publishing record [%s] marked failed",
//                                requestBookId, publishingRecordId));
//            }
////            if (catalogItemVersion != null) {
//
////                CompletableFuture.supplyAsync(() -> component.provideRemoveBookFromCatalogActivity()
////                        .execute(RemoveBookFromCatalogRequest.builder()
////                                .withBookId(catalogItemVersion.getBookId()).build()))
////                        .thenAccept(removeBookFromCatalogResponse -> {
////                            if (removeBookFromCatalogResponse.getStatusCode() == 200) {
////                                publishingStatusDao.markSuccessful(publishingRecordId,
////                                        catalogItemVersion);
////                            }
////                });
//
////        RemoveBookFromCatalogResponse markInactiveResponse = component.provideRemoveBookFromCatalogActivity()
////                .execute(RemoveBookFromCatalogRequest.builder()
////                        .withBookId(catalogItemVersion.getBookId())
////                        .build());
//
////            }
////            publishingStatusDao.markFailed(publishingRecordId, requestBookId);
////            throw new BookNotFoundException("Something went terribly wrong");
//        });
//        return future.get();
//    }
//
//    private void markResponse(CatalogItemVersion catalogItemVersion, String publishingRecordId) {
//        CompletableFuture.supplyAsync(() -> component.provideRemoveBookFromCatalogActivity()
//                        .execute(RemoveBookFromCatalogRequest.builder()
//                                .withBookId(catalogItemVersion.getBookId()).build()))
//                .thenAccept(removeBookFromCatalogResponse -> {
//                    if (removeBookFromCatalogResponse.getStatusCode() == 200) {
//                        publishingStatusDao.markSuccessful(publishingRecordId,
//                                catalogItemVersion);
//                    }
//                    publishingStatusDao.markFailed(publishingRecordId,
//                            catalogItemVersion.getBookId());
//                    throw new BookNotFoundException("Something went terribly wrong");
//                });
//    }
//
//    private void publishNewVersion(BookPublishRequest request, String publishingRecordId,
//                                   CatalogItemVersion currentVersion) {
//        KindleFormattedBook kindleFormat = KindleFormatConverter.format(request);
//        String text = kindleFormat.getText();
//        String title = kindleFormat.getTitle();
//        String author = kindleFormat.getAuthor();
//        BookGenre genre = kindleFormat.getGenre();
//
//        CatalogItemVersion newVersion = new CatalogItemVersion();
//        newVersion.setVersion(currentVersion.getVersion() + 1);
//        newVersion.setBookId(currentVersion.getBookId());
//        newVersion.setText(text);
//        newVersion.setTitle(title);
//        newVersion.setAuthor(author);
//        newVersion.setGenre(genre);
//        publishingStatusDao.markSuccessful(publishingRecordId, newVersion);
//        catalogDao.saveItem(newVersion);
//    }
//}


//    CompletableFuture<RemoveBookFromCatalogResponse> markInactiveResponse =
//            CompletableFuture.supplyAsync(() -> component.provideRemoveBookFromCatalogActivity()
//                    .execute(RemoveBookFromCatalogRequest.builder()
//                            .withBookId(catalogItemVersion.getBookId())
//                            .build()));
//
//                markInactiveResponse.thenAccept((removeBookFromCatalogResponse) -> {
//                        int httpCode = 0;
//                        try {
//                        httpCode = markInactiveResponse.get().getScanResult().getSdkHttpMetadata().getHttpStatusCode();
//                        } catch (InterruptedException | ExecutionException e) {
//                        e.printStackTrace();
//                        }
//                        if (httpCode != 200) {
//                        throw new RuntimeException("Mark previous version inactive failed with http code: " + httpCode);
//                        }
//                        });
