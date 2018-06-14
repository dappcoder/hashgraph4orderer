package com.dappcoder.grpc.client;

import com.dappcoder.grpc.server.ConsensusHandler;
import com.google.protobuf.ByteString;
import com.swirlds.platform.Address;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.hyperledger.fabric.protos.orderer.OrdererConsensus;
import org.hyperledger.fabric.protos.orderer.OrdererServiceGrpc;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class OrdererClient implements ConsensusHandler {

    private final ManagedChannel channel;

    private final OrdererServiceGrpc.OrdererServiceBlockingStub blockingStub;

    public OrdererClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // TODO TLS disabled.
                .usePlaintext()
                .build());
    }

    OrdererClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = OrdererServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Override
    public void handle(long id, boolean consensus, Instant timestamp, byte[] transaction, Address address) {
        // TODO check that the server is there first.
        sendGossipedTransaction(id, consensus, timestamp, transaction, address);
    }


    public void sendGossipedTransaction(long id, boolean consensus, Instant timestamp, byte[] transaction, Address address) {
        try {
            OrdererConsensus.ConsensusTransaction gossiped = OrdererConsensus.ConsensusTransaction
                    .newBuilder()
                    .setId(id)
                    .setConsensus(consensus)
                    .setTimestamp(timestamp.toEpochMilli())
                    .setTransaction(ByteString.copyFrom(transaction))
//              .setAddressId(address.getId())
                    .build();
            blockingStub.consensus(gossiped);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("RPC failed: {0}", e);
        }
    }
}
