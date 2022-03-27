package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import java.util.concurrent.CompletableFuture;

public interface AmazonWebServiceResponseHandler<T> {
    int getHttpStatus(CompletableFuture<GetItemResult> future);
}
