package com.amazon.ata.kindlepublishingservice.models.response;


import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.gson.GsonBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RemoveBookFromCatalogResponse {

    private final CatalogItemVersion item;
    private final ScanRequest scanRequest;
    private ScanResult scanResult;

    public RemoveBookFromCatalogResponse(CatalogItemVersion item) {
        this.item = item;
        this.scanRequest = new ScanRequest("CatalogItemVersions");
        this.processScanRequest();
    }

    private void processScanRequest() {
        this.scanRequest.addScanFilterEntry("bookId", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(this.item.getBookId())));

        this.scanResult = DynamoDbClientProvider.getDynamoDBClient().scan(this.scanRequest);
    }

    private void processScanRequestWithAttributeList(List<AttributeValue> attributeList) {

        this.scanRequest.addScanFilterEntry("bookId", new Condition()
                    .withComparisonOperator(ComparisonOperator.EQ)
                    .withAttributeValueList(attributeList));

        this.scanResult = DynamoDbClientProvider.getDynamoDBClient().scan(this.scanRequest);
    }

    public CatalogItemVersion getItem() {
        return this.item;
    }

    public String toJson() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(this);
    }

    public String toJsonWithColor() {
        String green = "\u001B[32m";
        String cyan = "\u001B[36m";
        String magenta = "\u001B[35m";
        String reset = "\u001B[0m";

        String item = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                .toJson(this.item).replaceAll("", magenta) + reset;

        String scanRequest = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                .toJson(this.scanRequest).replaceAll("", green) + reset;

        String scanResult = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                .toJson(this.scanResult).replaceAll("", cyan) + reset;

        return item + "\n" + scanRequest + "\n" + scanResult;
    }

    public String toJsonWithHttpMetaData() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(this);
    }

    private static RemoveBookFromCatalogResponse fromJson(String json) {
        return new GsonBuilder().create().fromJson(json, RemoveBookFromCatalogResponse.class);
    }

    private static RemoveBookFromCatalogResponse fromDynamoDb(String id) {
        try {
            CatalogItemVersion item = App.component.provideDynamoDBMapper()
                    .load(CatalogItemVersion.class, id);
            return new RemoveBookFromCatalogResponse(item);
        } catch (AmazonDynamoDBException e) {
            App.logger.error("Error while removing book from catalog", e);
            throw e;
        }
    }

    private ScanRequest getScanRequest() {
        return scanRequest;
    }

    private ScanResult getScanResult() {
        return scanResult;
    }


    @Override
    public String toString() {
        return "RemoveBookFromCatalogResponse{" +
                       "item=" + item +
                       ", scanRequest=" + scanRequest +
                       ", scanResult=" + scanResult +
                       '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoveBookFromCatalogResponse response = (RemoveBookFromCatalogResponse) o;
        return Objects.equals(getItem(), response.getItem()) &&
                       Objects.equals(getScanRequest(), response.getScanRequest()) &&
                       Objects.equals(getScanResult(), response.getScanResult());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItem(), getScanRequest(), getScanResult());
    }
}

//    public static RemoveBookFromCatalogResponse.Builder builder() {return new RemoveBookFromCatalogResponse.Builder();}
//
//public static final class Builder {
//    private CatalogItemVersion catalogItem;
//
//    private Builder() {
//    }
//
//    public Builder withCatalogItem(CatalogItemVersion catalogItem) {
//        this.catalogItem = catalogItem;
//        return this;
//    }
//
//    public String withJsonFormattedResponse() {
//        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
//                .toJson(this);
//    }
//
//    public String withJsonFormattedMetaData() {
//        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
//                .toJson(this.build().processScanRequest().getSdkResponseMetadata());
//    }
//
//    public RemoveBookFromCatalogResponse build() {
//        return new RemoveBookFromCatalogResponse(this.catalogItem);
//    }
//}
