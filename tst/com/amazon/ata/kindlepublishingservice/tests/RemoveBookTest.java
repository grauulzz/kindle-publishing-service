package com.amazon.ata.kindlepublishingservice.tests;

import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.activity.RemoveBookFromCatalogActivity;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveBookTest {


//    @Test
//    void removeBookResponseGoodRequest() {
//        String exsitingId = "book.ac510a76-008c-4478-b9f3-c277d74fa305";
//        RemoveBookFromCatalogRequest req = RemoveBookFromCatalogRequest.builder()
//                .withBookId(exsitingId).build();
//
//        RemoveBookFromCatalogActivity rmBookActivity = new RemoveBookFromCatalogActivity();
//        RemoveBookFromCatalogResponse response = rmBookActivity.execute(req);
//
//        CatalogItemVersion item = new CatalogItemVersion();
//        item.setBookId(exsitingId);
//
//        System.out.println(response.toJsonWithColor());
//
//    }

    @Test
    void badResponseThrowsException() {
        String nonExistingId = "book.nonExistingId";
        RemoveBookFromCatalogRequest req = RemoveBookFromCatalogRequest.builder()
                .withBookId(nonExistingId).build();

        RemoveBookFromCatalogActivity rmBookActivity = new RemoveBookFromCatalogActivity();

        Assertions.assertThrows(BookNotFoundException.class, () -> {
            rmBookActivity.execute(req);
        });
    }



}
