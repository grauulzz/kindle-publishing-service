package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.amazon.ata.kindlepublishingservice.App.*;


public class BookPublishTask implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(BookPublisher.class);
    private static final PublishingStatusDao PUBLISHING_STATUS_DAO = component.providePublishingStatusDao();
    private static final CatalogDao CATALOG_DAO = component.provideCatalogDao();

    @Inject
    public BookPublishTask() {}

    @Override
    public void run() {
        LOGGER.info("Book publish task");
        while(BookPublishingManager.queueHasNextRequest()) {
            BookPublishRequest request = BookPublishingManager.nextRequest();
            if (request == null) {
                return;
            }
            String publishingRecordId = request.getPublishingRecordId();
            String requestBookId = request.getBookId();

            markInProgress(request, requestBookId);

            if (requestBookId != null) {
                CompletableFuture<CatalogItemVersion> getBookFromCatalog =
                        CompletableFuture.supplyAsync(() -> CATALOG_DAO.getBookFromCatalog(requestBookId));
                try {
                    CatalogItemVersion item = waitForExistingItemHandling(publishingRecordId,
                            publishingRecordId, getBookFromCatalog);
                    publishNewVersion(request, publishingRecordId, item);
                } catch (InterruptedException | ExecutionException e) {
                    markFailed(publishingRecordId, requestBookId);
                    e.printStackTrace();
                }
            }

            String bookId = KindlePublishingUtils.generateBookId();
            publish(request, bookId);

            CompletableFuture<CatalogItemVersion> futureSubmission =
                    CompletableFuture.supplyAsync(() -> CATALOG_DAO.load(bookId));

            waitForNewItemHandling(publishingRecordId, bookId, futureSubmission);
        }
    }

    private void publishNewVersion(BookPublishRequest request, String publishingRecordId,
                                       CatalogItemVersion currentVersion) {
        KindleFormattedBook kindleFormat = KindleFormatConverter.format(request);
        String text = kindleFormat.getText();
        String title = kindleFormat.getTitle();
        String author = kindleFormat.getAuthor();
        BookGenre genre = kindleFormat.getGenre();

        CatalogItemVersion newVersion = new CatalogItemVersion();
        newVersion.setVersion(currentVersion.getVersion() + 1);
        newVersion.setBookId(currentVersion.getBookId());
        newVersion.setText(text);
        newVersion.setTitle(title);
        newVersion.setAuthor(author);
        newVersion.setGenre(genre);
        markSuccessful(publishingRecordId, newVersion);
        CATALOG_DAO.saveItem(newVersion);
    }

    private void publish(BookPublishRequest request, String bookId) {
        KindleFormattedBook kindleFormat = KindleFormatConverter.format(request);
        CatalogItemVersion catalogItemVersion = new CatalogItemVersion();
        catalogItemVersion.setBookId(bookId);
        catalogItemVersion.setTitle(kindleFormat.getTitle());
        catalogItemVersion.setAuthor(kindleFormat.getAuthor());
        catalogItemVersion.setGenre(kindleFormat.getGenre());
        catalogItemVersion.setText(kindleFormat.getText());
        CATALOG_DAO.saveItem(catalogItemVersion);
    }

    private CatalogItemVersion waitForExistingItemHandling(String publishingRecordId,
                                                           String requestBookId,
                                                           CompletableFuture<CatalogItemVersion> future)
            throws ExecutionException, InterruptedException {
        future.whenComplete((catalogItemVersion, throwable) ->  {
            if (catalogItemVersion != null) {
                markSuccessful(publishingRecordId, catalogItemVersion);
                markPreviousVersionInactive(catalogItemVersion.getBookId());
            }
            markFailed(publishingRecordId, requestBookId);
            throw new BookNotFoundException("Something went terribly wrong");
        });
        return future.get();
    }

    private void waitForNewItemHandling(String publishingRecordId, String generatedBookId,
                                        CompletableFuture<CatalogItemVersion> future) {
        future.thenAccept(item -> {
            if (item != null) {
                item.setVersion(1);
                CATALOG_DAO.saveItem(item);
                markSuccessful(publishingRecordId, item);
            }
            markFailed(publishingRecordId, generatedBookId);
            throw new BookNotFoundException("Something went terribly wrong");
        });
    }

    private void markInProgress(BookPublishRequest request, String requestBookId) {
        PublishingStatusItem inProgressItem = PUBLISHING_STATUS_DAO.setPublishingStatus(request.getPublishingRecordId(), PublishingRecordStatus.IN_PROGRESS, requestBookId);
        PUBLISHING_STATUS_DAO.save(inProgressItem);
    }

    private void markSuccessful(String publishingRecordId, CatalogItemVersion item) {
        PublishingStatusItem successfulItem = PUBLISHING_STATUS_DAO.setPublishingStatus(publishingRecordId, PublishingRecordStatus.SUCCESSFUL, item.getBookId());
        PUBLISHING_STATUS_DAO.save(successfulItem);
    }

    private void markFailed(String publishingRecordId, String requestBookId) {
        PublishingStatusItem failedItem = PUBLISHING_STATUS_DAO.setPublishingStatus(publishingRecordId, PublishingRecordStatus.SUCCESSFUL, requestBookId);
        PUBLISHING_STATUS_DAO.save(failedItem);
    }

    private RemoveBookFromCatalogResponse markPreviousVersionInactive(String requestBookId) {
        return component.provideRemoveBookFromCatalogActivity()
                .execute(RemoveBookFromCatalogRequest.builder()
                        .withBookId(requestBookId)
                        .build());
    }

}


