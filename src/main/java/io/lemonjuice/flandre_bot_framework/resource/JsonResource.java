package io.lemonjuice.flandre_bot_framework.resource;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class JsonResource extends Resource<JSONObject> {
    public JsonResource(String path) {
        super(path, new JSONObject());
    }

    @Override
    protected JSONObject load(InputStream input) {
        JSONTokener jsonTokener = new JSONTokener(input);
        return new JSONObject(jsonTokener);
    }
}
