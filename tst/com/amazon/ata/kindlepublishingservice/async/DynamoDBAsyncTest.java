package com.amazon.ata.kindlepublishingservice.async;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.models.response.FormatResponse;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;


class DynamoDBAsyncTest {

    @BeforeEach
    void setup() {
        // GIVEN
        CatalogItemVersion itemVersion = new CatalogItemVersion();
        itemVersion.setBookId("testId");
        itemVersion.setVersion(0);
        itemVersion.setText("test text");
        itemVersion.setGenre(BookGenre.AUTOBIOGRAPHY);
        itemVersion.setTitle("test title");
        itemVersion.setInactive(false);
        App.component.provideCatalogDao().saveItem(itemVersion);

    }

    @Test
    void getItemTest() {
        // WHEN
        GetItemResult getItemResult = DynamoDBAsync.getItem("CatalogItemVersions",
                "bookId", "testId", "version", 0);
        //THEN
        System.out.println(FormatResponse.toJsonWithColor(getItemResult));
        assert getItemResult.getSdkHttpMetadata().getHttpStatusCode() == 200;
    }

    @Test
    void asyncGetItem() {
        // WHEN
        CompletableFuture<GetItemResult> item = DynamoDBAsync.createCompletableFuture("CatalogItemVersions",
                "bookId", "testId", "version", 0);
        // THEN
//        Set<String> result = DynamoDBAsync.processFutureGetItemResult(item);
//        System.out.println("yay! (from tst): \n" + FormatResponse.toJsonWithColor(result));
//        assert StringUtils.isNotBlank(String.join("", result));
        assert HttpOps.getHttpStatus(item) == 200;
    }
























    @Test
    void asyncListTablesTest() {
        List<String> tableNames = listTablesExample();
        System.out.println(FormatResponse.toJsonWithColor(tableNames));
    }

    private static CompletableFuture<ListTablesResult> listTablesAsyncResponse() {
        return CompletableFuture.supplyAsync(() -> {
            ListTablesRequest request = new ListTablesRequest();
            return DynamoDbClientProvider.getDynamoDBClient().listTables(request);
        });
    }

    static List<String> listTablesExample() {

        // Map the response to another CompletableFuture containing just the table names
        CompletableFuture<List<String>> tableNames = listTablesAsyncResponse().thenApply(ListTablesResult::getTableNames);

        // When future is complete (either successfully or in error) handle the response
        tableNames.whenComplete((tables, err) -> {
            try {
                if (tables != null) {
                    tables.forEach(System.out::println);
                } else {
                    // Handle error
                    err.printStackTrace();
                }
            } finally {
                // Lets the application shut down. Only close the client when you are completely done with it.
                DynamoDbClientProvider.getDynamoDBClient().shutdown();
            }
        });
        return tableNames.join();
    }
}