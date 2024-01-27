package pl.zolekmarek.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public record ImageResponse(@JsonProperty("created") long created, @JsonProperty("data")List<ImageData> data) {

    public record ImageData(@JsonProperty("b64_json") String b64Json) {}

    public static ImageResponse empty() {
        return new ImageResponse(0L, Collections.emptyList());
    }

}
