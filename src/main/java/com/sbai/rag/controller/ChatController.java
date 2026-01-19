package com.sbai.rag.controller;

import com.sbai.rag.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api")
@CrossOrigin("*")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/query")
    private ResponseEntity getChat(@RequestParam(value = "q", defaultValue = "") String query){
        return chatService.getChat(query);
    }

}
