package com.amazon.ata.kindlepublishingservice.dagger;

import com.amazon.ata.kindlepublishingservice.activity.GetBookActivity;
import com.amazon.ata.kindlepublishingservice.activity.GetPublishingStatusActivity;
import com.amazon.ata.kindlepublishingservice.activity.RemoveBookFromCatalogActivity;
import com.amazon.ata.kindlepublishingservice.activity.SubmitBookForPublishingActivity;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishTask;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishingManager;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.Component;

import javax.inject.Singleton;

/**
 * The interface Application component.
 */
@Singleton
@Component(modules = {
        ClientsModule.class,
        DataAccessModule.class,
        PublishingModule.class,
})
public interface ApplicationComponent {
    /**
     * Provide get book activity get book activity.
     *
     * @return the get book activity
     */
    GetBookActivity provideGetBookActivity();

    /**
     * Provide get publishing status activity get publishing status activity.
     *
     * @return the get publishing status activity
     */
    GetPublishingStatusActivity provideGetPublishingStatusActivity();

    /**
     * Provide remove book from catalog activity remove book from catalog activity.
     *
     * @return the remove book from catalog activity
     */
    RemoveBookFromCatalogActivity provideRemoveBookFromCatalogActivity();

    /**
     * Provide submit book for publishing activity submit book for publishing activity.
     *
     * @return the submit book for publishing activity
     */
    SubmitBookForPublishingActivity provideSubmitBookForPublishingActivity();

    /**
     * Provide ata kindle publishing service manager ata kindle publishing service manager.
     *
     * @return the ata kindle publishing service manager
     */
    ATAKindlePublishingServiceManager provideATAKindlePublishingServiceManager();

    /**
     * Provide catalog dao catalog dao.
     *
     * @return the catalog dao
     */
    CatalogDao provideCatalogDao();

    /**
     * Provide publishing status dao publishing status dao.
     *
     * @return the publishing status dao
     */
    PublishingStatusDao providePublishingStatusDao();

    /**
     * Provide dynamo db mapper dynamo db mapper.
     *
     * @return the dynamo db mapper
     */
    DynamoDBMapper provideDynamoDBMapper();

    /**
     * Provide book publish task book publish task.
     *
     * @return the book publish task
     */
    BookPublishTask provideBookPublishTask();

    /**
     * Provide book publishing manager book publishing manager.
     *
     * @return the book publishing manager
     */
    BookPublishingManager provideBookPublishingManager();
}
