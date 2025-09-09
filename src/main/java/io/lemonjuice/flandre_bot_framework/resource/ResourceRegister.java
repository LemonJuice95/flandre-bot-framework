package io.lemonjuice.flandre_bot_framework.resource;

import java.util.ArrayList;
import java.util.List;

public class ResourceRegister {
    private final List<Resource<?>> resources = new ArrayList<>();

    public <T> Resource<T> register(Resource<T> resource) {
        resources.add(resource);
        return resource;
    }

    public void load() {
        resources.forEach(Resource::init);
    }
}
