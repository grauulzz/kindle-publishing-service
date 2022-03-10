import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.activity.RemoveBookFromCatalogActivity;
import com.amazon.ata.kindlepublishingservice.converters.CatalogItemConverter;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.models.Book;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.gson.GsonBuilder;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveBookTest {
    private final DynamoDBMapper mapper = new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient());
    private CatalogItemVersion catalogItemVersion;
    private final String exsitingId = "book.ac510a76-008c-4478-b9f3-c277d74fa305";
    String nonExistingId = "book.nonExistingId";
    private final Book book = new Book(Book.builder().withBookId(exsitingId));
    private Book catalogToBook;


    @BeforeEach
    void setUp() {
        catalogItemVersion = new CatalogItemVersion();
        catalogItemVersion.setBookId("bookId");
        catalogItemVersion.setTitle("title");
        catalogItemVersion.setAuthor("author");
        catalogItemVersion.setText("text");
        catalogItemVersion.setGenre(BookGenre.AUTOBIOGRAPHY);
        catalogItemVersion.setVersion(1);
        catalogItemVersion.setInactive(false);
        catalogToBook = CatalogItemConverter.toBook(catalogItemVersion);
    }

    private static void shitJsonFormatter(String consoleOut) {
        Arrays.stream(new String[]{new GsonBuilder().disableHtmlEscaping()
                        .create().toJson( consoleOut)
                                           .replaceAll("[\\[]",
                        String.format("%s%s", "\n [", "\u001B[36m"))})
                .forEach(System.out::println);
    }

    @Test
    void removeBookTest() {
        RemoveBookFromCatalogRequest req = RemoveBookFromCatalogRequest.builder()
                .withBookId("book.ac510a76-008c-4478-b9f3-c277d74fa305").build();

        RemoveBookFromCatalogActivity rmBookActivity = new RemoveBookFromCatalogActivity();
        RemoveBookFromCatalogResponse response = rmBookActivity.execute(req);

        System.out.println(response.toJson());
    }


}
