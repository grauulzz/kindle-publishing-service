package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazon.ata.kindlepublishingservice.converters.BookPublishRequestConverter;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequest;

import javax.inject.Inject;
import org.junit.platform.commons.util.StringUtils;

/**
 * Implementation of the SubmitBookForPublishingActivity for ATACurriculumKindlePublishingService's
 * SubmitBookForPublishing API.
 *
 * This API allows the client to submit a new book to be published in the catalog or update an existing book.
 */
public class SubmitBookForPublishingActivity {

    private final PublishingStatusDao publishingStatusDao;
    private final CatalogDao catalogDao;

    @Inject
    public SubmitBookForPublishingActivity() {
        this.publishingStatusDao = App.component.providePublishingStatusDao();
        this.catalogDao = App.component.provideCatalogDao();
    }

    /**
     * Submits the book in the request for publishing.
     *
     * @param request Request object containing the book data to be published. If the request is updating an existing
     *                book, then the corresponding book id should be provided. Otherwise, the request will be treated
     *                as a new book.
     * @return SubmitBookForPublishingResponse Response object that includes the publishing status id, which can be used
     * to check the publishing state of the book.
     */
    public SubmitBookForPublishingResponse execute(SubmitBookForPublishingRequest request) {
        String requestBookId = request.getBookId();

        if (StringUtils.isNotBlank(requestBookId)) {
            try {
                boolean bookExists = catalogDao.checkCatalogForItem(requestBookId);
                if (bookExists) {
                    // updates an existing book
                    PublishingStatusItem matchingPublishingItem = publishingStatusDao.getPublishingStatusIdByBookId(requestBookId)
                            .orElseThrow(() -> new PublishingStatusNotFoundException(
                                    "Publishing status not found for book id: " + requestBookId)
                            );

                    return SubmitBookForPublishingResponse.builder()
                            .withPublishingRecordId(matchingPublishingItem.getPublishingRecordId())
                            .build();
                }

            } catch (BookNotFoundException e) {
                App.logger.error("Book not found for book id: " + requestBookId);

            }
        }

        BookPublishRequest bookPublishRequest = BookPublishRequestConverter.toBookPublishRequest(request);

        String bookIdFromRequest = bookPublishRequest.getBookId();

        PublishingStatusItem item = publishingStatusDao.setPublishingStatus(
                bookPublishRequest.getPublishingRecordId(),
                PublishingRecordStatus.QUEUED,
                bookIdFromRequest);

        publishingStatusDao.save(item);

        return SubmitBookForPublishingResponse.builder()
                .withPublishingRecordId(item.getPublishingRecordId())
                .build();

    }


}


//    public SubmitBookForPublishingResponse execute(SubmitBookForPublishingRequest request) {
//        String requestBookId = request.getBookId();
//
//        if (StringUtils.isNotBlank(requestBookId)) {
//            try {
//
//                boolean bookExists = catalogDao.isExsitingCatalogItem(requestBookId);
//
//                if (bookExists) {
//
//                    PublishingStatusItem matchingPublishingItem = publishingStatusDao.getPublishingStatusIdByBookId(requestBookId)
//                            .orElseThrow(() -> new PublishingStatusNotFoundException(
//                                    "Publishing status not found for book id: " + requestBookId)
//                            );
//                    return SubmitBookForPublishingResponse.builder()
//                            .withPublishingRecordId(matchingPublishingItem.getPublishingRecordId())
//                            .build();
//                }
//
//            } catch (BookNotFoundException e) {
//                App.logger.error("Book not found for book id: " + requestBookId, e);
//                throw e;
//
//            }
//        }
//
//        BookPublishRequest bookPublishRequest = BookPublishRequestConverter.toBookPublishRequest(request);
//        String bookIdFromRequest = bookPublishRequest.getBookId();
//
//        PublishingStatusItem item = publishingStatusDao.setPublishingStatus(
//                bookPublishRequest.getPublishingRecordId(),
//                PublishingRecordStatus.QUEUED,
//                bookIdFromRequest);
//
//        publishingStatusDao.save(item);
//
//        return SubmitBookForPublishingResponse.builder()
//                .withPublishingRecordId(item.getPublishingRecordId())
//                .build();
//
//    }

