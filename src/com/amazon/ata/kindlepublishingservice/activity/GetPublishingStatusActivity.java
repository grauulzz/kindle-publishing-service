package com.amazon.ata.kindlepublishingservice.activity;


import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatus;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublisher;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class GetPublishingStatusActivity {
    private final DynamoDBMapper mapper;

    @Inject
    public GetPublishingStatusActivity() {
        this.mapper = App.component.provideDynamoDBMapper();
    }

    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {
        // needs a list of publishing status history
        String id = publishingStatusRequest.getPublishingRecordId();
        List<PublishingStatusItem> items = getPublishingStatusList();
        List<PublishingStatusItem> itemHistory = items.stream().filter(item -> item.getPublishingRecordId()
                .equals(id)).collect(Collectors.toList());

        List<PublishingStatusRecord> publishingStatusList = itemHistory.stream()
                .map(item -> new PublishingStatusRecord(item.getStatus().name(),
                item.getStatusMessage(), item.getBookId())).collect(Collectors.toList());


        if (publishingStatusList.isEmpty()) {
            throw new PublishingStatusNotFoundException(String.format("No Publishing history available for [%s]", id));
        }



        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(publishingStatusList).build();
    }

    private List<PublishingStatusItem> getPublishingStatusList() {
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient()
                .scan(new ScanRequest("PublishingStatus"));

        return result.getItems().stream().map(item -> this.mapper
                        .marshallIntoObject(PublishingStatusItem.class, item))
                .collect(Collectors.toList());
    }
}


//    List<PublishingStatusRecord> success = publishingStatusList.stream().filter(status -> status.getStatus().equals(PublishingStatus.SUCCESSFUL)).collect(Collectors.toList());
//    List<PublishingStatusRecord> failed = publishingStatusList.stream().filter(status -> status.getStatus().equals(PublishingStatus.FAILED)).collect(Collectors.toList());
//    List<PublishingStatusRecord> inProgress = publishingStatusList.stream().filter(status -> status.getStatus().equals(PublishingStatus.IN_PROGRESS)).collect(Collectors.toList());

//    List<PublishingStatusItem> publishingStatusList = itemHistory.stream()
//            .map(item -> new PublishingStatusDao(mapper).setPublishingStatus(
//                    item.getStatus().toString(),
//                    PublishingRecordStatus.valueOf(item.getStatus().name()),
//                    item.getBookId())).collect(Collectors.toList());


//        publishingStatusList.forEach(item -> new PublishingStatusDao(mapper).setPublishingStatus(
//                item.getBookId(),
//                PublishingRecordStatus.valueOf(item.getStatus()), item.getBookId()));