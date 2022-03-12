package com.amazon.ata.kindlepublishingservice.models.response;

import com.google.gson.GsonBuilder;

public class FormatResponse {
    public String toJson(Object obj) {
        String cyan = "\u001B[36m";
        String reset = "\u001B[0m";
        return new GsonBuilder().setPrettyPrinting().create()
                .toJson(obj).replaceAll("",  cyan) + reset;
    }
}