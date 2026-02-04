package com.nms.nms.ai.service;

import com.nms.nms.ai.model.KnowledgeDocument;
import com.nms.nms.ai.repository.KnowledgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VectorSearchService {

    private final KnowledgeRepository repo;

    // Basic search (later we improve similarity scoring)
    public List<KnowledgeDocument> getAllKnowledge() {
        return repo.findAll();
    }

    public void saveDocument(String content, String embedding) {
        repo.save(new KnowledgeDocument(content, embedding));
    }
}
