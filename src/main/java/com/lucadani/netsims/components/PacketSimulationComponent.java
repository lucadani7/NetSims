package com.lucadani.netsims.components;

import com.lucadani.netsims.simulation.NetworkSimulationEngine;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PacketSimulationComponent extends VerticalLayout {

    public PacketSimulationComponent(NetworkSimulationEngine simulationEngine) {
        setPadding(false);
        setSpacing(true);
        add(new H3("Packet Simulation Controls"));
        IntegerField lossField = new IntegerField("Packet Loss Rate (%)");
        lossField.setMin(0);
        lossField.setMax(50);
        lossField.setValue((int) (simulationEngine.getPacketLossRate() * 100));
        lossField.addValueChangeListener(event -> {
            int val = Optional.ofNullable(event.getValue()).orElse(0);
            val = Math.clamp(val, 0, 50);
            simulationEngine.setPacketLossRate(val / 100.0);
            Notification.show("Packet loss set at " + val + "%", 1500, Notification.Position.MIDDLE);
        });
        HorizontalLayout manualSendLayout = getManualSendLayout(simulationEngine);
        Div logOutput = new Div();
        logOutput.getStyle().set("background", "var(--lumo-contrast-5pct)")
                .set("padding", "var(--lumo-space-s)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("max-height", "150px")
                .set("overflow-y", "auto");
        logOutput.setWidthFull();
        logOutput.add(new Html("<div><i>Process log will appear here...</i></div>"));
        Button processQueueButton = new Button("Process Queue Now & Log", event -> {
            String logResult = simulationEngine.processPacketQueue();
            logOutput.removeAll();
            logOutput.add(new Html("<div>" + logResult + "</div>"));
        });
        add(lossField, manualSendLayout, processQueueButton, logOutput);
    }

    private @NonNull HorizontalLayout getManualSendLayout(NetworkSimulationEngine simulationEngine) {
        TextField srcField = new TextField("Source Node ID", "n1");
        TextField dstField = new TextField("Dest Node ID", "n2");
        IntegerField sizeField = new IntegerField("Size (bytes)");
        sizeField.setValue(128);
        Button sendManualButton = new Button("Send Manual Packet", event -> {
            try {
                simulationEngine.sendPacket(srcField.getValue(), dstField.getValue(), sizeField.getValue());
                Notification.show("Packet sent to queue!", 1500, Notification.Position.MIDDLE);
            } catch (Exception e) {
                Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });
        HorizontalLayout manualSendLayout = new HorizontalLayout(srcField, dstField, sizeField, sendManualButton);
        manualSendLayout.setAlignItems(Alignment.BASELINE);
        return manualSendLayout;
    }
}
