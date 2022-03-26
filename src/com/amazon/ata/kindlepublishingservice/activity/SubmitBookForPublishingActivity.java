package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazon.ata.kindlepublishingservice.converters.BookPublishRequestConverter;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequest;

import com.amazon.ata.kindlepublishingservice.publishing.BookPublishingManager;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.google.gson.Gson;
import java.util.Optional;
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
     *
     * @return SubmitBookForPublishingResponse Response object that includes the publishing status id, which can be used
     * to check the publishing state of the book.
     */
    public SubmitBookForPublishingResponse execute(SubmitBookForPublishingRequest request) {
        final BookPublishRequest bookPublishRequest = BookPublishRequestConverter.toBookPublishRequest(request);

        if (request.getBookId() == null) {
            String generatedBookId = KindlePublishingUtils.generateBookId();
            BookPublishingManager.addRequest(BookPublishRequest.builder()
                    .withPublishingRecordId(bookPublishRequest.getPublishingRecordId())
                    .withBookId(generatedBookId)
                    .withText(request.getText()).withTitle(request.getTitle())
                    .withAuthor(request.getAuthor())
                    .withGenre(BookGenre.valueOf(request.getGenre()))
                    .build());
        }

        App.logger.info("Processing Publishing Submit Book Request: " + new Gson().toJson(request));
        BookPublishingManager.addRequest(bookPublishRequest);
        String bookPublishRequestId = bookPublishRequest.getBookId();
        String publishingRecordId = bookPublishRequest.getPublishingRecordId();

        if (StringUtils.isNotBlank(request.getBookId())) {
                CatalogItemVersion c = catalogDao.isExsitingCatalogItem(request.getBookId())
                        .orElseThrow(() -> new BookNotFoundException(
                                String.format("could not find [%s] in CatalogItemsTable", request.getBookId())));
//                BookPublishingManager.addRequest(BookPublishRequest.builder()
//                        .withPublishingRecordId(publishingRecordId).withBookId(c.getBookId())
//                        .withText(c.getText()).withTitle(c.getTitle())
//                        .withGenre(BookGenre.valueOf(request.getGenre()))
//                        .build());
//
//                catalogDao.saveItem(c);

        }

        PublishingStatusItem item = publishingStatusDao.setPublishingStatus(
                publishingRecordId,
                PublishingRecordStatus.QUEUED,
                bookPublishRequestId);

        publishingStatusDao.save(item);

        return SubmitBookForPublishingResponse.builder()
                .withPublishingRecordId(item.getPublishingRecordId())
                .build();
    }



}



//
//    BookPublishRequest bookPublishRequest = BookPublishRequestConverter.toBookPublishRequest(request);
//    String bookIdFromRequest = bookPublishRequest.getBookId();
//    String requestBookId = request.getBookId();
//    PublishingStatusItem item = publishingStatusDao.setPublishingStatus(
//            bookPublishRequest.getPublishingRecordId(),
//            PublishingRecordStatus.QUEUED,
//            bookIdFromRequest);
//
//        if (StringUtils.isNotBlank(requestBookId)) {
//                // submit an existing book if the bookId doesn't throw exception
//                CatalogItemVersion catalogItem = catalogDao.isExsitingCatalogItem(requestBookId)
//                .orElseThrow(() -> new BookNotFoundException(
//                String.format("could not find [%s] in CatalogItemsTable", requestBookId)));
//
//                Optional<PublishingStatusItem> optional = publishingStatusDao.queryItemsByBookId(catalogItem.getBookId());
//                                                       return optional.map(statusItem -> SubmitBookForPublishingResponse.builder()
//                                                       .withPublishingRecordId(statusItem.getPublishingRecordId())
//                                                       .build())
//                                                       .orElseGet(() -> SubmitBookForPublishingResponse.builder()
//                                                       .withPublishingRecordId(item.getPublishingRecordId())
//                                                       .build());
//
//                                                       }
//
//                                                       BookPublishingManager.addRequest(bookPublishRequest);
//                                                       return SubmitBookForPublishingResponse.builder()
//                                                       .withPublishingRecordId(item.getPublishingRecordId())
//                                                       .build();