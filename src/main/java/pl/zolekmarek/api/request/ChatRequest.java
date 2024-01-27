package pl.zolekmarek.api.request;

import pl.zolekmarek.api.request.RequestPayload;

import java.util.List;

public record ChatRequest(String model, List<Message> messages) implements RequestPayload {

    public record Message(String role, String content) {}
}
