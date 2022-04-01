package com.amazon.ata.kindlepublishingservice.dagger;


import com.amazon.ata.kindlepublishingservice.activity.SubmitBookForPublishingActivity;
import com.amazon.ata.kindlepublishingservice.clients.RecommendationsServiceClient;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishTask;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishingManager;
import com.amazon.ata.recommendationsservice.RecommendationsService;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * The type Clients module.
 */
@Module
public class ClientsModule {


    /**
     * Provide recommendations service client recommendations service client.
     *
     * @param recommendationsService the recommendations service
     *
     * @return the recommendations service client
     */
    @Singleton
    @Provides
    public RecommendationsServiceClient provideRecommendationsServiceClient(
            RecommendationsService recommendationsService) {
        return new RecommendationsServiceClient(recommendationsService);
    }

    /**
     * Provide catalog dao catalog dao.
     *
     * @param dynamoDBMapper the dynamo db mapper
     *
     * @return the catalog dao
     */
    @Singleton
    @Provides
    public CatalogDao provideCatalogDao(DynamoDBMapper dynamoDBMapper) {
        return new CatalogDao(dynamoDBMapper);
    }

    /**
     * Provide publishing status dao publishing status dao.
     *
     * @param dynamoDBMapper the dynamo db mapper
     *
     * @return the publishing status dao
     */
    @Singleton
    @Provides
    public PublishingStatusDao providePublishingStatusDao(DynamoDBMapper dynamoDBMapper) {
        return new PublishingStatusDao(dynamoDBMapper);
    }

    /**
     * Provide submit book for publishing activity submit book for publishing activity.
     *
     * @param catalogDao          the catalog dao
     * @param publishingStatusDao the publishing status dao
     * @param manager             the manager
     *
     * @return submit book for publishing activity
     */
    @Singleton
    @Provides
    public SubmitBookForPublishingActivity provideSubmitBookForPublishingActivity(
            CatalogDao catalogDao, PublishingStatusDao publishingStatusDao, BookPublishingManager manager) {
        return new SubmitBookForPublishingActivity(catalogDao, publishingStatusDao,
                manager);
    }

    /**
     * Provide book publish task book publish task.
     *
     * @param catalogDao          the catalog dao
     * @param publishingStatusDao the publishing status dao
     * @param manager             the manager
     *
     * @return the book publish task
     */
    @Singleton
    @Provides
    public BookPublishTask provideBookPublishTask(CatalogDao catalogDao, PublishingStatusDao publishingStatusDao,
                                                  BookPublishingManager manager) {
        return new BookPublishTask(catalogDao, publishingStatusDao, manager);
    }

    /**
     * Provide book publishing manager book publishing manager.
     *
     * @return the book publishing manager
     */
    @Singleton
    @Provides
    public BookPublishingManager provideBookPublishingManager() {
        return new BookPublishingManager();
    }

}


