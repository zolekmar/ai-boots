package pl.zolekmarek.api.response;

import java.util.Collections;
import java.util.List;

public record ChatResponse (List<Choice> choices) {

    public record Choice(Message message) {
        public record Message(String role, String content) {

        }
    }

    public static ChatResponse empty() {
        return new ChatResponse(Collections.emptyList());
    }


}
