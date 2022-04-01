package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * The type Get publishing status activity.
 */
public class GetPublishingStatusActivity {


    private final PublishingStatusDao publishingStatusDao;

    /**
     * Instantiates a new Get publishing status activity.
     *
     * @param publishingStatusDao the publishing status dao
     */
    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
    }


    /**
     * Execute get publishing status response.
     *
     * @param publishingStatusRequest the publishing status request
     *
     * @return the get publishing status response
     */
    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {

        String id = publishingStatusRequest.getPublishingRecordId();

        List<PublishingStatusItem> items = publishingStatusDao.getPublishingStatusList()
                .stream().filter(item -> item.getPublishingRecordId().equals(id))
                .collect(Collectors.toList());

        List<PublishingStatusRecord> publishingStatusList = items.stream()
                .map(item -> new PublishingStatusRecord(item.getStatus().name(),
                        item.getStatusMessage(), item.getBookId()))
                .collect(Collectors.toList());

        if (publishingStatusList.isEmpty()) {
            throw new PublishingStatusNotFoundException(String.format("No Publishing history available for [%s]", id));
        }

        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(publishingStatusList).build();

    }
}


