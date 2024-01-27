package pl.zolekmarek.others;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.zolekmarek.api.request.ChatRequest;
import pl.zolekmarek.api.response.ChatResponse;
import pl.zolekmarek.service.OpenAIClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class ChatBot {
    public static void main(String[] args) {
        ChatBot chatBot = new ChatBot();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = chatBot.getUserInput(scanner);
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }


                List<ChatRequest.Message> messages = List.of(new ChatRequest.Message("user", input));
                ChatResponse chatResponse = chatBot.sendRequest(RequestData.builder()
                        .model("gpt-3.5-turbo")
                        .messages(messages)
                        .build());
                chatBot.printResponse(chatResponse);

            }

        } catch (Exception e) {
            log.error("An error occurred ", e);
        }
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