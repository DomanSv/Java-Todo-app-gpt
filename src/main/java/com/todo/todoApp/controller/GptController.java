package com.todo.todoApp.controller;


import static com.todo.todoApp.service.TodoService.BEARER;
import static java.util.Objects.isNull;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import com.todo.todoApp.data.entity.Todo;
import com.todo.todoApp.data.entity.UserProfile;
import com.todo.todoApp.data.repository.TodoRepository;
import com.todo.todoApp.data.repository.UserRepository;
import com.todo.todoApp.service.GptService;
import com.todo.todoApp.service.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/gpt")
@RequiredArgsConstructor
public class GptController {

  private final GptService gptService;

  public static final String OPEN_AI_SECRET_KEY = "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";

  @PostMapping("/context")
  public String configureContext() {

    OpenAiService openAiService = new OpenAiService(OPEN_AI_SECRET_KEY);

    ChatMessage systemChatMessage = new ChatMessage();
    systemChatMessage.setRole("system");
    systemChatMessage.setContent("Ще ти пратя задача която трябва да изпилня. Върни инструкции за това как да я свърша на български език. Максимум 5 изречения.");

    ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
        .messages(List.of(systemChatMessage))
        .model("gpt-3.5-turbo")
        .build();

    return openAiService.createChatCompletion(completionRequest).getChoices().get(0).getMessage().getContent();

  }

  @PostMapping("/todo/{todoId}")
  public ResponseEntity<String> askGptForTodoById(@RequestHeader("Authorization") String token, @PathVariable String todoId) {
    return gptService.askGptForTodo(token, todoId);

  }

}
