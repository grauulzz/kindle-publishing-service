package com.amazon.ata.kindlepublishingservice.dagger;


import com.amazon.ata.kindlepublishingservice.activity.SubmitBookForPublishingActivity;
import com.amazon.ata.kindlepublishingservice.clients.RecommendationsServiceClient;
//import com.amazon.ata.kindlepublishingservice.metrics.MetricsPublisher;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.recommendationsservice.RecommendationsService;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class ClientsModule {

    @Singleton
    @Provides
    public RecommendationsServiceClient provideRecommendationsServiceClient(
        RecommendationsService recommendationsService) {
        return new RecommendationsServiceClient(recommendationsService);
    }

    @Singleton
    @Provides
    public CatalogDao provideCatalogDao(DynamoDBMapper dynamoDBMapper) {
        return new CatalogDao(dynamoDBMapper);
    }

    @Singleton
    @Provides
    public PublishingStatusDao providePublishingStatusDao(DynamoDBMapper dynamoDBMapper) {
        return new PublishingStatusDao(dynamoDBMapper);
    }

    @Singleton
    @Provides
    public SubmitBookForPublishingActivity provideSubmitBookForPublishingActivity() {
        return new SubmitBookForPublishingActivity();
    }

//    @Singleton
//    @Provides
//    public MetricsPublisher provideMetricsPublisher() {
//        return new MetricsPublisher();
//    }
}


