package ru.yandex.yamblz.callinformer.util;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.yandex.yamblz.callinformer.model.SearchResult;

/**
 * Created by root on 7/18/16.
 */
public class GoogleSearchJSONDeserializer implements JsonDeserializer<ArrayList<SearchResult>> {
    @Override
    public ArrayList<SearchResult> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Gson().fromJson(json.getAsJsonObject().get("items"), new TypeToken<ArrayList<SearchResult>>() {
        }.getType());
    }
}
