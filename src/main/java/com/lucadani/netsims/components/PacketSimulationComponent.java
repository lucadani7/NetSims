package com.lucadani.netsims.components;

import com.lucadani.netsims.simulation.NetworkSimulationEngine;
import com.lucadani.netsims.simulation.Packet;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.stereotype.Component;

@Component
public class PacketSimulationComponent extends VerticalLayout {
    public PacketSimulationComponent(NetworkSimulationEngine simulationEngine) {
        setPadding(false);
        setSpacing(true);
        add(new H3("Traffic Simulation - Send Packet"));
        TextField sourceField = new TextField("Node Source ID");
        TextField destField = new TextField("Node Destination ID");
        TextField sizeField = new TextField("Packet Dimension (bytes)");
        sizeField.setPlaceholder("Ex: 512");
        Button sendPacketButton = new Button("Send Packet", event -> {
            try {
                if (sizeField.isEmpty()) {
                    Notification.show("Please enter the packet dimension!", 3000, Notification.Position.MIDDLE);
                    return;
                }
                int sizeBytes = Integer.parseInt(sizeField.getValue());
                Packet p = simulationEngine.sendPacket(sourceField.getValue(), destField.getValue(), sizeBytes);
                Notification.show("Packet sent! ID: " + p.id() + " | Dimension: " + sizeBytes + " | Sequence: " + p.sequenceNumber());
            } catch (NumberFormatException e) {
                Notification.show("Error: invalid packet dimension! It must be a valid number!", 3000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                Notification.show("Error send: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });
        add(sourceField, destField, sizeField, sendPacketButton);
    }
}
