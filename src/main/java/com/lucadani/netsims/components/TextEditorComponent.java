package com.lucadani.netsims.components;

import com.lucadani.netsims.editor.TextDocument;
import com.lucadani.netsims.editor.TextEditorService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.stereotype.Component;

@Component
public class TextEditorComponent extends VerticalLayout {
    public TextEditorComponent(TextEditorService textEditorService) {
        setPadding(false);
        setSpacing(true);
        add(new H3("Testing Text Editor"));
        TextField titleField = new TextField("Document Title");
        TextField contentField = new TextField("Initial Content");
        Button saveDocButton = new Button("Create Document", event -> {
            try {
                TextDocument doc = textEditorService.createDocument(titleField.getValue(), contentField.getValue());
                Notification.show("Document created successfully! ID: " + doc.getId());
            } catch (Exception e) {
                Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });
        add(titleField, contentField, saveDocButton);
    }
}
