package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazon.ata.kindlepublishingservice.converters.BookPublishRequestConverter;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequest;

import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

/**
 * Implementation of the SubmitBookForPublishingActivity for ATACurriculumKindlePublishingService's
 * SubmitBookForPublishing API.
 *
 * This API allows the client to submit a new book to be published in the catalog or update an existing book.
 */
public class SubmitBookForPublishingActivity {

    private final PublishingStatusDao publishingStatusDao;

    /**
     * Instantiates a new SubmitBookForPublishingActivity object.
     *
     * @param publishingStatusDao PublishingStatusDao to access the publishing status table.
     */
    @Inject
    public SubmitBookForPublishingActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
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
        BookPublishRequest bookPublishRequest = BookPublishRequestConverter.toBookPublishRequest(request);

        if (StringUtils.isNotBlank(bookPublishRequest.getBookId())) {
            this.getCatalogItems().stream()
                    .filter(item -> item.getBookId().equals(bookPublishRequest.getBookId()))
                    .findFirst().orElseThrow(() ->
                                                     new BookNotFoundException(bookPublishRequest.getBookId()));
        }

        PublishingStatusItem item = publishingStatusDao.setPublishingStatus(
                bookPublishRequest.getPublishingRecordId(),
                PublishingRecordStatus.QUEUED,
                bookPublishRequest.getBookId());

        App.component.provideDynamoDBMapper().save(item);


        return SubmitBookForPublishingResponse.builder()
                .withPublishingRecordId(item.getPublishingRecordId())
                .build();
    }

    private List<CatalogItemVersion> getCatalogItems() {
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient()
                .scan(new ScanRequest("CatalogItemVersions"));

        return result.getItems().stream().map(item -> App.component.provideDynamoDBMapper()
                        .marshallIntoObject(CatalogItemVersion.class, item))
                .collect(Collectors.toList());
    }

    public static class Handler implements RequestHandler<SubmitBookForPublishingRequest, SubmitBookForPublishingResponse> {

        private final SubmitBookForPublishingActivity submitBookForPublishingActivity = App.component.provideSubmitBookForPublishingActivity();

        @Override
        public SubmitBookForPublishingResponse handleRequest(SubmitBookForPublishingRequest request, Context context) {
            return submitBookForPublishingActivity.execute(request);
        }
    }
}


