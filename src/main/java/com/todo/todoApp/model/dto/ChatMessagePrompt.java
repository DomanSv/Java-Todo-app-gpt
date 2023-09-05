package com.todo.todoApp.model.dto;

import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatMessagePrompt {
  private List<ChatMessage> chatMessage;

}
