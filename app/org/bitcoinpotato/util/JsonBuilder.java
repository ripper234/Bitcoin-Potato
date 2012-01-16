package org.bitcoinpotato.util;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class JsonBuilder {
    private final JsonObject json = new JsonObject();
    private final Map<String, JsonBuilder> children = newHashMap();

    public String toJson() {
        for (Map.Entry<String, JsonBuilder> entry : children.entrySet()) {
            String value = entry.getValue().toJson();
            add(entry.getKey(), value);
        }
        children.clear();

        return json.toString();
    }

    public JsonBuilder add(String key, String value) {
        json.addProperty(key, value);
        return this;
    }

    public JsonBuilder add(String key, JsonBuilder value) {
        Preconditions.checkArgument(!children.containsKey(key));
        children.put(key, value);
        return this;
    }
}
