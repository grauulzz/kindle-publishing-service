package com.amazon.ata.kindlepublishingservice.async;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.models.response.FormatResponse;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.amazon.ata.kindlepublishingservice.App.component;

public class DynamoDBAsync {
    public static final AmazonDynamoDB client = DynamoDbClientProvider.getDynamoDBClient();


    static void processFutureGetItemResult(String tableName, String key, String keyVal,
                                           String rangeKey, int rangeKeyVal, String publishingRecordId) {

        CompletableFuture<GetItemResult> async = CompletableFuture.supplyAsync(() -> getItem(
                tableName, key, keyVal, rangeKey, rangeKeyVal));

        async.whenComplete((result, throwable) -> {
            try {
                if (result != null) {
                    PublishingStatusItem successfulItem = component.providePublishingStatusDao()
                            .setPublishingStatus(publishingRecordId,
                            PublishingRecordStatus.SUCCESSFUL, keyVal);
                    component.providePublishingStatusDao().save(successfulItem);
                }
                PublishingStatusItem failedItem = component.providePublishingStatusDao()
                        .setPublishingStatus(publishingRecordId,
                                PublishingRecordStatus.FAILED, keyVal);
                component.providePublishingStatusDao().save(failedItem);

            } catch (Throwable e) {
                PublishingStatusItem failedItem = component.providePublishingStatusDao()
                        .setPublishingStatus(publishingRecordId,
                                PublishingRecordStatus.FAILED, keyVal);
                component.providePublishingStatusDao().save(failedItem);
                e.printStackTrace();
            } finally {
                client.shutdown();
            }
        });
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

            return client.getItem(request);

        } catch (AmazonDynamoDBException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public static int getHttpStatusGetITem(CompletableFuture<GetItemResult> future) {
        return future.whenComplete((response, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else {
                System.out.println("Http response: " + response.getSdkHttpMetadata().getHttpStatusCode());
            }
        }).join().getSdkHttpMetadata().getHttpStatusCode();
    }


}

////  original
//    static Set<String> processFutureGetItemResultReturnsSet(CompletableFuture<GetItemResult> async) {
//        CompletableFuture<GetItemResult> getItemResult = async.whenComplete((result, throwable) -> {
//            try {
//                if (result != null) {
//                    // sideEffect if async getItem completes a with non-null value (meaning it's present in db)
//                    Map<String, AttributeValue> map = result.getItem();
//                    System.out.println("yay! \n" + FormatResponse.toJsonWithColor(map));
//                }
//                // if you catch my drift
//            } catch (Throwable e) {
//                // another side effect if async getItem is null
//                e.printStackTrace();
//            } finally {
//                client.shutdown();
//            }
//        });
//        // actual result of async processing (should return the item's attribute values effected)
//        return getItemResult.join().getItem().keySet();
//    }


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
