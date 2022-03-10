
import com.amazon.ata.kindlepublishingservice.activity.RemoveBookFromCatalogActivity;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RemoveBookTest {

    private final String exsitingId = "book.ac510a76-008c-4478-b9f3-c277d74fa305";
    private final String nonExistingId = "book.nonExistingId";


    @Test
    void removeBookResponseGoodRequest() {
        RemoveBookFromCatalogRequest req = RemoveBookFromCatalogRequest.builder()
                .withBookId(exsitingId).build();
        CatalogItemVersion catalogItemVersion = new CatalogItemVersion();
        catalogItemVersion.setBookId(exsitingId);

        RemoveBookFromCatalogActivity rmBookActivity = new RemoveBookFromCatalogActivity();
        RemoveBookFromCatalogResponse response = rmBookActivity.execute(req);

        ScanResult scanResult = response.processScanRequest();
        System.out.println(response.toJson());
        assert scanResult.getSdkHttpMetadata().getHttpStatusCode() == 200;

    }

    @Test
    void badResponseThrowsException() {
        RemoveBookFromCatalogRequest req = RemoveBookFromCatalogRequest.builder()
                .withBookId(nonExistingId).build();

        RemoveBookFromCatalogActivity rmBookActivity = new RemoveBookFromCatalogActivity();

        Assertions.assertThrows(BookNotFoundException.class, () -> {
            rmBookActivity.execute(req);
        });
    }



}
