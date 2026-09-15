package com.lucadani.netsims.components;

import com.lucadani.netsims.simulation.NetworkSimulationEngine;
import com.lucadani.netsims.simulation.Packet;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TextEditorComponent extends VerticalLayout {
    private final TextArea textArea = new TextArea("Collaborative Document Text");

    public TextEditorComponent(NetworkSimulationEngine simulationEngine) {
        setPadding(false);
        setSpacing(true);
        textArea.setWidthFull();
        textArea.setHeight("150px");
        Button syncButton = new Button("Sync Changes over Network", event -> {
            String content = textArea.getValue();
            if (content == null || content.isEmpty()) {
                Notification.show("The document is empty!", 2000, Notification.Position.MIDDLE);
                return;
            }
            int sizeInBytes = content.getBytes().length;
            Packet syncPacket = new Packet(
                    "sync-" + UUID.randomUUID().toString().substring(0, 6),
                    "editor-client",
                    "server-node",
                    (int) (Math.random() * 1000),
                    sizeInBytes
            );
            simulationEngine.enqueuePacket(syncPacket);
            Notification.show("Sync packet sent to the network! (" + sizeInBytes + " bytes)", 3000, Notification.Position.MIDDLE);
        });
        add(textArea, syncButton);
    }
}
