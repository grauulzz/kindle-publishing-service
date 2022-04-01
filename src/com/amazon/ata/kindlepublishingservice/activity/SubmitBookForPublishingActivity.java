package com.amazon.ata.kindlepublishingservice.activity;


import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.converters.BookPublishRequestConverter;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequest;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishingManager;

import org.junit.platform.commons.util.StringUtils;

import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;


/**
 * The type Submit book for publishing activity.
 */
public class SubmitBookForPublishingActivity {

    private final PublishingStatusDao publishingStatusDao;
    private final CatalogDao catalogDao;
    private final BookPublishingManager manager;

    /**
     * Instantiates a new Submit book for publishing activity.
     *
     * @param catalogDao          the catalog dao
     * @param publishingStatusDao the publishing status dao
     * @param manager             the manager
     */
    @Inject
    public SubmitBookForPublishingActivity(CatalogDao catalogDao, PublishingStatusDao publishingStatusDao,
                                           BookPublishingManager manager) {
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
        this.manager = manager;
    }

    /**
     * Submits the book in the request for publishing.
     *
     * @param request Request object containing the book data to be published. If the request is updating an existing
     * book, then the corresponding book id should be provided. Otherwise, the request will be treated as a new book.
     * @return SubmitBookForPublishingResponse Response object that includes the publishing status id,
     * which can be used to check the publishing state of the book.
     */
    public SubmitBookForPublishingResponse execute(SubmitBookForPublishingRequest request)
            throws BookNotFoundException {

        final BookPublishRequest bookPublishRequest = BookPublishRequestConverter.toBookPublishRequest(request);
        manager.addRequest(bookPublishRequest);

        var publishingStatusItem = new AtomicReference<>(
                publishingStatusDao.setPublishingStatus(bookPublishRequest
                                .getPublishingRecordId(), PublishingRecordStatus.QUEUED,
                        bookPublishRequest.getBookId())
        );
        // if bookId is present in catalog, generate a new record with it's corresponding id in publishing status dao
        // if BookNotFoundException is caught, log it but don't throw it,
        // because the incoming request still needs to be added to the queue, just not with the exsiting bookId
        if (StringUtils.isNotBlank(bookPublishRequest.getBookId())) {
            try {
                catalogDao.isExsitingCatalogItem(bookPublishRequest.getBookId())
                        .ifPresentOrElse(catalogItemVersion -> publishingStatusItem.set(publishingStatusDao
                                        .setPublishingStatus(
                                                bookPublishRequest.getPublishingRecordId(),
                                                PublishingRecordStatus.QUEUED,
                                                catalogItemVersion.getBookId())
                        ), () -> {
                            throw new BookNotFoundException("BookId was present in request, but not found in catalog");
                        });
            } catch (BookNotFoundException e) {
                App.logger.error("Error while checking if book exists", e);
                throw e;
            }
        }

        return SubmitBookForPublishingResponse.builder()
                .withPublishingRecordId(publishingStatusItem.get().getPublishingRecordId())
                .build();

    }
}
