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
        CatalogItemVersion item = catalogDao.getLatestVersionOfBook(request.getBookId());

        int version = item.getVersion();
        item.setInactive(true);
        catalogDao.saveItem(item);
        item.setVersion(version + 1);
        item.setInactive(false);
        catalogDao.saveItem(item);
        return new RemoveBookFromCatalogResponse(item);
    }
}