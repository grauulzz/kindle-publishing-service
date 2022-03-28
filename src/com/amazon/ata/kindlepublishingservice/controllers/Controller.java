package com.amazon.ata.kindlepublishingservice.controllers;

import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.activity.GetBookActivity;
import com.amazon.ata.kindlepublishingservice.activity.RemoveBookFromCatalogActivity;
import com.amazon.ata.kindlepublishingservice.activity.SubmitBookForPublishingActivity;
import com.amazon.ata.kindlepublishingservice.dagger.ApplicationComponent;
import com.amazon.ata.kindlepublishingservice.models.Book;
import com.amazon.ata.kindlepublishingservice.models.requests.GetBookRequest;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {
    private static final ApplicationComponent component = App.component;


    @GetMapping(value = "/books/{id}", produces = {"application/json"})
    public ResponseEntity<?> getBook(@PathVariable String id) {
        GetBookActivity bookActivity = component.provideGetBookActivity();
        GetBookRequest getBookRequest = GetBookRequest.builder().withBookId(id).build();
        return new ResponseEntity<>(bookActivity.execute(getBookRequest), HttpStatus.OK);
    }

    @DeleteMapping( value = "/books/{id}", produces = {"application/json"})
    public ResponseEntity<?> removeBook(@PathVariable String id) {
        RemoveBookFromCatalogActivity removeBookFromCatalogActivity = component.provideRemoveBookFromCatalogActivity();
        RemoveBookFromCatalogRequest removeBookFromCatalogRequest = RemoveBookFromCatalogRequest.builder()
                .withBookId(id)
                .build();

        return new ResponseEntity<>(removeBookFromCatalogActivity
                .execute(removeBookFromCatalogRequest), HttpStatus.OK);
    }

    @PostMapping (value = "/books", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> submitBookForPublishing(@Valid @RequestBody Book book) {
        SubmitBookForPublishingActivity activity = component.provideSubmitBookForPublishingActivity();
        SubmitBookForPublishingRequest req = SubmitBookForPublishingRequest.builder().withBook(book).build();
        return new ResponseEntity<>(activity.execute(req), HttpStatus.OK);
    }
}
//    @Async
//    @PostMapping(value = "/books")
//    public CompletableFuture<ResponseEntity<SubmitBookForPublishingResponse>> submitBookForPublishing(
//            @Valid @RequestBody SubmitBookForPublishingRequest request)
//    {
//        return CompletableFuture.supplyAsync(() -> {
//            SubmitBookForPublishingActivity activity = new SubmitBookForPublishingActivity();
//            SubmitBookForPublishingResponse response = activity.execute(request);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        });
//    }