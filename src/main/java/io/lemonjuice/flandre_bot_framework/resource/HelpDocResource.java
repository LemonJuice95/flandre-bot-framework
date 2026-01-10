package io.lemonjuice.flandre_bot_framework.resource;

import io.lemonjuice.flandre_bot_framework.message.segment.ImageMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class HelpDocResource extends Resource<List<List<MessageSegment>>> {

    public HelpDocResource(String docPath) {
        super(docPath, Collections.unmodifiableList(new ArrayList<>()));
    }

    @Override
    protected List<List<MessageSegment>> load(InputStream input) throws IOException {
        Pattern image_pattern = Pattern.compile("^<(local|net)Image:(\\S+)>$");
        List<List<MessageSegment>> contents = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            List<String> contentsRaw = bufferedReader.lines().toList();

            List<MessageSegment> reading = new ArrayList<>();
            StringBuilder readingStr = new StringBuilder();
            for(String c : contentsRaw) {
                if(c.equals("<split>") && (!reading.isEmpty() || !readingStr.isEmpty())) {
                    if(!readingStr.isEmpty()) {
                        reading.add(new TextMessageSegment(readingStr.toString().trim()));
                        readingStr = new StringBuilder();
                    }
                    contents.add(reading);
                    reading = new ArrayList<>();
                } else if(image_pattern.matcher(c).matches()) {
                    reading.add(new TextMessageSegment(readingStr.toString().trim()));
                    readingStr = new StringBuilder();
                    Matcher matcher = image_pattern.matcher(c);
                    matcher.find();
                    String imageSource = matcher.group(1);
                    String imagePath = matcher.group(2);
                    if(imageSource.equals("local")) {
                        File imageFile = new File(imagePath);
                        if (imageFile.exists()) {
                            reading.add(new ImageMessageSegment("file:///" + imageFile.getAbsolutePath()));
                        } else {
                            log.warn("找不到帮助文档中的本地图片: {}", imagePath);
                        }
                    } else {
                        reading.add(new ImageMessageSegment(imagePath));
                    }
                } else {
                    readingStr.append(c).append("\n");
                }
            }
            if(!readingStr.toString().trim().isEmpty()) {
                reading.add(new TextMessageSegment(readingStr.toString().trim()));
            }
            if(!reading.isEmpty()) {
                contents.add(reading);
            }
        }

        return Collections.unmodifiableList(contents);
    }
}
