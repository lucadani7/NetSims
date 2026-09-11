package com.lucadani.netsims.editor;

import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TextEditorService {
    private final Map<String, TextDocument> documents = new ConcurrentHashMap<>();
    private final Validator validator;

    public TextEditorService(Validator validator) {
        this.validator = validator;
    }

    public TextDocument createDocument(String title, String initialContent) {
        String id = UUID.randomUUID().toString();
        TextDocument doc = new TextDocument(id, title, initialContent, 1L, LocalDateTime.now());
        validateDocument(doc);
        documents.put(id, doc);
        return doc;
    }

    public TextDocument getDocument(String id) {
        return documents.get(id);
    }

    public synchronized TextDocument updateDocumentContent(String id, String newContent, long expectedVersion) {
        TextDocument doc = getDocument(id);
        if (doc == null) {
            throw new IllegalArgumentException(String.format("Document with ID %s does not exist!", id));
        }
        if (doc.getVersion() != expectedVersion) {
            var message = String.format("Document with ID %s has been modified by someone else since last read! Current version: %d, expected version: %d", id, doc.getVersion(), expectedVersion);
            throw new IllegalArgumentException(message);
        }
        doc.setContent(newContent);
        doc.setVersion(doc.getVersion() + 1);
        doc.setLastModified(LocalDateTime.now());
        return doc;
    }

    public void validateDocument(TextDocument doc) {
        var violations = validator.validate(doc);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Invalid data: " + violations.iterator().next().getMessage());
        }
    }
}
