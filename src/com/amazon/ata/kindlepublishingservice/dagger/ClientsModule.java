package com.amazon.ata.kindlepublishingservice.dagger;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.clients.RecommendationsServiceClient;
//import com.amazon.ata.kindlepublishingservice.metrics.MetricsPublisher;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
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
    public CatalogDao provideCatalogDao() {
        return new CatalogDao(new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient()));
    }

}
