package com.sbai.rag.controller;

import com.sbai.rag.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
public class RagController {

    @Autowired
    private RagService ragService;

    @PostMapping("/v1/api/upload/doc")
    private ResponseEntity extractData(@RequestParam("file")MultipartFile file){
        return ragService.extractData(file);
    }
}
