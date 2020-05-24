package org.sigmoid.configclient;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Serializer {

    private static Gson gson = new Gson();

    public static <T> T jsonToBean(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static Map<String, Object> jsonToMap(String json) {
        Map<String, Object> result = Maps.newHashMap();
        Set<Map.Entry<String, JsonElement>> entrySet = JsonParser.parseString(json).getAsJsonObject().entrySet();
        entrySet.forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    public static <T> List<T> jsonToBeans(String json, Class<T> clazz) {
        final List<T> result = Lists.newArrayList();
        JsonElement jsonElement = JsonParser.parseString(json);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        jsonArray.iterator().forEachRemaining(j -> {
            result.add(gson.fromJson(j, clazz));
        });
        return result;
    }

}
