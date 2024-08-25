package org.trips.service_framework.audit.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.javers.core.json.JsonTypeAdapter;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author anomitra on 16/08/24
 */

@Component
public class JSONObjectAdapter implements JsonTypeAdapter<JSONObject> {

    private final Gson gson;
    JSONObjectAdapter() {
        this.gson = new Gson();
    }

    @Override
    public JSONObject fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        return gson.fromJson(json, JSONObject.class);
    }

    @Override
    public JsonElement toJson(JSONObject sourceValue, JsonSerializationContext jsonSerializationContext) {
        return gson.toJsonTree(sourceValue, JSONObject.class);
    }

    @Override
    public List<Class> getValueTypes() {
        return List.of(JSONObject.class);
    }
}
