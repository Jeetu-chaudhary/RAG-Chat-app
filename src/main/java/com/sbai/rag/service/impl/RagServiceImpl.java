package com.sbai.rag.service.impl;

import com.sbai.rag.service.RagService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class RagServiceImpl implements RagService {
    @Autowired
    private VectorStore vectorStore;

    //step to store data in vector db ETL
//    1. extract data from doc
//    2. translate that doc in chunks
//    3. load in vector db

    @Override
    public ResponseEntity<?> extractData(MultipartFile file) {

        try {

            /* 1️⃣ Convert MultipartFile → Resource */
            Resource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            //    1. extract data from doc
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
            List<Document> documentList = tikaDocumentReader.read();

            //    2. translate that doc in chunks
            TokenTextSplitter tokenTextSplitter =
                    TokenTextSplitter.builder()
                            .withChunkSize(150)              // chunk length
                            .withMinChunkSizeChars(50)       // ignore tiny chunks
                            .withMinChunkLengthToEmbed(50)   // embedding threshold
                            .withMaxNumChunks(1000)          // safety limit
                            .withKeepSeparator(true)
                            .build();

            List<Document> translateDocList = tokenTextSplitter.apply(documentList);

            //    3. load in vector db
            vectorStore.accept(translateDocList);

            return ResponseEntity.ok(
                    Map.of("message","file uploaded successfully")
            );
        } catch (Exception e) {
            return new ResponseEntity<>("file not uploaded " + e.getMessage(), HttpStatusCode.valueOf(501));
        }
    }
}
