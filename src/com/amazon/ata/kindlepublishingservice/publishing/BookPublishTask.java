package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatus;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.gson.Gson;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookPublishTask implements Runnable {
    private final BookPublishingContext context;
    private final PublishingStatusDao publishingStatusDao = App.component.providePublishingStatusDao();
    private final CatalogDao catalogDao = App.component.provideCatalogDao();
    public static final Logger logger = LoggerFactory.getLogger(BookPublishTask.class);



    @Inject
    public BookPublishTask(BookPublishingContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        logger.info("BookPublishTask started");
        while (context.hasNextRequest()) {
            BookPublishRequest request = context.getNextRequestInQueue();
        }
    }
}

//    public Optional<PublishingStatusItem> queryItemsByStatus(PublishingRecordStatus status) {
//
//        HashMap<String, Condition> scanFilter = new HashMap<>();
//        scanFilter.put("publishingRecordId", new Condition()
//                .withComparisonOperator(ComparisonOperator.EQ)
//                .withAttributeValueList(new AttributeValue().withS(status.name())));
//
//        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
//                .withScanFilter(scanFilter);
//
//        PaginatedScanList<PublishingStatusItem> scanResult = dynamoDbMapper.scan(
//                PublishingStatusItem.class, scanExpression);
//
//        if (!scanResult.isEmpty()) {
//            return Optional.of(scanResult.get(0));
//        }
//        return Optional.empty();
//    }
