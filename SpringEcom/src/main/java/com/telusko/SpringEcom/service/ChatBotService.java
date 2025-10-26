package com.telusko.SpringEcom.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatBotService {
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private PgVectorStore vectorStore;

    @Autowired
    private ChatClient chatClient;

    public String getBotResponse(String message) {

        try {
            String promptStringTemplate = Files.readString(
                    resourceLoader.getResource("classpath:prompts/chatbot-rag-prompt.st")
                            .getFile()
                            .toPath()
            );

            String context = fetchSemanticContext(message);

            Map<String, Object> var = new HashMap<>();
            var.put("context", context);
            var.put("userQuery", message);

            PromptTemplate promptTemplate = PromptTemplate.builder()
                    .template(promptStringTemplate)
                    .variables(var)
                    .build();

            return chatClient.prompt(promptTemplate.create()).call().content();

        } catch (IOException e) {
            return "Bot Error";
        }

    }

    private String fetchSemanticContext(String message) {
        List<Document> documentList = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(5)
                        .similarityThreshold(0.7f)
                        .build()
        );

        StringBuilder response = new StringBuilder();
        for (Document d : documentList) {
            response.append(d.getFormattedContent()).append("\n");
        }

        return response.toString();
    }
}
