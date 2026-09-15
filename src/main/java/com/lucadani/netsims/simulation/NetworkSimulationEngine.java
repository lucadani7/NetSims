package com.lucadani.netsims.simulation;

import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NetworkSimulationEngine {
    @Getter
    private double packetLossRate = 0;
    private final Random random = new Random();
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();
    private final Map<String, Node> nodes = new HashMap<>();
    private final Queue<Packet> packetQueue = new ConcurrentLinkedQueue<>();
    @Getter
    private final List<Packet> packetHistory = new ArrayList<>();
    private final Map<String, TcpConnectionState> tcpStates = new HashMap<>();

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        System.out.printf("Node added in the simulation: %s\n", node.getName());
    }

    public TcpConnectionState getOrCreateTcpState(String sourceId, String destinationId) {
        String key = sourceId + "->" + destinationId;
        return tcpStates.computeIfAbsent(key, TcpConnectionState::new);
    }

    public Collection<TcpConnectionState> getAllTcpStates() {
        return tcpStates.values();
    }

    public void setPacketLossRate(double packetLossRate) {
        this.packetLossRate = Math.clamp(packetLossRate, 0.0, 1.0);
    }

    public String processPacketQueue() {
        if (packetQueue.isEmpty()) {
            return "No packets to process.";
        }
        StringBuilder resultLog = new StringBuilder();
        Packet packet;
        while ((packet = packetQueue.poll()) != null) {
            TcpConnectionState state = getOrCreateTcpState(packet.sourceNodeId(), packet.destinationNodeId());
            if (random.nextDouble() < packetLossRate) {
                state.onPacketLossTimeout();
                resultLog.append(String.format("❌ <b>LOST</b>: Pachet [%s] | Seq: %d | %s ➔ %s | Size: %dB | <i>Slow Start Reset</i><br>",
                        packet.id(), packet.sequenceNumber(), packet.sourceNodeId(), packet.destinationNodeId(), packet.sizeBytes()));
            } else {
                state.onAckReceived();
                resultLog.append(String.format("✔ <b>ACK</b>: Pachet [%s] | Seq: %d | %s ➔ %s | Size: %dB | Cwnd: %.2f<br>",
                        packet.id(), packet.sequenceNumber(), packet.sourceNodeId(), packet.destinationNodeId(), packet.sizeBytes(), state.getCwnd()));
            }
        }
        notifyListeners();
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

    @Scheduled(fixedRate = 2000)
    public void backgroundTick() {
        Packet packet;
        boolean updated = false;
        while ((packet = packetQueue.poll()) != null) {
            TcpConnectionState state = getOrCreateTcpState(packet.sourceNodeId(), packet.destinationNodeId());
            if (random.nextDouble() < packetLossRate) {
                state.onPacketLossTimeout();
                System.out.printf("[BackgroundTick] ❌ LOST packet %s (Seq: %d, Rate: %.0f%%). Reset Slow Start.\n", packet.id(), packet.sequenceNumber(), packetLossRate * 100);
            } else {
                state.onAckReceived();
                System.out.printf("[BackgroundTick] ✔️ DELIVERED packet %s (Seq: %d), New Cwnd: %.2f\n", packet.id(), packet.sequenceNumber(), state.getCwnd());
            }
            updated = true;
        }
        if (updated) {
            notifyListeners();
        }
    }

    public void enqueuePacket(Packet packet) {
        if (packet != null) {
            packetQueue.offer(packet);
        }
    }

    public void registerListener(Runnable listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Runnable listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        listeners.forEach(Runnable::run);
    }
}
