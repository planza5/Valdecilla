package com.plm.valdecilla;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.plm.valdecilla.model.App;
import com.plm.valdecilla.model.Node;
import com.plm.valdecilla.model.Path;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author planz
 */
public class GsonUtils {

    public static App fromJson(String json){
        JsonDeserializer<App> deserialization = new JsonDeserializer<App>() {
            HashMap<String, Node> map = new HashMap();

            @Override
            public App deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
                App app = new App();
                JsonObject object = je.getAsJsonObject();
                JsonArray nodes = object.getAsJsonArray("nodes");

                for (JsonElement e : nodes) {
                    JsonObject one=e.getAsJsonObject();
                    String id=one.get("id").getAsString();

                    Node node = new Node();
                    node.id=id;
                    node.name=one.get("name").getAsString();
                    node.subnames=one.get("subnames").getAsString();
                    node.x=one.get("x").getAsFloat();
                    node.y=one.get("y").getAsFloat();

                    map.put(id,node);
                    app.nodes.add(node);
                }

                JsonArray paths = object.getAsJsonArray("paths");

                for (JsonElement e : paths) {
                    JsonObject one=e.getAsJsonObject();
                    Path path=new Path();
                    path.a=map.get(one.get("a").getAsString());
                    path.b=map.get(one.get("b").getAsString());
                    //path.colors.add(one.get("color").);
                    app.paths.add(path);
                }

                return app;
            }
        };

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(App.class, deserialization);
        builder.setPrettyPrinting();

        return builder.create().fromJson(json, App.class);
    }

    public static String  toJson(App app) {

        JsonSerializer<App> serialization = new JsonSerializer<App>() {
            @Override
            public JsonElement serialize(App app, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject object = new JsonObject();

                Map<String, Node> map = new HashMap();
                JsonArray array1 = new JsonArray();

                for (Node node : app.nodes) {
                    map.put(node.id, node);
                    JsonObject one = new JsonObject();
                    one.addProperty("x", node.x);
                    one.addProperty("y", node.y);
                    one.addProperty("name", node.name);
                    one.addProperty("id", node.id);
                    one.addProperty("subnames",node.subnames);
                    array1.add(one);
                }

                object.add("nodes", array1);

                JsonArray array2 = new JsonArray();

                for (Path path : app.paths) {
                    JsonObject one = new JsonObject();
                    one.addProperty("a", path.a.id);
                    one.addProperty("b", path.b.id);
                    //one.addProperty("color", path.color);
                    array2.add(one);
                }

                object.add("paths", array2);

                return object;
            }
        };

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(App.class, serialization);
        builder.setPrettyPrinting();

        return builder.create().toJson(app);


    }

}
