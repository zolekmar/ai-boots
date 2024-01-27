package pl.zolekmarek.dalle;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.zolekmarek.others.OpenAiConfig;
import pl.zolekmarek.api.request.ImageRequest;
import pl.zolekmarek.api.response.ImageResponse;
import pl.zolekmarek.service.OpenAIClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
public class DalleGenerateImageBot {
    public static void main(String[] args) {
        DalleGenerateImageBot dalleGenerateImageBot = new DalleGenerateImageBot();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = dalleGenerateImageBot.getUserInput(scanner);
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }

                ImageResponse response = dalleGenerateImageBot.sendRequest(RequestData.builder()
                        .prompt(input)
                        .numberOfImages(3)
                        .model("dall-e-3")
                        .size("1024x1024")
                        .responseFormat("b64_json")
                        .build());
                dalleGenerateImageBot.processResponse(response);
            }
        }
    }

    private void processResponse(ImageResponse imageResponse) {
        List<ImageResponse.ImageData> images = Optional.ofNullable(imageResponse.data()).orElse(Collections.emptyList());
        images.stream().map(image -> Base64.getDecoder().decode(image.b64Json())).forEach(this::storeImage);
    }

    private void storeImage(byte[] image) {
        String imageDir = "src/main/resources/dalle/result/";
        String imageFileName =  imageDir + "image_" + System.currentTimeMillis() + ".png";

        try(FileOutputStream fos = new FileOutputStream(imageFileName)) {
            fos.write(image);
            log.info("The image has been generated and saved: " +imageFileName);
        } catch (IOException e) {
            log.error("An error occurred while writing the image file: " + e.getMessage(), e);
        }
    }


    private ImageResponse sendRequest(RequestData requestData) {
        ImageRequest request = new ImageRequest(requestData.getPrompt(), requestData.getNumberOfImages(),
                requestData.getSize(), requestData.getResponseFormat());
        OpenAIClient openAIClient = new OpenAIClient();

        return openAIClient.sendImageMessage(request, OpenAiConfig.DALLE_API_URL).orElse(ImageResponse.empty());
    }

    private String getUserInput(Scanner scanner) {
        log.info("Provide a description of the image (or type 'exit' to quit):");
        return scanner.nextLine();
    }

    @Builder
    @Getter
    private static class RequestData {

        private final String prompt;
        private final int numberOfImages;
        private final String model;
        private final String size;
        private final String responseFormat;
    }
}
