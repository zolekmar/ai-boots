package pl.zolekmarek.dalle;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.zolekmarek.others.OpenAiConfig;
import pl.zolekmarek.api.response.ImageResponse;
import pl.zolekmarek.service.OpenAIClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DalleImageVariationBot {


    private static final String INPUT_DIR = "src/main/resources/dalle/";
    private static final String INPUT_FILE = "/kaczka.png";


    public static void main(String[] args) {

        DalleImageVariationBot bot = new DalleImageVariationBot();

        ImageResponse response = bot.sendRequest(RequestData.builder()
                .numberOfImages(3)
                .size("1024x1024")
                .responseFormat("b64_json")
                .image(INPUT_FILE)
                .imageUrl(INPUT_DIR + INPUT_FILE)
                .build());

        bot.processResponse(response);

    }

    private ImageResponse sendRequest(RequestData requestData) {

        RequestBody requestBody = createMultipartRequestBody(requestData);
        OpenAIClient openAIClient = new OpenAIClient();

        return openAIClient.sendImageMessage(requestBody, OpenAiConfig.DALLE_VARIATIONS_API_URL).orElse(ImageResponse.empty());

    }

    private RequestBody createMultipartRequestBody(RequestData requestData) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("n", String.valueOf(requestData.getNumberOfImages()))
                .addFormDataPart("size", requestData.getSize())
                .addFormDataPart("response_format", requestData.getResponseFormat())
                .addFormDataPart("image", requestData.getImage(),
                        RequestBody.create(MediaType.parse("image/png"), new File(requestData.getImageUrl())))
                .build();
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

    @Builder
    @Getter
    private static class RequestData {

        private final String prompt;
        private final int numberOfImages;
        private final String size;
        private final String responseFormat;
        private final String image;
        private final String imageUrl;
    }
}
