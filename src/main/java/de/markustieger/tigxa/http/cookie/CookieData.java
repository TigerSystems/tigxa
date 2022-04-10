package de.markustieger.tigxa.http.cookie;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CookieData {

    public Map<String, Map<Cookie, Cookie>> buckets =
            new HashMap<String, Map<Cookie, Cookie>>();

    public static class CookieDataSerializer implements JsonSerializer<CookieData>, JsonDeserializer<CookieData> {

        private final Gson GSON = new GsonBuilder().create();

        @Override
        public JsonElement serialize(CookieData src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject obj = new JsonObject();

            for (Map.Entry<String, Map<Cookie, Cookie>> e : src.buckets.entrySet()) {
                obj.add(e.getKey(), serialize(e.getValue()));
            }

            return obj;
        }

        private JsonElement serialize(Map<Cookie, Cookie> data) {

            JsonArray array = new JsonArray();

            for (Map.Entry<Cookie, Cookie> e : data.entrySet()) {

                JsonObject obj = new JsonObject();
                obj.add("key", GSON.toJsonTree(e.getKey()));
                obj.add("value", GSON.toJsonTree(e.getValue()));
                array.add(obj);
            }

            return array;

        }


        @Override
        public CookieData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            try {

                CookieData data = new CookieData();

                JsonObject obj = json.getAsJsonObject();

                for (Map.Entry<String, JsonElement> e : obj.entrySet()) {

                    data.buckets.put(e.getKey(), deserialize(e.getValue()));

                }

                return data;

            } catch (Throwable e) {
                throw new JsonParseException(e);
            }
        }

        private Map<Cookie, Cookie> deserialize(JsonElement element) throws Throwable {

            HashMap<Cookie, Cookie> data = new HashMap<>();

            JsonArray array = element.getAsJsonArray();

            for (int i = 0; i < array.size(); i++) {

                JsonObject entry = array.get(i).getAsJsonObject();

                Cookie key = GSON.fromJson(entry.get("key"), Cookie.class);
                Cookie value = GSON.fromJson(entry.get("value"), Cookie.class);

                data.put(key, value);

            }

            return data;

        }
    }

}
