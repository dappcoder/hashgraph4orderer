package com.dappcoder.grpc.server;

public interface ConsensusHandler {
    void handle(String consensus);
}
