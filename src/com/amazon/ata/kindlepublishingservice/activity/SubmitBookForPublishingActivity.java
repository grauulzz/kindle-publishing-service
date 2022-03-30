package com.amazon.ata.kindlepublishingservice.activity;


import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.converters.BookPublishRequestConverter;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequest;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishingManager;

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
    public SubmitBookForPublishingResponse execute(SubmitBookForPublishingRequest request) {
        BookPublishRequest bookPublishRequest = BookPublishRequestConverter.toBookPublishRequest(request);
        manager.addRequest(bookPublishRequest);

        if (request.getBookId() != null) {
            String id = request.getBookId();
            catalogDao.isExsitingCatalogItem(id)
                    .orElseThrow(() -> new BookNotFoundException(
                            String.format("could not find [%s] in CatalogItemsTable", id)));

            PublishingStatusItem publishingStatusItem = publishingStatusDao.setPublishingStatus(
                    bookPublishRequest.getPublishingRecordId(),
                    PublishingRecordStatus.QUEUED,
                    id);

            return SubmitBookForPublishingResponse.builder()
                    .withPublishingRecordId(publishingStatusItem.getPublishingRecordId())
                    .build();
        }

        String bookPublishRequestId = bookPublishRequest.getBookId();
        String publishingRecordId = bookPublishRequest.getPublishingRecordId();


        PublishingStatusItem item = publishingStatusDao.setPublishingStatus(
                publishingRecordId,
                PublishingRecordStatus.QUEUED,
                bookPublishRequestId);

        return SubmitBookForPublishingResponse.builder()
                .withPublishingRecordId(item.getPublishingRecordId())
                .build();
    }
}

