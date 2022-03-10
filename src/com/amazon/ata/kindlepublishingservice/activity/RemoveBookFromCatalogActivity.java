package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.App;
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
    public RemoveBookFromCatalogActivity() {
        this.catalogDao = App.component.provideCatalogDao();
    }

    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        CatalogItemVersion catalogItemVersion = catalogDao.getBookFromCatalog(request.getBookId());
        try {
            CatalogItemVersion latestVersion = catalogDao.getLatestVersionOfBook(request.getBookId());
            latestVersion.setInactive(true);
            catalogDao.saveItem(latestVersion);
        } catch (BookNotFoundException e) {
           throw new BookNotFoundException("Book not found in catalog" + e);
        }
        return new RemoveBookFromCatalogResponse(catalogItemVersion);
    }
}

//    private final DynamoDBMapper mapper = new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient());
//
//    @Inject
//    RemoveBookFromCatalogActivity() {}
//
//
//    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest removeBookFromCatalogRequest) {
//
//        Map<String, AttributeValue> eav = new HashMap<>();
//        eav.put(":val1", new AttributeValue().withS(removeBookFromCatalogRequest.getBookId()));
//
//        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression<CatalogItemVersion>()
//                .withKeyConditionExpression("bookId = :val1")
//                .withExpressionAttributeValues(eav);
//
//        PaginatedQueryList<CatalogItemVersion> query = mapper.query(CatalogItemVersion.class, queryExpression);
//
//
//        if(query.stream().filter(catalogItemVersion -> !catalogItemVersion.isInactive()).toArray().length <= 0){
//            throw new BookNotFoundException("There is no book");
//        }
//        CatalogItemVersion latestversion = query.stream()
//                .max(Comparator.comparing(CatalogItemVersion::getVersion))
//                .orElseThrow(NoSuchElementException::new);
//
//        latestversion.setInactive(true);
//
//        mapper.save(latestversion);
//
//
//        RemoveBookFromCatalogResponse response = new RemoveBookFromCatalogResponse();
//
//
//        return response;
//    }
//
//}



//    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest removeBookFromCatalogRequest) {;
//        allBooks.removeIf(catalogItemVersion -> catalogItemVersion.getBookId()
//                .equals(removeBookFromCatalogRequest.getBookId()));
//        return null;
//    }