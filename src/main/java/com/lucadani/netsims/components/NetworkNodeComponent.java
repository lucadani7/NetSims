package com.lucadani.netsims.components;

import com.lucadani.netsims.simulation.NetworkSimulationEngine;
import com.lucadani.netsims.simulation.Node;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class NetworkNodeComponent extends VerticalLayout {
    public NetworkNodeComponent(NetworkSimulationEngine simulationEngine) {
        setPadding(false);
        setSpacing(true);
        add(new H3("Testing Network Simulation"));
        TextField nodeNameField = new TextField("Network Node Name");
        Button addNodeButton = new Button("Add Node", event -> {
            try {
                Node node = new Node("node-" + System.currentTimeMillis(), nodeNameField.getValue(), 1000, new ArrayList<>());
                simulationEngine.addNode(node);
                Notification.show("Node " + node.getName() + " added to the network!");
            } catch (Exception e) {
                Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });
        add(nodeNameField, addNodeButton);
    }
}
