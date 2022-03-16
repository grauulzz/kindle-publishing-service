package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class GetPublishingStatusActivity {


    private final PublishingStatusDao publishingStatusDao;

    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
    }

    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {

        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(new ArrayList<>()).build();
    }
}





















//    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {
//        // needs a list of publishing status history
//        String id = publishingStatusRequest.getPublishingRecordId();
//        List<PublishingStatusItem> items = publishingStatusDao.getPublishingStatusList();
//        List<PublishingStatusItem> itemHistory = items.stream().filter(item -> item.getPublishingRecordId()
//                .equals(id)).collect(Collectors.toList());
//
//        List<PublishingStatusRecord> publishingStatusList = itemHistory.stream()
//                .map(item -> new PublishingStatusRecord(item.getStatus().name(),
//                        item.getStatusMessage(), item.getBookId())).collect(Collectors.toList());
//
//        if (publishingStatusList.isEmpty()) {
//            throw new PublishingStatusNotFoundException(String.format("No Publishing history available for [%s]", id));
//        }
//
//        return GetPublishingStatusResponse.builder()
//                .withPublishingStatusHistory(publishingStatusList).build();
//    }