package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

public class GetPublishingStatusActivity {


    private final PublishingStatusDao publishingStatusDao;

    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
    }


    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {
        String id = publishingStatusRequest.getPublishingRecordId();

        Map<String, List<PublishingStatusRecord>> publishingRecords = publishingStatusDao.getPublishingRecordHistory(id);
        List<PublishingStatusRecord> records = publishingRecords.get(id);

        if (records == null || records.isEmpty()) {
            throw new PublishingStatusNotFoundException(String.format("No Publishing history available for [%s]", id));
        }
        Set<String> hashKeys = publishingRecords.keySet();
        hashKeys.forEach(publishingStatusDao::saveByHashKey);

        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(records)
                .build();

    }
}


