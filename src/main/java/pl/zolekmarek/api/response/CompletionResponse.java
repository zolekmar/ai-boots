package pl.zolekmarek.api.response;

import java.util.Collections;
import java.util.List;

public record CompletionResponse(String id, String object, long created, String model, List<Choice> choices, Usage usage) {

    public record Choice(String text, int index, Integer logprobs, String finish_reason) {

        @Override
        public String toString() {
            return "Text: " + text() + "\n\nindex: " + index() + ", logprobs: " + logprobs() + ", finish_reason: " + finish_reason();
        }
    }

    public record Usage(int prompt_tokens, int completion_tokens, int total_tokens) {

    }

    public static CompletionResponse empty() {
        return new CompletionResponse(null, null, 0, null, Collections.emptyList(),
                new Usage(0,0,0));
    }
}
