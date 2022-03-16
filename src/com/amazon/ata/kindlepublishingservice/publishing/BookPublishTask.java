package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.controllers.Controller;
import com.amazon.ata.kindlepublishingservice.models.Book;
import org.springframework.http.ResponseEntity;

public class BookPublishTask implements Runnable {
    private final BookPublishingManager context;

    public BookPublishTask(BookPublishingManager context) {
        this.context = context;
    }


    public ResponseEntity<?> publish(Book book) {
        Controller controller = new Controller();
        return controller.submitBookForPublishing(book);
    }

    @Override
    public void run() {
        App.logger.info("BookPublishTask started");
        while (true) {
            Book book = context.getNextBook();
            if (book != null) {
                publish(book);
            } else {
                App.logger.info("BookPublishTask finished");
                break;
            }
        }
    }
}
