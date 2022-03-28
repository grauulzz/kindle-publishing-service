package com.amazon.ata.kindlepublishingservice.async;

import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishTask;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.SubmissionPublisher;

public class AsyncHandler {
    private CompletableFuture<GetItemResult> futureGetItemResult;
    private BookPublishTask task;

    public AsyncHandler(CompletableFuture<GetItemResult> futureGetItemResult, BookPublishTask task) {
        this.futureGetItemResult = futureGetItemResult;
        this.task = task;
    }

    public void onExecuteMarkPublishingStatus() {


    }


}
