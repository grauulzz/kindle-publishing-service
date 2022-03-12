package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;

import javax.inject.Inject;

public class RemoveBookFromCatalogActivity {

    private final CatalogDao catalogDao;

    @Inject
    public RemoveBookFromCatalogActivity() {
        this.catalogDao = App.component.provideCatalogDao();
    }

    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest request) {
        CatalogItemVersion item = catalogDao.getBookFromCatalog(request.getBookId());
        // CatalogItemVersion item = catalogDao.getLatestVersionOfBook(request.getBookId());

        item.setInactive(true);
        catalogDao.saveItem(item);
        return new RemoveBookFromCatalogResponse(item);
    }
}