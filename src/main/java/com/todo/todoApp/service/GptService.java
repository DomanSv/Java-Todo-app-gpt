package com.todo.todoApp.service;

import static com.todo.todoApp.service.TodoService.BEARER;
import static java.util.Objects.isNull;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import com.todo.todoApp.data.entity.Todo;
import com.todo.todoApp.data.entity.UserProfile;
import com.todo.todoApp.data.repository.TodoRepository;
import com.todo.todoApp.data.repository.UserRepository;
import com.todo.todoApp.model.dto.ChatMessagePrompt;
import com.todo.todoApp.service.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GptService {

  private final UserRepository userRepository;
  private final TodoRepository todoRepository;
  public static final String OPEN_AI_SECRET_KEY = "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";

  public ResponseEntity<String> askGptForTodo(String token, String todoId) {

    UserProfile user = getUserFromToken(token);

    if (isNull(user)) {
      return ResponseEntity.badRequest().build();
    }

    Todo todo = user.getTodos().stream().filter(t -> t.getId().equals(Long.parseLong(todoId))).findFirst().orElse(null);

    if (isNull(todo)) {
      return ResponseEntity.badRequest().build();
    }

    ChatMessagePrompt chatMessagePrompt = createChatMessagePrompt(todo);

    ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
        .messages(chatMessagePrompt.getChatMessage())
        .model("gpt-3.5-turbo")
        .build();

    OpenAiService openAiService = new OpenAiService(OPEN_AI_SECRET_KEY, Duration.ofSeconds(60));

    String response = openAiService.createChatCompletion(completionRequest).getChoices().get(0).getMessage().getContent();

    todo.setAIResponse(response);
    todo.setHasAIResponse(true);
    todoRepository.save(todo);

    return ResponseEntity.ok(response);

  }

  private ChatMessagePrompt createChatMessagePrompt(Todo todo) {
    ChatMessage systemChatMessage = new ChatMessage();
    systemChatMessage.setRole("system");
    systemChatMessage.setContent("Ще ти пратя задача която трябва да изпълня. Дай съвети за това как да я свърша. Максимум 5 изречения.");

    ChatMessage userChatMessage = new ChatMessage();
    userChatMessage.setRole("user");
    userChatMessage.setContent(todo.toString());

    ChatMessagePrompt chatMessagePrompt = new ChatMessagePrompt();
    chatMessagePrompt.setChatMessage(List.of(systemChatMessage, userChatMessage));

    return chatMessagePrompt;

  }

  private UserProfile getUserFromToken(String token) {

    String formattedToken = token.replace(BEARER, "").replace("\"", "");

    String username = JwtUtils.extractUsernameFromToken(formattedToken);

    return userRepository.findByUsername(username).orElse(null);

  }
}
