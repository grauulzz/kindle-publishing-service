package com.amazon.ata.kindlepublishingservice.models.response;

import com.google.gson.GsonBuilder;

public class FormatResponse {

    public static String toJsonWithColor(Object obj) {
        String cyan = "\u001B[36m";
        String reset = "\u001B[0m";
        return new GsonBuilder().setPrettyPrinting().create()
                .toJson(obj).replaceAll("",  cyan) + reset;
    }

    public static String toJson(Object obj) {
        return new GsonBuilder().setPrettyPrinting().create()
                .toJson(obj);
    }

    public String toJson() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(this);
    }

    private static RemoveBookFromCatalogResponse fromJson(String json) {
        return new GsonBuilder().create().fromJson(json, RemoveBookFromCatalogResponse.class);
    }

}