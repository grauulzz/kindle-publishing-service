package com.amazon.ata.kindlepublishingservice.models.response;

import com.google.gson.GsonBuilder;

/**
 * The type Format response.
 */
public class FormatResponse {

    /**
     * To json with color string.
     *
     * @param obj the obj
     *
     * @return the string
     */
    public static String toJsonWithColor(Object obj) {
        String cyan = "\u001B[36m";
        String reset = "\u001B[0m";
        return new GsonBuilder().setPrettyPrinting().create()
                .toJson(obj).replaceAll("", cyan) + reset;
    }

    /**
     * To json string.
     *
     * @param obj the obj
     *
     * @return the string
     */
    public static String toJson(Object obj) {
        return new GsonBuilder().setPrettyPrinting().create()
                .toJson(obj);
    }

    private static RemoveBookFromCatalogResponse fromJson(String json) {
        return new GsonBuilder().create().fromJson(json, RemoveBookFromCatalogResponse.class);
    }

    /**
     * To json string.
     *
     * @return the string
     */
    public String toJson() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(this);
    }

}