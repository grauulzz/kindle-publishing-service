package com.amazon.ata.kindlepublishingservice.async;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.models.response.FormatResponse;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DynamoDBAsync {
    public static final AmazonDynamoDB client = DynamoDbClientProvider.getDynamoDBClient();


    static Set<String> processFutureGetItemResult(CompletableFuture<GetItemResult> async) {
        CompletableFuture<GetItemResult> getItemResult = async.whenComplete((result, throwable) -> {
           try {
               if (result != null) {
                   // sideEffect if async getItem completes a with non-null value (meaning it's present in db)
                   Map<String, AttributeValue> map = result.getItem();
                   System.out.println("yay! \n" + FormatResponse.toJsonWithColor(map));
               }
               // if you catch my drift
           } catch (Throwable e) {
               // another side effect if async getItem is null
               e.printStackTrace();
           } finally {
               client.shutdown();
           }
        });
        // actual result of async processing (should return the item's attribute values effected)
        return getItemResult.join().getItem().keySet();
    }

    public static CompletableFuture<GetItemResult> createCompletableFuture(
            String tableName, String key,  String keyVal, String rangeKey, int rangeKeyVal
    ) {
        return CompletableFuture.supplyAsync(() -> getItem(tableName, key,
                keyVal, rangeKey, rangeKeyVal));
    }

    public static GetItemResult getItem(String tableName, String key,  String keyVal, String rangeKey, int rangeKeyVal) {

        Entry<String, AttributeValue> keyToGet = new HashMap.SimpleEntry<>(key, new AttributeValue().withS(keyVal));
        Entry<String, AttributeValue> rangeKeyToGet = new HashMap.SimpleEntry<>(
                rangeKey, new AttributeValue().withN(String.valueOf(rangeKeyVal))
        );

        try {
            // Create a GetItemRequest instance
            GetItemRequest request = new GetItemRequest().withKey(keyToGet, rangeKeyToGet)
                    .withTableName(tableName);

            // peace of mind
            System.out.println(FormatResponse.toJsonWithColor(request));

            return client.getItem(request);

        } catch (AmazonDynamoDBException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}


//    static CompletableFuture<List<String>> asyncListTables() {
//        return CompletableFuture.supplyAsync(() -> client.listTables(new ListTablesRequest()).getTableNames());
//    }


//    private static CompletableFuture<GetBookResponse> getBookAsyncResponse(String bookId) {
//        return CompletableFuture.supplyAsync(() -> {
//            GetBookRequest request = GetBookRequest.builder().withBookId(bookId).build();
//            return component.provideGetBookActivity().execute(request);
//        });
//    }


//    private static CompletableFuture<SubmitBookForPublishingResponse> asyncGetIdFromSubmitResponse() {
//        Controller controller = new Controller();
//        return CompletableFuture.supplyAsync(
//                () -> component.;
//        );
//    }

//
//    private static CompletableFuture<GetPublishingStatusResponse> asyncGetPublishingStatusResponse(
//            GetPublishingStatusRequest request
//    ) {
//        return CompletableFuture.supplyAsync(
//                () -> component.provideGetPublishingStatusActivity().execute(request)
//        );
//    }

//    static List<String> publishALlIdsInProgress() {
//        CompletableFuture<List<String>> queuedPublishingIds = CompletableFuture.supplyAsync(() -> {
//
//        }
//    }
