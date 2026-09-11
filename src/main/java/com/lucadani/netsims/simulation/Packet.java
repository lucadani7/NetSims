package com.lucadani.netsims.simulation;

public record Packet(String id, String sourceNodeId, String destinationNodeId, int sequenceNumber, int sizeBytes) {}
