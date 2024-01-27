package pl.zolekmarek.whisper;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.zolekmarek.others.OpenAiConfig;
import pl.zolekmarek.api.response.AudioResponse;
import pl.zolekmarek.service.OpenAIClient;
import pl.zolekmarek.support.TextLineWrapper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class TranscriptionBot {

    public static final String INPUT_DIR = "src/main/resources/whisper/";
    public static final String INPUT_FILE = "AudioInput.mp3";

    public static void main(String[] args) {
        TranscriptionBot bot = new TranscriptionBot();
        AudioResponse audioResponse = bot.sendRequest(RequestData.builder()
                .audio(INPUT_FILE)
                .audioUrl(INPUT_DIR + INPUT_FILE)
                .model("whisper-1")
                .prompt("This transcript should include proper punctuation and be in formal writing style.")
                .responseFormat("json")
                .temperature(new BigDecimal("0.2"))
                .language("pl")
                .build());
        bot.storeAudioText(audioResponse);
    }

    private AudioResponse sendRequest(RequestData requestData) {
        RequestBody requestBody = createMultipartRequestBody(requestData);
        OpenAIClient openAIClient = new OpenAIClient();
        return openAIClient.sendAudioMessage(requestBody, OpenAiConfig.TRANSCRIPTIONS_API_URL).orElse(
                AudioResponse.empty());
    }

    private void storeAudioText(AudioResponse audioResponse) {
        String audioDir = "src/main/resources/whisper/result/";
        String audioFileName = audioDir + "audio_" + System.currentTimeMillis() + ".txt";
        try {
            Files.writeString(Path.of(audioFileName), TextLineWrapper.wrapLines(audioResponse.text()), StandardCharsets.UTF_8);
            log.info("The audio text has been generated and saved: " + audioFileName);
        } catch (IOException exception) {
            log.error("An error occurred while writing the audio file: " + exception.getMessage());
        }
    }

    private RequestBody createMultipartRequestBody(RequestData requestData) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", requestData.getAudio(),
                                    RequestBody.create(MediaType.parse("whisper/mpeg"),
                                            new File(requestData.getAudioUrl())))
                .addFormDataPart("model", requestData.getModel())
                .addFormDataPart("prompt", requestData.getPrompt())
                .addFormDataPart("response_format", requestData.getResponseFormat())
                .addFormDataPart("temperature", requestData.getTemperature().toString())
                .addFormDataPart("language", requestData.getLanguage()).build();
    }

    @Builder
    @Getter
    private static class RequestData {
        private String audio;
        private String audioUrl;
        private String model;
        private String prompt;
        private String responseFormat;
        private BigDecimal temperature;
        private String language;
    }
}
