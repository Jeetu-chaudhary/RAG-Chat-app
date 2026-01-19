package com.sbai.rag.service.impl;

import com.sbai.rag.service.ChatService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private VectorStore vectorStore;

    @Override
    public ResponseEntity<?> getChat(String query) {

        try {

            /* 1️⃣ Vector search */
            List<Document> docs =
                    vectorStore.similaritySearch(
                            SearchRequest.builder()
                                    .query(query)
                                    .topK(3)
                                    .similarityThreshold(0.4)
                                    .build()
                    );

            if (docs.isEmpty()) {
                return ResponseEntity.ok(
                        "No relevant data found for your query.");
            }

            /* 2️⃣ Build context */
            StringBuilder context = new StringBuilder();

            for (Document d : docs) {
                context.append(d.getFormattedContent()).append("\n\n");
            }
//            for (Document d : docs) {
//                context.append(d.getText()).append("\n\n");
//            }

            /* 3️⃣ Create RAG prompt */
            String ragPrompt = """
                You are an AI assistant.
                Answer the question only from the context below.

                Context:
                %s

                Question:
                %s
                """.formatted(context, query);

            /* 4️⃣ Call LLM */
            String answer = chatModel.call(ragPrompt);

            /* 5️⃣ Response */
            Map<String, Object> res = new HashMap<>();
            res.put("question", query);
            res.put("answer", answer);
            res.put("sources", context);

            return ResponseEntity.ok(res);

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body("RAG failed: " + e.getMessage());
        }
    }
}