//                CompletableFuture<CatalogItemVersion> loadItem =
//                        CompletableFuture.supplyAsync(() -> CATALOG_DAO.load(requestBookId));
//                CatalogItemVersion existingItem = null;
//                try {
//                    existingItem = onCompletionExistingItem(publishingRecordId, requestBookId, loadItem);
//                } catch (ExecutionException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if (existingItem != null) {
//
//
//                CatalogItemVersion item = component.provideCatalogDao().isExsitingCatalogItem(requestBookId)
//                        .orElseThrow(() -> new RuntimeException("Request had a bookId present, but no book was found in catalog"));
//
//                CompletableFuture<GetItemResult> catalogItem = DynamoDBAsync.createCompletableFuture(
//            "CatalogItemVersions", "bookId", item.getBookId(), "status",
//                        item.getVersion());

//            if (request.getBookId() == null) {
//                    KindleFormattedBook kindleFormattedBook = KindleFormattedBook.builder().withBookId(KindlePublishingUtils.generateBookId()).withAuthor(request.getAuthor()).withGenre(request.getGenre()).withText(KindleConversionUtils.convertTextToKindleFormat(request.getText())).withTitle(request.getTitle()).build();
//
//                    }
//
//                    KindleFormattedBook requestWithNonNullId = KindleFormatConverter.format(request);
//public class BookPublishTask implements Callable<CatalogItemVersion> {
//
//    public static final CatalogDao CATALOG_DAO = App.component.provideCatalogDao();
//    public static final PublishingStatusDao PUBLISHING_STATUS_DAO = App.component.providePublishingStatusDao();
//    private final BookPublishRequest request;
//
//    BookPublishTask(BookPublishRequest request) {
//        this.request = request;
//    }
//
//    @Override
//    public CatalogItemVersion call() throws Exception {
//        return CATALOG_DAO.isExsitingCatalogItem(request.getBookId())
//                .orElseThrow(() -> new Exception("Catalog item not found"));
//    }
//
//
//
//
//    static ExecutorService createExecutor() {
//        return new ThreadPoolExecutor(
//                0,
//                Integer.MAX_VALUE,
//                60,
//                TimeUnit.SECONDS,
//                new LinkedBlockingQueue<Runnable>());
//    }
//
//}





//                CompletableFuture<Integer> httpStatus =
//                        CompletableFuture.supplyAsync(() -> markPreviousVersionInactive(requestBookId));
//                    httpStatus.thenAccept(response -> {
//                        if (response == 200) {
//                            publishNewVersion(request, publishingRecordId,
//                                    catalogItemVersion);
//                        }
//                        System.out.println("HTTP status: " + response);
//                    });
//
//                getBookFromCatalog.whenComplete((catalogItemVersion, throwable) -> {
//                    if (throwable != null) {
//                        markFailed(publishingRecordId, requestBookId);
//                        throwable.printStackTrace();
//                        throw new BookNotFoundException("Something went terribly wrong");
//                    }
//
//                });