package com.amazon.ata.kindlepublishingservice.publishing;


import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.publishing.utils.KindlePublishingUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.commons.util.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;

import static com.amazon.ata.kindlepublishingservice.App.component;


/**
 * The type Book publish task.
 */
public class BookPublishTask implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(BookPublisher.class);
    private final CatalogDao catalogDao;
    private final PublishingStatusDao publishingStatusDao;
    private final BookPublishingManager manager;

    /**
     * Instantiates a new Book publish task.
     *
     * @param catalogDao            the catalog dao
     * @param publishingStatusDao   the publishing status dao
     * @param manager               the manager
     */
    @Inject
    public BookPublishTask(CatalogDao catalogDao, PublishingStatusDao publishingStatusDao,
                           BookPublishingManager manager) {
        this.catalogDao = catalogDao;
        this.publishingStatusDao = publishingStatusDao;
        this.manager = manager;
    }

    @Override
    public void run() {
        LOGGER.info("Book publish task");
        while (manager.queueHasNextRequest()) {
            BookPublishRequest request = manager.nextRequest();
            if (request == null) {
                LOGGER.info("Incoming BookPublish Request was null");
                return;
            }
            String publishingRecordId = request.getPublishingRecordId();
            String requestBookId = request.getBookId();

            publishingStatusDao.markInProgress(request, requestBookId);

            if (!StringUtils.isBlank(requestBookId)) {
                CompletableFuture<CatalogItemVersion> getBookFromCatalog = CompletableFuture
                        .supplyAsync(() -> catalogDao.getBookFromCatalog(requestBookId));

                CatalogItemVersion item = awaitGetBookFuture(publishingRecordId,
                        publishingRecordId, getBookFromCatalog);

                if (item != null) {
                    catalogDao.publishNewVersion(request, item);
                    publishingStatusDao.markSuccessful(publishingRecordId, item);
                }
                LOGGER.info("CatalogItem returned null after async getBookFromCatalog call");
            }

            String bookId = KindlePublishingUtils.generateBookId();
            catalogDao.publish(request, bookId);

            try {
                CompletableFuture.supplyAsync(() -> catalogDao.load(bookId))
                        .thenAccept(item -> {
                            try {
                                item.get().setVersion(1);
                                catalogDao.saveItem(item.get());
                                publishingStatusDao.markSuccessful(publishingRecordId, item.get());
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            publishingStatusDao.markFailed(publishingRecordId, bookId);
                        });
            } catch (BookNotFoundException e) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    private CatalogItemVersion awaitGetBookFuture(
            String publishingRecordId, String requestBookId, CompletableFuture<CatalogItemVersion> future
    ) {
        future.whenComplete((currentVersion, throwable) -> {
            if (throwable != null) {
                publishingStatusDao.markFailed(publishingRecordId, requestBookId);
            }
            awaitMarkCurrentVersionInactiveFuture(currentVersion, publishingRecordId);
        });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            LOGGER.info("CatalogItemVersion failed to load from db");
        }
        return null;
    }

    private void awaitMarkCurrentVersionInactiveFuture(CatalogItemVersion current, String publishingRecordId) {
        CompletableFuture.supplyAsync(() -> component.provideRemoveBookFromCatalogActivity()
                        .execute(RemoveBookFromCatalogRequest.builder()
                                .withBookId(current.getBookId())
                                .build()))
                .thenAccept(markInactiveResponse -> {
                    if (markInactiveResponse.getStatusCode() != 200) {
                        publishingStatusDao.markFailed(publishingRecordId,
                                current.getBookId());
                        LOGGER.info(String.format("bookId [%s] failed to mark inactive with http status code [%d] ",
                                        current.getBookId(), markInactiveResponse.getStatusCode()));
                    }
                });
    }
}
