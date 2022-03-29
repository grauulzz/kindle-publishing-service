package com.amazon.ata.kindlepublishingservice.dao;


import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequest;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormatConverter;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.scheduling.annotation.Async;

public class CatalogDao {

    private final DynamoDBMapper db;

    /**
     * Instantiates a new CatalogDao object.
     *
     * @param db The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper db) {
        this.db = db;

    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     *
     * @param bookId Id associated with the book.
     *
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
     *
     * @param bookId Id associated with the book.
     *
     * @return The corresponding CatalogItem from the catalog table.
     */
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression<CatalogItemVersion>()
                .withHashKeyValues(book)
                .withScanIndexForward(false)
                .withLimit(1);

        List<CatalogItemVersion> results = db.query(CatalogItemVersion.class,
                queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    /**
     * Save item.
     *
     * @param version the version
     */
    public void saveItem(CatalogItemVersion version) {
        db.save(version);
    }

    /**
     * @return result from db scan
     */
    public List<CatalogItemVersion> getCatalogItemsList() {
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient()
                .scan(new ScanRequest("CatalogItemVersions"));

        return result.getItems().stream()
                .map(item -> this.db.marshallIntoObject(CatalogItemVersion.class, item))
                .collect(Collectors.toList());
    }

    /**
     * Is exsiting catalog item optional.
     *
     * @param bookId the book id
     *
     * @return the optional
     */
    public Optional<CatalogItemVersion> isExsitingCatalogItem(String bookId) {
        return getCatalogItemsList().stream().filter(item -> item
                .getBookId().equals(bookId)).findFirst();
    }

    /**
     * Load catalog item version.
     *
     * @param <U> the type parameter
     * @param id  the id
     *
     * @return the catalog item version
     *
     * @throws DynamoDBMappingException the dynamo db mapping exception
     * @throws BookNotFoundException    the book not found exception
     */

    public <U> CompletableFuture<CatalogItemVersion> load(String id) throws DynamoDBMappingException, BookNotFoundException {

        CatalogItemVersion item = db.load(CatalogItemVersion.class, id);
        if (item == null) {
            throw new BookNotFoundException("Book not found on load from db");
        }
        return CompletableFuture.completedFuture(item);
    }

    /**
     * Publish.
     *
     * @param request the request
     * @param bookId  the book id
     */
    public void publish(BookPublishRequest request, String bookId) {
        KindleFormattedBook kindleFormat = KindleFormatConverter.format(request);
        CatalogItemVersion catalogItemVersion = new CatalogItemVersion();
        catalogItemVersion.setBookId(bookId);
        catalogItemVersion.setTitle(kindleFormat.getTitle());
        catalogItemVersion.setAuthor(kindleFormat.getAuthor());
        catalogItemVersion.setGenre(kindleFormat.getGenre());
        catalogItemVersion.setText(kindleFormat.getText());
        saveItem(catalogItemVersion);
    }

    /**
     * Publish new version.
     *
     * @param request        the request
     * @param currentVersion the current version
     */
    public void publishNewVersion(BookPublishRequest request, CatalogItemVersion currentVersion) {
        KindleFormattedBook kindleFormat = KindleFormatConverter.format(request);
        String text = kindleFormat.getText();
        String title = kindleFormat.getTitle();
        String author = kindleFormat.getAuthor();
        BookGenre genre = kindleFormat.getGenre();

        CatalogItemVersion newVersion = new CatalogItemVersion();
        newVersion.setVersion(currentVersion.getVersion() + 1);
        newVersion.setBookId(currentVersion.getBookId());
        newVersion.setText(text);
        newVersion.setTitle(title);
        newVersion.setAuthor(author);
        newVersion.setGenre(genre);
        saveItem(newVersion);
    }
}


