package pl.zolekmarek.others;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.zolekmarek.api.request.ChatRequest;
import pl.zolekmarek.api.request.CompletionRequest;
import pl.zolekmarek.api.response.CompletionResponse;
import pl.zolekmarek.service.OpenAIClient;

import java.util.*;

@Slf4j
public class CompletionBot {

    public static void main(String[] args) {
        CompletionBot completionBot = new CompletionBot();
        try (Scanner scanner = new Scanner(System.in)) {
            List<ChatRequest.Message> messages = new ArrayList<>();
            while (true) {
                String input = completionBot.getUserInput(scanner);
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }

                CompletionResponse completionResponse = completionBot.sendRequest(RequestData.builder()
                        .model("text-davinci-003")
                        .prompt(input)
                        .maxTokens(100)
                        .temperature(0.2)
                        .topP(1)
                        .n(1)
                        .stream(false)
                        .logprobs(null)
                        .build());

                messages.add(new ChatRequest.Message("user", input));
                completionBot.printResponse(completionResponse);
//                messages.addAll(completionBot.extractMessagesFromResponse("assistant", chatResponse));
            }

        } catch (Exception e) {
            log.error("An error occurred ", e);
        }
    }

    private String getUserInput(Scanner scanner) {
        log.info("Enter your question (or type 'exit' to quit):");
        return scanner.nextLine();
    }

    private CompletionResponse sendRequest(RequestData requestData) {

        CompletionRequest request = new CompletionRequest(requestData.getModel(), requestData.getPrompt(),
                requestData.getMaxTokens(), requestData.getTemperature(), requestData.getTopP(),
                requestData.getN(), requestData.isStream(), requestData.getLogprobs(), requestData.getStop());
        OpenAIClient openAIClient = new OpenAIClient();

        return openAIClient.sendMessage(request).orElse(CompletionResponse.empty());

    }

    private void printResponse(CompletionResponse completionResponse) {
        log.info("ID: " + completionResponse.id());
        log.info("Object: " + completionResponse.object());
        log.info("Model: " + completionResponse.model());
        completionResponse.choices().stream().map(CompletionResponse.Choice::toString).forEach(log::info);
        log.info("\n");
        log.info("Prompt tokens: " + completionResponse.usage().prompt_tokens());
        log.info("Completion tokens: " + completionResponse.usage().completion_tokens());
        log.info("Total tokens: " + completionResponse.usage().total_tokens());
    }

    @Builder
    @Getter
    public static class RequestData {
        private final String model;
        private final String prompt;
        private final int maxTokens;
        private final double temperature;
        private final double topP;
        private final int n;
        private final boolean stream;
        private final Integer logprobs;
        private final String stop;

    }
}
