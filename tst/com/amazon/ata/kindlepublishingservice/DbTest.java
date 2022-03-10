package com.amazon.ata.kindlepublishingservice;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.converters.CatalogItemConverter;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.models.Book;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DbTest {
    private final DynamoDBMapper mapper = new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient());
    private CatalogItemVersion catalogItemVersion;
    private final String exsitingId = "book.ac510a76-008c-4478-b9f3-c277d74fa305";
    String nonExistingId = "book.nonExistingId";
    private final Book book = new Book(Book.builder().withBookId(exsitingId));
    private Book catalogToBook;


    @BeforeEach
    void setUp() {
        catalogItemVersion = new CatalogItemVersion();
        catalogItemVersion.setBookId("bookId");
        catalogItemVersion.setTitle("title");
        catalogItemVersion.setAuthor("author");
        catalogItemVersion.setText("text");
        catalogItemVersion.setGenre(BookGenre.AUTOBIOGRAPHY);
        catalogItemVersion.setVersion(1);
        catalogItemVersion.setInactive(false);
        catalogToBook = CatalogItemConverter.toBook(catalogItemVersion);
    }

    private static void print(String input) {
        String reset = "\u001B[31m";
        String teal = "\u001B[36m";
        System.out.printf("%s%s%s\n", teal, input, reset);

    }

    private static void shitJsonFormatter(String consoleOut) {
        Arrays.stream(new String[]{new GsonBuilder().disableHtmlEscaping()
                        .create().toJson( consoleOut)
                                           .replaceAll("[\\[]",
                        String.format("%s%s", "\n [", "\u001B[36m"))})
                .forEach(System.out::println);
    }

    @Test
    void daoTest() {

    }

    @Test
    void tableInfo() {
        DescribeTableResult tableDescription = DynamoDbClientProvider.getDynamoDBClient()
                .describeTable("CatalogItemVersions");

        print(tableDescription.toString());
    }

    @Test
    void scanRequestBookId() {
        // list all items in "catalogItemVersions" table
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient().scan(
                new ScanRequest("CatalogItemVersions"));

        List<Map<String, AttributeValue>> items = result.getItems();
        List<String> catalogItemIdList = items.stream().map(item -> item.get("bookId")
                .getS()).collect(Collectors.toList());

        print(catalogItemIdList.toString());
    }

    @Test
    void filterItemsByBookId() {
        ScanRequest scanRequest = new ScanRequest("CatalogItemVersions");
        scanRequest.addScanFilterEntry("bookId", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(exsitingId)));
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient().scan(scanRequest);
        print(result.toString());
    }

    @Test
    void filterItemsByActiveStatus() {
        ScanRequest scanRequest = new ScanRequest("CatalogItemVersions");

        scanRequest.addScanFilterEntry("inactive", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withN("0")));

        ScanResult result = DynamoDbClientProvider.getDynamoDBClient().scan(scanRequest);
        print(result.toString());
    }

    @Test
    void filterItemsByVersion() {
        ScanRequest scanRequest = new ScanRequest("CatalogItemVersions");
        scanRequest.addScanFilterEntry("version", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withN("1")));
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient().scan(scanRequest);

        // filter by

        print(result.toString());
    }

    @Test
    void setInactiveWithExpressionQuery() {
        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName("CatalogItemVersions")
                .withKey(new HashMap<String, AttributeValue>() {{
                    put("bookId", new AttributeValue().withS(exsitingId));
                }})
                .withUpdateExpression("set inactive = :inactive")
                .withExpressionAttributeValues(new HashMap<String, AttributeValue>() {{
                    put(":inactive", new AttributeValue().withN("1"));
                }});

        UpdateItemResult result = DynamoDbClientProvider.getDynamoDBClient().updateItem(updateItemRequest);
        print(result.toString());
    }

    @Test
    void filterItemsByBookIdAndActiveStatus() {
        ScanRequest scanRequest = new ScanRequest("CatalogItemVersions");
        scanRequest.addScanFilterEntry("bookId", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(exsitingId)));
        scanRequest.addScanFilterEntry("inactive", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withN("0")));
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient().scan(scanRequest);
        print(result.toString());
    }

    @Test
    void filterItemsByBookIdAndVersion() {
        ScanRequest scanRequest = new ScanRequest("CatalogItemVersions");
        scanRequest.addScanFilterEntry("bookId", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(exsitingId)));
        scanRequest.addScanFilterEntry("version", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withN("1")));
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient().scan(scanRequest);
        print(result.toString());
    }

    @Test
    void deleteExpression() {

        Book book = CatalogItemConverter.toBook(catalogItemVersion);

        try {
            DynamoDBDeleteExpression deleteExpression = new DynamoDBDeleteExpression();
            Map<String, ExpectedAttributeValue> expected = new HashMap<>();
            expected.put("inactive", new ExpectedAttributeValue(new AttributeValue().withBOOL(false)));
            deleteExpression.setExpected(expected);
            mapper.delete(book, deleteExpression);
        } catch (ConditionalCheckFailedException e) {
            System.out.println(e);
        }
    }
}
