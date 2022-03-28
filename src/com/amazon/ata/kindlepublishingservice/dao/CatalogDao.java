package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;

import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class CatalogDao {

    private final DynamoDBMapper dynamoDbMapper;


    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;

    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if ((book == null) || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression<CatalogItemVersion>()
                .withHashKeyValues(book)
                .withScanIndexForward(false)
                .withLimit(1);

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class,
                queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public void saveItem(CatalogItemVersion version) {
        dynamoDbMapper.save(version);
    }

    public List<CatalogItemVersion> getCatalogItemsList() {
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient()
                .scan(new ScanRequest("CatalogItemVersions"));

        return result.getItems().stream()
                .map(item -> this.dynamoDbMapper
                        .marshallIntoObject(CatalogItemVersion.class, item))
                .collect(Collectors.toList());
    }

    public Optional<CatalogItemVersion> isExsitingCatalogItem(String bookId) {
        return getCatalogItemsList().stream().filter(item -> item
                .getBookId().equals(bookId)).findFirst();
    }

    public <U> CatalogItemVersion load(String id) {
        return dynamoDbMapper.load(CatalogItemVersion.class, id);
    }
}


