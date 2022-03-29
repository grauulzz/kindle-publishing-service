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
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {
    private static final ApplicationComponent component = App.component;

    /**
     * Gets book.
     *
     * @param id the id
     *
     * @return the book
     */
    @GetMapping(value = "/books/{id}", produces = "application/json")
    public ResponseEntity<?> getBook(@PathVariable String id) {
        GetBookActivity bookActivity = component.provideGetBookActivity();
        GetBookRequest getBookRequest = GetBookRequest.builder().withBookId(id).build();
        return new ResponseEntity<>(bookActivity.execute(getBookRequest), HttpStatus.OK);
    }

    /**
     * Remove book response entity.
     *
     * @param id the id
     *
     * @return the response entity
     */
    @DeleteMapping(value = "/books/{id}", produces = "application/json")
    public ResponseEntity<?> removeBook(@PathVariable String id) {
        RemoveBookFromCatalogActivity removeBookFromCatalogActivity = component.provideRemoveBookFromCatalogActivity();
        RemoveBookFromCatalogRequest removeBookFromCatalogRequest = RemoveBookFromCatalogRequest.builder()
                .withBookId(id)
                .build();

        return new ResponseEntity<>(removeBookFromCatalogActivity
                .execute(removeBookFromCatalogRequest), HttpStatus.OK);
    }

    /**
     * Submit book for publishing response entity.
     *
     * @param book the book
     *
     * @return the response entity
     */
    @PostMapping(value = "/books", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> submitBookForPublishing(@Valid @RequestBody Book book) {
        SubmitBookForPublishingActivity activity = component.provideSubmitBookForPublishingActivity();
        SubmitBookForPublishingRequest req = SubmitBookForPublishingRequest.builder().withBook(book).build();
        return new ResponseEntity<>(activity.execute(req), HttpStatus.OK);
    }
}
