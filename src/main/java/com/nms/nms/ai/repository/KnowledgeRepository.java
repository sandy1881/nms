package com.nms.nms.ai.repository;

import com.nms.nms.ai.model.KnowledgeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeRepository extends JpaRepository<KnowledgeDocument, Long> {
}
