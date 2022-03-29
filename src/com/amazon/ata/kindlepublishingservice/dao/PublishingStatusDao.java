package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequest;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.*;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class PublishingStatusDao {
    private static final String ADDITIONAL_NOTES_PREFIX = " Additional Notes: ";
    private final DynamoDBMapper db;

    /**
     * Instantiates a new PublishingStatusDao object.
     *
     * @param db The {@link DynamoDBMapper} used to interact with the publishing status table.
     */
    @Inject
    public PublishingStatusDao(DynamoDBMapper db) {
        this.db = db;
    }

    /**
     * Updates the publishing status table for the given publishingRecordId with the provided
     * publishingRecordStatus. If the bookId is provided it will also be stored in the record.
     *
     * @param publishingRecordId     The id of the record to update
     * @param publishingRecordStatus The PublishingRecordStatus to save into the table.
     * @param bookId                 The id of the book associated with the request, may be null
     *
     * @return The stored PublishingStatusItem.
     */
    public PublishingStatusItem setPublishingStatus(String publishingRecordId,
                                                    PublishingRecordStatus publishingRecordStatus,
                                                    String bookId) {
        return setPublishingStatus(publishingRecordId,
                publishingRecordStatus, bookId, null);
    }

    /**
     * Updates the publishing status table for the given publishingRecordId with the provided
     * publishingRecordStatus. If the bookId is provided it will also be stored in the record. If
     * a message is provided, it will be appended to the publishing status message in the datastore.
     *
     * @param publishingRecordId     The id of the record to update
     * @param publishingRecordStatus The PublishingRecordStatus to save into the table.
     * @param bookId                 The id of the book associated with the request, may be null
     * @param message                additional notes stored with the status
     *
     * @return The stored PublishingStatusItem.
     */
    public PublishingStatusItem setPublishingStatus(String publishingRecordId,
                                                    PublishingRecordStatus publishingRecordStatus,
                                                    String bookId,
                                                    String message) {
        String statusMessage = KindlePublishingUtils.generatePublishingStatusMessage(publishingRecordStatus);
        if (StringUtils.isNotBlank(message)) {
            statusMessage = new StringBuffer()
                    .append(statusMessage)
                    .append(ADDITIONAL_NOTES_PREFIX)
                    .append(message)
                    .toString();
        }

        PublishingStatusItem item = new PublishingStatusItem();
        item.setPublishingRecordId(publishingRecordId);
        item.setStatus(publishingRecordStatus);
        item.setStatusMessage(statusMessage);
        item.setBookId(bookId);
        db.save(item);
        return item;
    }

    /**
     * Save.
     *
     * @param item the item
     */
    public void save(PublishingStatusItem item) {
        db.save(item);
    }

    /**
     * Load publishing status item.
     *
     * @param publishingRecordId the publishing record id
     *
     * @return the publishing status item
     */
    public PublishingStatusItem load(String publishingRecordId) {
        try {
            return db.load(PublishingStatusItem.class, publishingRecordId);
        } catch (AmazonServiceException e) {
            if (e.getStatusCode() == 404) {
                App.logger.error("PublishingStatusItem not found for publishingRecordId: " + publishingRecordId);
                return null;
            }
            throw e;
        }
    }

    /**
     * Gets publishing status list.
     *
     * @return the publishing status list
     */
    public List<PublishingStatusItem> getPublishingStatusList() {
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient()
                .scan(new ScanRequest("PublishingStatus"));

        return result.getItems().stream()
                .map(item -> this.db.marshallIntoObject(PublishingStatusItem.class, item))
                .collect(Collectors.toList());
    }

    /**
     * Mark in progress.
     *
     * @param request       the request
     * @param requestBookId the request book id
     */
    public void markInProgress(BookPublishRequest request, String requestBookId) {
        PublishingStatusItem inProgressItem = setPublishingStatus(
                request.getPublishingRecordId(), PublishingRecordStatus.IN_PROGRESS,
                requestBookId);
        save(inProgressItem);
    }

    /**
     * Mark successful.
     *
     * @param publishingRecordId the publishing record id
     * @param item               the item
     */
    public void markSuccessful(String publishingRecordId, CatalogItemVersion item) {
        PublishingStatusItem successfulItem = setPublishingStatus(publishingRecordId,
                PublishingRecordStatus.SUCCESSFUL, item.getBookId());
        save(successfulItem);
    }

    /**
     * Mark failed.
     *
     * @param publishingRecordId the publishing record id
     * @param requestBookId      the request book id
     */
    public void markFailed(String publishingRecordId, String requestBookId) {
        PublishingStatusItem failedItem = setPublishingStatus(publishingRecordId,
                PublishingRecordStatus.SUCCESSFUL, requestBookId);
        save(failedItem);
    }


    /**
     * Query items by book id optional.
     *
     * @param bookId the book id
     *
     * @return the optional
     */
    public Optional<PublishingStatusItem> queryItemsByBookId(String bookId) {

        HashMap<String, Condition> scanFilter = new HashMap<>();
        scanFilter.put("publishingRecordId", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(bookId)));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withScanFilter(scanFilter);

        PaginatedScanList<PublishingStatusItem> scanResult = db.scan(
                PublishingStatusItem.class, scanExpression);

        if (!scanResult.isEmpty()) {
            return Optional.of(scanResult.get(0));
        }
        return Optional.empty();
    }

}
