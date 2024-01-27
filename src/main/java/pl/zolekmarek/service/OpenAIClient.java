package pl.zolekmarek.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import pl.zolekmarek.api.response.AudioResponse;
import pl.zolekmarek.api.request.ChatRequest;
import pl.zolekmarek.api.request.CompletionRequest;
import pl.zolekmarek.api.request.ImageRequest;
import pl.zolekmarek.others.OpenAiConfig;
import pl.zolekmarek.api.request.RequestPayload;
import pl.zolekmarek.api.response.ChatResponse;
import pl.zolekmarek.api.response.CompletionResponse;
import pl.zolekmarek.api.response.ImageResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OpenAIClient {

    private static final int TIMEOUT_SECONDS = 60;
    private final OkHttpClient client;
    private static final ObjectMapper MAPPER = objectMapper();
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");


    public OpenAIClient () {
        client = new OkHttpClient.Builder()
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    private Request createRequest (String url, RequestBody body) {
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + OpenAiConfig.API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
    }

    public Optional<ChatResponse> sendMessage(ChatRequest chatRequest) {
        RequestBody requestBody = createJSONRequestBody(chatRequest);
        Request request = createRequest(OpenAiConfig.CHAT_GPT_URL, requestBody);
        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                log.error("Unexpected response code: {}", response);
                return Optional.empty();
            }
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                ChatResponse chatResponse = MAPPER.readValue(responseBody.string(), ChatResponse.class);
                return Optional.ofNullable(chatResponse);
            } else {
                log.error("No data in the response.");
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error("Exception occurred during API call: {}", e.getMessage(), e);
            return Optional.empty();
        }

    }

    public Optional<CompletionResponse> sendMessage(CompletionRequest completionRequest) {
        RequestBody requestBody = createJSONRequestBody(completionRequest);
        Request request = createRequest(OpenAiConfig.COMPLETIONS_GPT_URL, requestBody);
        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                log.error("Unexpected response code: {}", response);
                return Optional.empty();
            }
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                CompletionResponse completionResponse = MAPPER.readValue(responseBody.string(), CompletionResponse.class);
                return Optional.ofNullable(completionResponse);
            } else {
                log.error("No data in the response.");
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error("Exception occurred during API call: {}", e.getMessage(), e);
            return Optional.empty();
        }

    }

    public Optional<ImageResponse> sendImageMessage(ImageRequest imageRequest, String url) {
        RequestBody requestBody = createJSONRequestBody(imageRequest);

        return sendImageMessage(requestBody, url);
    }

    public Optional<ImageResponse> sendImageMessage(RequestBody requestBody, String url) {
        Request request = createRequest(url, requestBody);
        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                log.error("Unexpected response code: {}", response);
                return Optional.empty();
            }
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                ImageResponse imageResponse = MAPPER.readValue(responseBody.string(), ImageResponse.class);
                return Optional.ofNullable(imageResponse);
            } else {
                log.error("No data in the response.");
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error("Exception occurred during API call: {}", e.getMessage(), e);
            return Optional.empty();
        }

    }

    public Optional<AudioResponse> sendAudioMessage(RequestBody requestBody, String url) {
        Request request = createRequest(url, requestBody);
        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                log.error("Unexpected response code: {}", response);
                return Optional.empty();
            }
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                AudioResponse audioResponse = MAPPER.readValue(responseBody.string(), AudioResponse.class);
                return Optional.ofNullable(audioResponse);
            } else {
                log.error("No data in the response.");
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error("Exception occurred during API call: {}", e.getMessage(), e);
            return Optional.empty();
        }

    }



    private RequestBody createJSONRequestBody(RequestPayload requestPayload) {

        try {
            String json = MAPPER.writeValueAsString(requestPayload);
            return RequestBody.create(json, JSON_MEDIA_TYPE);
        } catch (JsonProcessingException e) {
            log.error("Failed to create request body: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }

}
