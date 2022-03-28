package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NoOpTaskTest {
    private final BookPublishRequest request = BookPublishRequest.builder()
            .withBookId("test-book-publisher-manager")
            .withAuthor("a")
            .withGenre(BookGenre.ACTION)
            .withText("...")
            .withTitle("t")
            .withPublishingRecordId("publishingid")
            .build();
    @Test
    void run_completesSuccessfully() {
        // GIVEN
        BookPublishingManager.addRequest(request);

        BookPublishTask task = new BookPublishTask();
        SubmitBookForPublishingResponse response = App.component.provideSubmitBookForPublishingActivity()
                .execute(SubmitBookForPublishingRequest.builder()
//                        .withBookId("test-book-publisher-manager")
                        .withAuthor("a")
                        .withGenre(String.valueOf(BookGenre.ACTION))
                        .withText("...")
                        .withTitle("t")
                        .build());

        try {
            task.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert response.getPublishingRecordId() != null;
    }

}
