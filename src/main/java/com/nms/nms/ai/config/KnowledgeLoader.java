package com.nms.nms.ai.config;

import com.nms.nms.ai.model.KnowledgeDocument;
import com.nms.nms.ai.repository.KnowledgeRepository;
import com.nms.nms.ai.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KnowledgeLoader implements CommandLineRunner {

    private final EmbeddingService embeddingService;
    private final KnowledgeRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        List<String> docs = List.of(
                "Optical power below -28 dBm indicates a critical fiber signal issue.",
                "Device temperature above 75 degrees Celsius is considered critical.",
                "Packet loss above 5 percent indicates network instability.",
                "High CPU utilization on network devices may indicate overload.",
                "OLT devices aggregate fiber connections from ONT users.",
                "Performance KPIs measure throughput, latency, and packet loss.",
                "Health KPIs detect hardware or environmental issues."
        );

        docs.forEach(text -> {
            var vector = embeddingService.generateEmbedding(text);

            KnowledgeDocument doc = new KnowledgeDocument();
            doc.setContent(text);
            doc.setEmbedding(vector);

            repository.save(doc);
        });

        System.out.println("✅ NMS AI Knowledge Base Loaded");
    }
}
