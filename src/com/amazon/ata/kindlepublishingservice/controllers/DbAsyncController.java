package com.amazon.ata.kindlepublishingservice.controllers;

import com.amazon.ata.kindlepublishingservice.activity.SubmitBookForPublishingActivity;
import com.amazon.ata.kindlepublishingservice.models.Book;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import java.util.concurrent.CompletableFuture;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import static com.amazon.ata.kindlepublishingservice.App.component;

public class DbAsyncController {
    private static CompletableFuture<ResponseEntity<?>> future;

    @Async
    @PostMapping(value = "/books")
    public CompletableFuture<ResponseEntity<?>> submitBookForPublishing(@Valid @RequestBody SubmitBookForPublishingRequest request) {
        CompletableFuture.supplyAsync(() -> {
            SubmitBookForPublishingActivity activity = new SubmitBookForPublishingActivity();
            SubmitBookForPublishingResponse response = activity.execute(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        });
        return future;
    }

    @Async
    @PostMapping(value = "/books", consumes = {"application/json"}, produces = {"application/json"})
    public CompletableFuture<ResponseEntity<?>> submitBookForPublishing(@Valid @RequestBody Book book) {
        SubmitBookForPublishingActivity activity = component.provideSubmitBookForPublishingActivity();
        SubmitBookForPublishingRequest req = SubmitBookForPublishingRequest.builder().withBook(book).build();
        future = CompletableFuture.completedFuture(new ResponseEntity<>(activity.execute(req),
                HttpStatus.OK));
        return future;
    }

    public static CompletableFuture<String> asyncGetIdFromSubmitResponse() {
        return future.thenApply(response -> {
            SubmitBookForPublishingResponse submissionResponse = (SubmitBookForPublishingResponse) response.getBody();
            return submissionResponse != null ? submissionResponse.getPublishingRecordId() : null;
        });
    }
}
