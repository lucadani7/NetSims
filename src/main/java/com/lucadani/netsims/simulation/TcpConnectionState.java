package com.lucadani.netsims.simulation;

import com.lucadani.netsims.enums.TcpPhase;
import lombok.Data;

@Data
public class TcpConnectionState {
    private static final double SEGMENT_UNIT = 1.0;

    private String connectionId;
    private double cwnd;
    private double ssthresh;
    private TcpPhase phase;
    private int duplicateAckCount;

    public TcpConnectionState(String connectionId) {
        this.connectionId = connectionId;
        this.cwnd = 1.0;
        this.ssthresh = 16.0;
        this.phase = TcpPhase.SLOW_START;
        this.duplicateAckCount = 0;
    }

    private boolean isInSlowStart() {
        return cwnd < ssthresh;
    }

    private double calculateCongestionAvoidanceIncrement() {
        return SEGMENT_UNIT / cwnd;
    }

    public void onDuplicateAckReceived() {
        ++duplicateAckCount;
        if (phase == TcpPhase.FAST_RECOVERY) {
            cwnd += SEGMENT_UNIT;
        } else if (duplicateAckCount == 3) {
            ssthresh = Math.max(cwnd / 2.0, 2.0);
            cwnd = ssthresh + 3.0;
            phase = TcpPhase.FAST_RECOVERY;
        }
    }

    public void onAckReceived() {
        switch (phase) {
            case FAST_RECOVERY -> {
                cwnd = ssthresh;
                phase = TcpPhase.CONGESTION_AVOIDANCE;
            }
            case SLOW_START, CONGESTION_AVOIDANCE -> {
                if (isInSlowStart()) {
                    cwnd += 1.0;
                } else {
                    cwnd += calculateCongestionAvoidanceIncrement();
                    phase = TcpPhase.CONGESTION_AVOIDANCE;
                }
            }
        }
        duplicateAckCount = 0;
    }

    public void onPacketLossTimeout() {
        ssthresh = Math.max(cwnd / 2.0, 2.0);
        cwnd = SEGMENT_UNIT;
        phase = TcpPhase.SLOW_START;
    }
}
