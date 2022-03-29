package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
                    CatalogItemVersion item = awaitGetBookFuture(publishingRecordId,
                            publishingRecordId, getBookFromCatalog);

                    catalogDao.publishNewVersion(request, item);
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

    private CatalogItemVersion awaitGetBookFuture(
            String publishingRecordId, String requestBookId, CompletableFuture<CatalogItemVersion> future
    ) throws ExecutionException, InterruptedException {
        future.whenComplete((currentVersion, throwable) -> {
            if (throwable != null) {
                publishingStatusDao.markFailed(publishingRecordId, requestBookId);
                throw new BookNotFoundException(
                        String.format("Book [%s] not found publishing record [%s] marked failed",
                                requestBookId, publishingRecordId));
            }
            awaitMarkCurrentVersionInactiveFuture(currentVersion, publishingRecordId);
        });
        return future.get();
    }

    private void awaitMarkCurrentVersionInactiveFuture(CatalogItemVersion current, String publishingRecordId) {
        CompletableFuture.supplyAsync(() -> component.provideRemoveBookFromCatalogActivity().execute(
                RemoveBookFromCatalogRequest.builder().withBookId(current.getBookId()).build())
        ).thenAccept(markInactiveResponse -> {
            if (markInactiveResponse.getStatusCode() != 200) {
                publishingStatusDao.markFailed(publishingRecordId, current.getBookId());
                throw new BookNotFoundException("Something went terribly wrong");
            }
        });
    }
}
