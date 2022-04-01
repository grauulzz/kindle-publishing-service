package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;

import javax.inject.Inject;

/**
 * The type Remove book from catalog activity.
 */
public class RemoveBookFromCatalogActivity {

    private final CatalogDao catalogDao;

    /**
     * Instantiates a new Remove book from catalog activity.
     *
     * @param catalogDao the catalog dao
     */
    @Inject
    public RemoveBookFromCatalogActivity(CatalogDao catalogDao) {
        this.catalogDao = catalogDao;
    }

    /**
     * Execute remove book from catalog response.
     *
     * @param request the request
     *
     * @return the remove book from catalog response
     */
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
