import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperTableModel;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.amazon.ata.kindlepublishingservice.App.*;

class TableModelTest {
    private final DynamoDBMapper mapper = component.provideDynamoDBMapper();
    private CatalogItemVersion catalogItemVersion;
    private final String exsitingIdMutated = "book.bd621b87-119d-5589-c0g4-d388f85gb417";

    @BeforeEach
    void setUp() {
        this.catalogItemVersion = new CatalogItemVersion();
        String exsitingId = "book.ac510a76-008c-4478-b9f3-c277d74fa305";
        catalogItemVersion.setBookId(exsitingId);
    }

    @Test
    void marshalToObject() {
        print("starting obj: ", this.catalogItemVersion);
        DynamoDBMapperTableModel<CatalogItemVersion> tableModel = this.mapper
                .getTableModel(CatalogItemVersion.class);

        Map<String, AttributeValue> catalogItemAttributeMap = tableModel.convert(this.catalogItemVersion);
        catalogItemAttributeMap.put("bookId", new AttributeValue().withS(exsitingIdMutated));
        catalogItemAttributeMap.put("version", new AttributeValue().withN("1"));
        catalogItemAttributeMap.put("inactive", new AttributeValue().withBOOL(true));
        catalogItemAttributeMap.put("title", new AttributeValue().withS("title"));
        catalogItemAttributeMap.put("author", new AttributeValue().withS("author"));
        catalogItemAttributeMap.put("text", new AttributeValue().withS(  "cool-book-text"));
        catalogItemAttributeMap.put("genre", new AttributeValue().withS(String.valueOf(BookGenre.HORROR)));

        // this updates the catalog item version with marshalled data from catalog item attribute map
        this.catalogItemVersion = this.mapper.marshallIntoObject(CatalogItemVersion.class,
                catalogItemAttributeMap);

        print("marshalled obj: ", this.catalogItemVersion);
    }

    @Test
    void convertCatalogItemAttributeMapAndBackAgain() {

        DynamoDBMapperTableModel<CatalogItemVersion> tableModel = this.mapper.getTableModel(CatalogItemVersion.class);

        // starting catalog item
        print("catalog item: ", this.catalogItemVersion);

        // convert catalog item to attribute values what can be marshalled to dynamo
        Map<String, AttributeValue> catalogItemAttributeMap = tableModel.convert(this.catalogItemVersion);
        print("convert obj into Map<String, Attribute>: ", catalogItemAttributeMap);

        // change the book id by adding new values to the attribute map
        catalogItemAttributeMap.put("bookId", new AttributeValue().withS(exsitingIdMutated));
        catalogItemAttributeMap.put("version", new AttributeValue().withN("1"));
        catalogItemAttributeMap.put("inactive", new AttributeValue().withBOOL(true));

        // un-convert attribute values back to catalog item (should have the new book id)
        this.catalogItemVersion = tableModel.unconvert(catalogItemAttributeMap);
        print("un-convert: ", this.catalogItemVersion);

    }


    @Test
    void tableModel() {
        DynamoDBMapperTableModel<CatalogItemVersion> tableModel = component.provideDynamoDBMapper()
                .getTableModel(CatalogItemVersion.class);

        // these are all possible <string, attribute> values for the CatalogItemVersion object passed in
        List<Map<String, AttributeValue>> allPossibleValues = new ArrayList<>();
        tableModel.convert(catalogItemVersion).forEach((k, v) -> {
            allPossibleValues.add(new HashMap<>() {{
                put(k, v);
                print("(k, v) => ",  k + " " + v);
            }});
            allPossibleValues.forEach(map -> {
                tableModel.unconvert(map);
                print("Unconverted: ",  map.values());
            });
        });
    }

    @Test
    void scans() {
        // list all items in "catalogItemVersions" table
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient().scan(
                new ScanRequest("CatalogItemVersions"));

        List<Map<String, AttributeValue>> items = result.getItems();

        items.stream().filter(item -> item.containsKey(
                "book.b3750190-2a30-4ca8-ae1b-73d0d202dc41")
        ).forEach(item -> {
            print("item: ", item);
        });



        List<Map<String, AttributeValue>> beefy = items.stream().map(item -> {
            Map<String, AttributeValue> map = new HashMap<>();
                map.put("bookId", item.get("bookId"));
                map.put("author", item.get("author"));
                map.put("title", item.get("title"));
                map.put("genre", item.get("genre"));
                map.put("version", item.get("version"));
                map.put("inactive", item.get("inactive"));
                return map;
        }).collect(Collectors.toList());

        print("beefy: ", beefy);

    }

    @Test
    void tableModelBatchConvert() {
        DynamoDBMapperTableModel<CatalogItemVersion> tableModel = component.provideDynamoDBMapper()
                .getTableModel(CatalogItemVersion.class);

        List<Map<String, AttributeValue>> allPossibleValues = new ArrayList<>();
        tableModel.convert(catalogItemVersion).forEach((k, v) -> {
            allPossibleValues.add(new HashMap<>() {{
                put(k, v);
            }});
        });

        // can get stuff like this
        List<String> keySetList = allPossibleValues.stream().flatMap(m -> m.keySet().stream())
                .collect(Collectors.toList());

        List<AttributeValue> attributeValuesList = allPossibleValues.stream().flatMap(m -> m.values().stream())
                .collect(Collectors.toList());

        allPossibleValues.forEach(map -> {
            CatalogItemVersion unconvert = tableModel.unconvert(map);
            print("unconverted: ", unconvert);
        });

        // or
        print("allPossibleValues Converted: ", allPossibleValues.stream()
                .map(tableModel::unconvert).collect(Collectors.toList()));
    }

    void print(String description, Object obj){
        String green = "\u001B[32m";
        String cyan = "\u001B[36m";
        String reset = "\u001B[0m";
        String magenta = "\u001B[35m";
        // multiply dash characters times the length of the description
        String totalDescription = green + description + reset + " " + obj;
        String dash = String.join("",
                Collections.nCopies(totalDescription.length() - reset.length() - green.length(), "-"));

        System.out.println(magenta + dash + reset);

        System.out.println(green + description + reset + " " + obj);
        System.out.println(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                .toJson(obj).replaceAll("",  cyan) + reset);

    }

    public void batchDeleteInactiveVersionsOfTheSameId(CatalogItemVersion cat) {
        HashMap<String, Condition> scanFilter = new HashMap<>();
        scanFilter.put("bookId", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue()
                        .withS(cat.getBookId())));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withScanFilter(scanFilter);

        PaginatedScanList<CatalogItemVersion> sr = mapper.scan(CatalogItemVersion.class,
                scanExpression);

        sr.removeIf(p -> cat.isInactive());
        mapper.batchDelete(sr);
    }

    public void saveItem(CatalogItemVersion version) {
        mapper.save(version);
    }

    public List<CatalogItemVersion> getCatalogItems() {
        ScanResult result = DynamoDbClientProvider.getDynamoDBClient().scan(
                new ScanRequest("CatalogItemVersions"));

        return result.getItems().stream()
                .map(item -> mapper.marshallIntoObject(CatalogItemVersion.class,
                        item))
                .collect(Collectors.toList());
    }
}
