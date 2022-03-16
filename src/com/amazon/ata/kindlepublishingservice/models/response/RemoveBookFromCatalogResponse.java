package com.amazon.ata.kindlepublishingservice.models.response;


import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazonaws.services.dynamodbv2.model.*;
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

    private static RemoveBookFromCatalogResponse fromDynamoDb(String id) {
        try {
            CatalogItemVersion item = App.component.provideDynamoDBMapper()
                    .load(CatalogItemVersion.class, id);
            return new RemoveBookFromCatalogResponse(item);
        } catch (AmazonDynamoDBException e) {
            App.logger.info("Error while removing book from catalog", e);
            throw e;
        }
    }

    private ScanRequest getScanRequest() {
        return scanRequest;
    }

    private ScanResult getScanResult() {
        return scanResult;
    }

    public CatalogItemVersion getItem() {
        return item;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
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
        return Objects.equals(getItem(), response.getItem()) && Objects.equals(getScanRequest(), response.getScanRequest()) && Objects.equals(getScanResult(), response.getScanResult());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItem(), getScanRequest(), getScanResult());
    }

}

