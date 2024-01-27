package pl.zolekmarek.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CompletionRequest(String model, String prompt, @JsonProperty("max_tokens") int maxTokens, double temperature,
                                @JsonProperty("top_p") double topP, int n, boolean stream,
                                Integer logprobs, String stop) implements RequestPayload {
}
