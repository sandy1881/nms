package com.nms.nms.ai.model;

import jakarta.persistence.*;

@Entity
@Table(name = "knowledge_document")
public class KnowledgeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    // Store vector as string (simple approach)
    @Column(columnDefinition = "TEXT")
    private String embedding;

    public KnowledgeDocument() {}

    public KnowledgeDocument(String content, String embedding) {
        this.content = content;
        this.embedding = embedding;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getEmbedding() { return embedding; }

    public void setContent(String content) { this.content = content; }
    public void setEmbedding(String embedding) { this.embedding = embedding; }
}
