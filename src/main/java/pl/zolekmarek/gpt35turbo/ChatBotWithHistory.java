package pl.zolekmarek.gpt35turbo;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.zolekmarek.api.request.ChatRequest;
import pl.zolekmarek.api.response.ChatResponse;
import pl.zolekmarek.service.OpenAIClient;

import java.util.*;

@Slf4j
public class ChatBotWithHistory {
    public static void main(String[] args) {
        ChatBotWithHistory chatBot = new ChatBotWithHistory();

        try (Scanner scanner = new Scanner(System.in)) {
            List<ChatRequest.Message> messages = new ArrayList<>();
            while (true) {
                String input = chatBot.getUserInput(scanner);
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }

                messages.add(new ChatRequest.Message("user", input));
                ChatResponse chatResponse = chatBot.sendRequest(RequestData.builder()
                        .model("gpt-3.5-turbo")
                        .messages(messages)
                        .build());
                chatBot.printResponse(chatResponse);
                messages.addAll(chatBot.extractMessagesFromResponse("assistant", chatResponse));
            }

        } catch (Exception e) {
            log.error("An error occurred ", e);
        }
    }

    private List<ChatRequest.Message> extractMessagesFromResponse(String role, ChatResponse chatResponse) {
        List<ChatRequest.Message> messages = new ArrayList<>();

        List<ChatResponse.Choice> choices = Optional.ofNullable(chatResponse.choices()).orElse(Collections.emptyList());
        choices.forEach(choice -> messages.add(new ChatRequest.Message(role, choice.message().content())));
        return messages;
    }

    private void printResponse(ChatResponse chatResponse) {
        List<ChatResponse.Choice> choices = Optional.ofNullable(chatResponse.choices()).orElse(Collections.emptyList());
        choices.stream().map(choice -> choice.message().content()).forEach(log::info);
    }

    private ChatResponse sendRequest(RequestData requestData) {
        ChatRequest request = new ChatRequest(requestData.getModel(), requestData.getMessages());
        OpenAIClient openAIClient = new OpenAIClient();
        return openAIClient.sendMessage(request).orElse(ChatResponse.empty());
    }

    private String getUserInput(Scanner scanner) {
      log.info("Enter your question (or type 'exit' to quit):");
      return scanner.nextLine();
    }

    @Builder
    @Getter
    private static class RequestData {
        private String model;
        private List<ChatRequest.Message> messages;
    }
}