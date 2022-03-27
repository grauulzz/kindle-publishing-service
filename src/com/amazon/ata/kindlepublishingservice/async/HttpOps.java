package com.amazon.ata.kindlepublishingservice.async;

import com.amazon.ata.kindlepublishingservice.publishing.AmazonWebServiceResponseHandler;
import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import java.util.concurrent.CompletableFuture;

public class HttpOps {

    public static int getHttpStatus(CompletableFuture<GetItemResult> future) {
        return future.whenComplete((response, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else {
                System.out.println("Http response: " + response.getSdkHttpMetadata().getHttpStatusCode());
            }
        }).join().getSdkHttpMetadata().getHttpStatusCode();
    }
}
