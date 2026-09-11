package com.lucadani.netsims.simulation;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class NetworkSimulationEngine {
    private final Map<String, Node> nodes = new HashMap<>();
    private final Queue<Packet> packetQueue = new ConcurrentLinkedQueue<>();
    @Getter
    private final List<Packet> packetHistory = new ArrayList<>();
    private final Map<String, TcpConnectionState> tcpStates = new HashMap<>();

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        System.out.printf("Node added in the simulation: %s\n", node.getName());
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    public Map<String, Node> getAllNodes() {
        return nodes;
    }

    public TcpConnectionState getOrCreateTcpState(String sourceId, String destinationId) {
        String key = sourceId + "->" + destinationId;
        return tcpStates.computeIfAbsent(key, TcpConnectionState::new);
    }

    public Collection<TcpConnectionState> getAllTcpStates() {
        return tcpStates.values();
    }

    public String processPacketQueue() {
        if (packetQueue.isEmpty()) {
            return "No packets to process.";
        }
        StringBuilder resultLog = new StringBuilder();
        Packet packet;
        while ((packet = packetQueue.poll()) != null) {
            TcpConnectionState state = getOrCreateTcpState(packet.sourceNodeId(), packet.destinationNodeId());
            resultLog.append(String.format("Processed: Packet [%s] | Sequence: %d | Route: %s -> %s | Dimension: %d bytes | Current cwnd: %.2f<br>",
                    packet.id(),
                    packet.sequenceNumber(),
                    packet.sourceNodeId(),
                    packet.destinationNodeId(),
                    packet.sizeBytes(),
                    state.getCwnd()));
        }
        return resultLog.toString();
    }

    public Packet sendPacket(String sourceId, String destinationId, int sizeBytes) {
        Node source = nodes.get(sourceId);
        Node destination = nodes.get(destinationId);
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Source or destination node not found!");
        }
        String packetId = "pkt-" + UUID.randomUUID().toString().substring(0, 6);
        int sequenceNumber = packetHistory.size() + 1;
        Packet packet = new Packet(packetId, sourceId, destinationId, sequenceNumber, sizeBytes);
        packetQueue.add(packet);
        packetHistory.add(packet);
        System.out.printf("%s packet has been sent from %s to %s\n", packetId, source.getName(), destination.getName());
        return packet;
    }
}
