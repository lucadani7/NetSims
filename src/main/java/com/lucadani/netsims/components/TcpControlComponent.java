package com.lucadani.netsims.components;

import com.lucadani.netsims.simulation.NetworkSimulationEngine;
import com.lucadani.netsims.simulation.TcpConnectionState;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.stereotype.Component;

@Component
public class TcpControlComponent extends VerticalLayout {

    private final NetworkSimulationEngine simulationEngine;
    private final Grid<TcpConnectionState> grid = new Grid<>(TcpConnectionState.class);

    public TcpControlComponent(NetworkSimulationEngine simulationEngine) {
        this.simulationEngine = simulationEngine;
        setPadding(false);
        setSpacing(true);

        add(new H3("TCP Congestion Control (RFC 5681) & Monitor"));

        TextField connSourceField = new TextField("Connection Source (ex: n1)");
        TextField connDestField = new TextField("Connection Destination (ex: n2)");

        Button initConnButton = new Button("Get/Init TCP State", event -> {
            try {
                simulationEngine.getOrCreateTcpState(connSourceField.getValue(), connDestField.getValue());
                refreshGrid();
                Notification.show("TCP State loaded!");
            } catch (Exception e) {
                Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        Button ackButton = new Button("Simulate ACK Received", event -> {
            try {
                TcpConnectionState state = simulationEngine.getOrCreateTcpState(connSourceField.getValue(), connDestField.getValue());
                state.onAckReceived();
                refreshGrid();
            } catch (Exception e) {
                Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        Button dupAckButton = new Button("Simulate Duplicate ACK", event -> {
            try {
                TcpConnectionState state = simulationEngine.getOrCreateTcpState(connSourceField.getValue(), connDestField.getValue());
                state.onDuplicateAckReceived();
                refreshGrid();
            } catch (Exception e) {
                Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        Button timeoutButton = new Button("Simulate Timeout (Packet Loss)", event -> {
            try {
                TcpConnectionState state = simulationEngine.getOrCreateTcpState(connSourceField.getValue(), connDestField.getValue());
                state.onPacketLossTimeout();
                refreshGrid();
                Notification.show("Timeout simulated! Reset to Slow Start.", 3000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        // Configurare Grid
        grid.setColumns("connectionId", "cwnd", "ssthresh", "phase", "duplicateAckCount");
        grid.setHeight("200px");
        refreshGrid();

        add(connSourceField, connDestField, initConnButton, ackButton, dupAckButton, timeoutButton, grid);
    }

    private void refreshGrid() {
        grid.setItems(simulationEngine.getAllTcpStates());
    }
}