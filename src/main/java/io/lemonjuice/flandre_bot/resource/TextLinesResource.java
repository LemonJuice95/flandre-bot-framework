package io.lemonjuice.flandre_bot.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextLinesResource extends Resource<List<String>> {
    public TextLinesResource(String path) {
        super(path, Collections.unmodifiableList(new ArrayList<>()));
    }

    @Override
    protected List<String> load(InputStream input) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.lines().toList();
        }
    }
}
