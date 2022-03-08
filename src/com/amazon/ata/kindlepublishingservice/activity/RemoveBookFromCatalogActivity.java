package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.models.Book;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;

import java.util.List;
import java.util.ListIterator;
import javax.inject.Inject;

public class RemoveBookFromCatalogActivity {
    private final List<CatalogItemVersion> allBooks = CatalogDao.getAllBooksFromCatalog();
    @Inject
    RemoveBookFromCatalogActivity() {}

    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest removeBookFromCatalogRequest) {;
        allBooks.removeIf(catalogItemVersion -> catalogItemVersion.getBookId()
                .equals(removeBookFromCatalogRequest.getBookId()));
        return null;
    }

}
