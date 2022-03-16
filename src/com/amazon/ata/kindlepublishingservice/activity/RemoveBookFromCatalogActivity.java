package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import javax.inject.Inject;

public class RemoveBookFromCatalogActivity {

    private final CatalogDao catalogDao;

    @Inject
    public RemoveBookFromCatalogActivity(CatalogDao catalogDao) {
        this.catalogDao = catalogDao;
    }

    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest request) {
        CatalogItemVersion item;
        try {
            item = catalogDao.getBookFromCatalog(request.getBookId());
            item.setInactive(true);
            catalogDao.saveItem(item);

        } catch (BookNotFoundException | DynamoDBMappingException e) {
            throw new BookNotFoundException("Book null or not found", e);
        }
        return new RemoveBookFromCatalogResponse(item);
    }
}