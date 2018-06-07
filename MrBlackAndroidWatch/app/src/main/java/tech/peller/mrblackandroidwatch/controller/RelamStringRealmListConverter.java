package tech.peller.mrblackandroidwatch.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import io.realm.RealmList;
import tech.peller.mrblackandroidwatch.models.RealmString;

public class RelamStringRealmListConverter implements JsonSerializer<RealmList<RealmString>>,
        JsonDeserializer<RealmList<RealmString>> {


    @Override
    public RealmList<RealmString> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        RealmList<RealmString> tags = new RealmList<>();
        JsonArray ja = json.getAsJsonArray();
        for (JsonElement je : ja) {
            tags.add(new RealmString(je.getAsJsonPrimitive().getAsString()));
        }
        return tags;
    }

    @Override
    public JsonElement serialize(RealmList<RealmString> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray ja = new JsonArray();
        for (RealmString tag : src) {
            ja.add(context.serialize(tag.getString()));
        }
        return ja;
    }
}