package com.lucadani.netsims.ui;

import com.lucadani.netsims.components.NetworkNodeComponent;
import com.lucadani.netsims.components.PacketSimulationComponent;
import com.lucadani.netsims.components.TcpControlComponent;
import com.lucadani.netsims.components.TextEditorComponent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {
    public MainView(TextEditorComponent textEditorComponent,
                    NetworkNodeComponent networkNodeComponent,
                    PacketSimulationComponent packetSimulationComponent,
                    TcpControlComponent tcpControlComponent) {
        setSpacing(true);
        setPadding(true);
        add(new H1("NetSims - Simulator & Collaborative Editor"));
        add(textEditorComponent);
        add(new Hr());
        add(networkNodeComponent);
        add(new Hr());
        add(packetSimulationComponent);
        add(new Hr());
        add(tcpControlComponent);
    }
}
