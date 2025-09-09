package io.lemonjuice.flandre_bot_framework.resource;

import io.lemonjuice.flandre_bot_framework.utils.CQCode;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class HelpDocResource extends Resource<List<String>> {

    public HelpDocResource(String docPath) {
        super(docPath, Collections.unmodifiableList(new ArrayList<>()));
    }

    @Override
    protected List<String> load(InputStream input) throws IOException {
        Pattern image_pattern = Pattern.compile("^<(local|net)Image:(\\S+)>$");
        List<String> contents = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            List<String> contentsRaw = bufferedReader.lines().toList();

            StringBuilder reading = new StringBuilder();
            for(String c : contentsRaw) {
                if(c.equals("<split>") && !reading.isEmpty()) {
                    contents.add(reading.toString().trim());
                    reading = new StringBuilder();
                } else if(image_pattern.matcher(c).find()) {
                    Matcher matcher = image_pattern.matcher(c);
                    matcher.find();
                    String imageSource = matcher.group(1);
                    String imagePath = matcher.group(2);
                    if(imageSource.equals("local")) {
                        File imageFile = new File(imagePath);
                        if (imageFile.exists()) {
                            reading.append(CQCode.image("file:///" + imageFile.getAbsolutePath()));
                        } else {
                            log.warn("找不到帮助文档中的本地图片: {}", imagePath);
                        }
                    } else {
                        reading.append(CQCode.image(imagePath));
                    }
                } else {
                    reading.append(c).append("\n");
                }
            }
            if(!reading.toString().trim().isEmpty()) {
                contents.add(reading.toString().trim());
            }
        }

        return Collections.unmodifiableList(contents);
    }
}
