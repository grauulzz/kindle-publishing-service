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

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class Controller {
    private static final ApplicationComponent component = App.component;
    private static final Logger LOGGER = LogManager.getLogger(Controller.class);

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
    /**
     * Gets book.
     *
     * @param id the id
     *
     * @return the book
     */
    @Timed(value = "get.book", description = "Time taken to return book")
    @GetMapping(value = "/books/{id}", produces = "application/json")
    public ResponseEntity<?> getBook(@PathVariable String id) {
        LOGGER.info("Getting book with id: {}", id);
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
        LOGGER.info("Removing book with id: {}", id);
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
        LOGGER.info("Submitting book for publishing: {}", book);
        SubmitBookForPublishingActivity activity = component.provideSubmitBookForPublishingActivity();
        SubmitBookForPublishingRequest req = SubmitBookForPublishingRequest.builder().withBook(book).build();
        return new ResponseEntity<>(activity.execute(req), HttpStatus.OK);
    }

}

