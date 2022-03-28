package com.amazon.ata.kindlepublishingservice.dagger;

import com.amazon.ata.kindlepublishingservice.activity.GetBookActivity;
import com.amazon.ata.kindlepublishingservice.activity.GetPublishingStatusActivity;
import com.amazon.ata.kindlepublishingservice.activity.RemoveBookFromCatalogActivity;
import com.amazon.ata.kindlepublishingservice.activity.SubmitBookForPublishingActivity;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        ClientsModule.class,
        DataAccessModule.class,
        PublishingModule.class,
})
public interface ApplicationComponent {
    GetBookActivity provideGetBookActivity();

    GetPublishingStatusActivity provideGetPublishingStatusActivity();

    RemoveBookFromCatalogActivity provideRemoveBookFromCatalogActivity();

    SubmitBookForPublishingActivity provideSubmitBookForPublishingActivity();

    ATAKindlePublishingServiceManager provideATAKindlePublishingServiceManager();

    CatalogDao provideCatalogDao();

    PublishingStatusDao providePublishingStatusDao();

    DynamoDBMapper provideDynamoDBMapper();

}
