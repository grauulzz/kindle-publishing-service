//package com.amazon.ata.kindlepublishingservice.dagger;
//
//import com.amazon.ata.kindlepublishingservice.activity.GetBookActivity;
//import com.amazon.ata.kindlepublishingservice.activity.GetPublishingStatusActivity;
//import com.amazon.ata.kindlepublishingservice.activity.RemoveBookFromCatalogActivity;
//import com.amazon.ata.kindlepublishingservice.activity.SubmitBookForPublishingActivity;
//import com.amazon.ata.kindlepublishingservice.clients.RecommendationsServiceClient;
//import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
//import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
//import dagger.Module;
//import dagger.Provides;
//import javax.inject.Singleton;
//
//@Module
//public class ActivityModule {
//    @Singleton
//    @Provides
//    public GetBookActivity provideGetBookActivity(CatalogDao catalogDao,
//                                                  RecommendationsServiceClient recommendationsServiceClient) {
//        return new GetBookActivity(catalogDao, recommendationsServiceClient);
//    }
//
//    @Singleton
//    @Provides
//    public RemoveBookFromCatalogActivity provideRemoveBookFromCatalogActivity(CatalogDao catalogDao) {
//        return new RemoveBookFromCatalogActivity(catalogDao);
//    }
//
//    @Singleton
//    @Provides
//    public SubmitBookForPublishingActivity provideSubmitBookForPublishingActivity(
//            PublishingStatusDao publishingStatusDao, CatalogDao catalogDao) {
//        return new SubmitBookForPublishingActivity(publishingStatusDao, catalogDao);
//    }
//
//    @Provides
//    public GetPublishingStatusActivity provideGetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
//        return new GetPublishingStatusActivity(publishingStatusDao);
//    }
//
//}
