package com.amazon.ata.kindlepublishingservice.models.response;


import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.gson.GsonBuilder;
import java.util.HashMap;

public class RemoveBookFromCatalogResponse {

    private final ScanRequest scanRequest;

    private final CatalogItemVersion cat;
    private final DynamoDBMapper dynamoDBMapper = App.component.provideDynamoDBMapper();
    private final HashMap<String, Condition> scanFilter = new HashMap<>();

    public RemoveBookFromCatalogResponse(CatalogItemVersion catalogItemVersion) {
        this.cat = catalogItemVersion;
        this.scanRequest = new ScanRequest("CatalogItemVersions");
    }

    private boolean removeInactiveBook() {
        this.scanFilter.put("bookId", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue()
                        .withS(cat.getBookId())));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withScanFilter(scanFilter);
        return dynamoDBMapper.scan(CatalogItemVersion.class, scanExpression)
                .removeIf(CatalogItemVersion::isInactive);
    }


    public ScanResult processScanRequest() {
        ScanRequest scanRequest = new ScanRequest("CatalogItemVersions");
        scanRequest.addScanFilterEntry("bookId", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(cat.getBookId())));
        return DynamoDbClientProvider.getDynamoDBClient().scan(scanRequest);
    }

    public ScanResult processAnyScanRequest(String tableName, String filterEntry,
                                            String AttributeVal) {
        try {
            ScanRequest scanRequest = new ScanRequest(tableName);
            scanRequest.addScanFilterEntry(filterEntry, new Condition()
                    .withComparisonOperator(ComparisonOperator.EQ)
                    .withAttributeValueList(new AttributeValue().withS(AttributeVal)));
            return DynamoDbClientProvider.getDynamoDBClient().scan(scanRequest);
        } catch (AmazonDynamoDBException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String toJson() {
        String reqJson =  new GsonBuilder().setPrettyPrinting()
                .create().toJson(processScanRequest());
        String cyan = "\u001B[36m";
        String reset = "\u001B[0m";
        return reqJson.replaceAll("",  cyan) + reset;
    }

}

//    private List<Serializable> getCatalogItemVersion(CatalogItemVersion catalogItemVersion) {
//        String active = catalogItemVersion.isInactive() ? "inactive" : "active";
//        return Arrays.asList(catalogItemVersion.getBookId(), catalogItemVersion.getVersion(), active);
//    }
//
