package pl.zolekmarek.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImageRequest(String prompt, int n, String size,
                           @JsonProperty("response_format") String responseFormat) implements RequestPayload {

}
