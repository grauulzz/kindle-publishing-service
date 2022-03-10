package com.amazon.ata.kindlepublishingservice.models.response;


import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.gson.GsonBuilder;

public class RemoveBookFromCatalogResponse {

    private final ScanResult scan;

    public RemoveBookFromCatalogResponse(CatalogItemVersion catalogItemVersion) {
        this.scan = getScanResult(catalogItemVersion);
    }

    private ScanResult getScanResult(CatalogItemVersion cat) {
        ScanRequest scanRequest = new ScanRequest("CatalogItemVersions");
        scanRequest.addScanFilterEntry("bookId", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(cat.getBookId())));
        return DynamoDbClientProvider.getDynamoDBClient().scan(scanRequest);
    }

    public String toJson() {
        String json =  new GsonBuilder().setPrettyPrinting().create().toJson(scan);
        String cyan = "\u001B[36m";
        String reset = "\u001B[0m";
        return json.replaceAll("",  cyan) + reset;
    }

}






//    @Override
//    public String toString() {
//        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
//    }

//    private List<Serializable> getCatalogItemVersion(CatalogItemVersion catalogItemVersion) {
//        String active = catalogItemVersion.isInactive() ? "inactive" : "active";
//        return Arrays.asList(catalogItemVersion.getBookId(), catalogItemVersion.getVersion(), active);
//    }
//















//    public List<String> getCoolResponse() {
//        return Arrays.stream(new String[]{new GsonBuilder().disableHtmlEscaping()
//                .create().toJson( response)
//                                                  .replaceAll("[\\[]",
//                String.format("%s%s", "\n [", "\u001B[36m"))}).collect(Collectors.toList());
//    }
//
//    public JsonElement getResponseElement() {
//        return response;
//    }
//
//    public String generateResponseString() {
//        return new GsonBuilder().disableHtmlEscaping()
//                .create().toJson(response);
//    }














//
//package com.amazon.ata.kindlepublishingservice.models.response;
//
//        import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
//        import com.amazon.ata.kindlepublishingservice.models.Book;
//        import java.util.List;
//        import java.util.Objects;
//
//public class RemoveBookFromCatalogResponse {
//    private Book book;
//    private List<CatalogItemVersion> catalogItems;
//
//    public RemoveBookFromCatalogResponse(Book book, List<CatalogItemVersion> catalogItems) {
//        this.book = book;
//        this.catalogItems = catalogItems;
//    }
//
//    public RemoveBookFromCatalogResponse() {
//
//    }
//
//    public Book getBook() {
//        return book;
//    }
//
//    public List<CatalogItemVersion> getCatalogItems() {
//        return catalogItems;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        RemoveBookFromCatalogResponse response = (RemoveBookFromCatalogResponse) o;
//        return getBook().equals(response.getBook()) && getCatalogItems().equals(response.getCatalogItems());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getBook(), getCatalogItems());
//    }
//
//    @Override
//    public String toString() {
//        return "RemoveBookFromCatalogResponse{" +
//                       "book=" + book +
//                       ", catalogItems=" + catalogItems +
//                       '}';
//    }
//
//    public RemoveBookFromCatalogResponse(Builder builder) {
//        this.book = builder.book;
//        this.catalogItems = builder.catalogItems;
//    }
//
//
//    public static Builder builder() {
//        return new Builder();
//    }
//
//    public static final class Builder {
//        private Book book;
//        private List<CatalogItemVersion> catalogItems;
//
//        private Builder() {
//        }
//
//        public Builder withBook(Book book) {
//            this.book = book;
//            return this;
//        }
//
//        public Builder withCatalogItems(List<CatalogItemVersion> catalogItems) {
//            this.catalogItems = catalogItems;
//            return this;
//        }
//
//        public RemoveBookFromCatalogResponse build() {
//            return new RemoveBookFromCatalogResponse(this);
//        }
//    }
//
//
//}