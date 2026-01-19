package com.sbai.rag.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface RagService {
     ResponseEntity extractData(MultipartFile file);
}
