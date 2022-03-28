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
    }



    private ScanRequest getScanRequest() {
        return scanRequest;
    }

    public ScanResult getScanResult() {
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

