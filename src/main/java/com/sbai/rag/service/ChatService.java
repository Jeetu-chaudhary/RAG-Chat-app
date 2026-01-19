package com.sbai.rag.service;


import org.springframework.http.ResponseEntity;

public interface ChatService {
    ResponseEntity getChat(String query);
}
