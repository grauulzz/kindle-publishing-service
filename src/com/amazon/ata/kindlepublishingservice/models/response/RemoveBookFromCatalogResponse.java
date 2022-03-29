package com.amazon.ata.kindlepublishingservice.models.response;


import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import java.util.Objects;

/**
 * The type Remove book from catalog response.
 */
public class RemoveBookFromCatalogResponse {


    private final CatalogItemVersion item;
    private final ScanRequest scanRequest;
    private ScanResult scanResult;

    /**
     * Instantiates a new Remove book from catalog response.
     *
     * @param item the item
     */
    public RemoveBookFromCatalogResponse(CatalogItemVersion item) {
        this.item = item;
        this.scanRequest = new ScanRequest("CatalogItemVersions");
    }

    public int getStatusCode() {
        return scanResult.getSdkHttpMetadata().getHttpStatusCode();
    }


    private ScanRequest getScanRequest() {
        return scanRequest;
    }

    /**
     * Gets scan result.
     *
     * @return the scan result
     */
    public ScanResult getScanResult() {
        return scanResult;
    }

    /**
     * Gets item.
     *
     * @return the item
     */
    public CatalogItemVersion getItem() {
        return item;
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

