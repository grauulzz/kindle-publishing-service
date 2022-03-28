package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

/**
 * Accesses the Publishing Status table.
 */
public class PublishingStatusDao {
//    private static final AmazonDynamoDB client = DynamoDbClientProvider.getDynamoDBClient();
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
     * @param publishingRecordId The id of the record to update
     * @param publishingRecordStatus The PublishingRecordStatus to save into the table.
     * @param bookId The id of the book associated with the request, may be null
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
     * @param publishingRecordId The id of the record to update
     * @param publishingRecordStatus The PublishingRecordStatus to save into the table.
     * @param bookId The id of the book associated with the request, may be null
     * @param message additional notes stored with the status
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

    public void save(PublishingStatusItem item) {
        db.save(item);
    }

    public void saveIfPresent(String hashKey) {
        this.loadOptional(hashKey).ifPresent(this.db::save);
    }

    public Optional<PublishingStatusItem> loadOptional(String publishingRecordId) {
        return Optional.of(db.load(PublishingStatusItem.class, publishingRecordId));
    }

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

    public List<PublishingStatusItem> getPublishingStatusList() {
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient()
                .scan(new ScanRequest("PublishingStatus"));

        return result.getItems().stream()
                .map(item -> this.db.marshallIntoObject(PublishingStatusItem.class, item))
                .collect(Collectors.toList());
    }


    public Map<String, List<PublishingStatusRecord>> getPublishingRecordHistory(String publishingRecordId) {
        Optional<PublishingStatusItem> recordId = loadOptional(publishingRecordId);
        Map<String, List<PublishingStatusRecord>> publishingRecords = new HashMap<>();

        if (recordId.isPresent()) {
            Predicate<PublishingStatusItem> predicate = item -> item.getPublishingRecordId().equals(publishingRecordId);
            List<PublishingStatusItem> items = getPublishingStatusList();
            Stream<PublishingStatusItem> listToMap = items
                    .parallelStream()
                    .filter(predicate);

            publishingRecords.put(publishingRecordId,
                    listToMap.map(item -> PublishingStatusRecord.builder()
                                    .withStatus(item.getStatus().name())
                                    .withStatusMessage(item.getStatusMessage())
                                    .withBookId(item.getBookId())
                                    .build())
                    .collect(Collectors.toList()));

            return publishingRecords;
        }

        return publishingRecords;
    }


    public List<PublishingStatusItem> statusItems(PublishingRecordStatus status) {
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient()
                .scan(new ScanRequest("PublishingStatus"));

        Predicate<PublishingStatusItem> filterByStatus = item -> item.getStatus().equals(status);

        return result.getItems().stream()
                .map(item -> db.marshallIntoObject(PublishingStatusItem.class, item))
                .filter(filterByStatus).collect(Collectors.toList());
    }
//    public List<PublishingStatusItem> statusItems(String id, PublishingRecordStatus status) {
//        ScanResult result = DynamoDbClientProvider.getDynamoDBClient()
//                .scan(new ScanRequest("PublishingStatus"));
//
//        Predicate<PublishingStatusItem> filterById = item -> item.getPublishingRecordId().equals(id);
//        Predicate<PublishingStatusItem> filterByStatus = item -> item.getStatus().equals(status);
//
//        return result.getItems().stream()
//                .map(item -> db.marshallIntoObject(PublishingStatusItem.class, item))
//                .filter(filterById.and(filterByStatus)).collect(Collectors.toList());
//    }

}