package pl.zolekmarek.support;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TextLineWrapper {

    private static final int MAX_LINE_LENGTH = 120;

    public static String wrapLines(String text) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            if (lineWithNextWordIsTooLong(line, word, MAX_LINE_LENGTH)) {
                lines.add(line.toString().trim());
                line.setLength(0);
            }
            line.append(word).append(" ");
        }
        if (line.length() > 0) {
            lines.add(line.toString().trim());
        }

        return String.join(System.lineSeparator(), lines);
    }

    private static boolean lineWithNextWordIsTooLong(StringBuilder line, String word, int maxLineLength) {
        return line.length() + word.length() > maxLineLength;
    }
}
