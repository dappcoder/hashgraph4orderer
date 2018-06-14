package com.dappcoder.grpc.client;

import com.dappcoder.grpc.server.ConsensusHandler;
import com.google.protobuf.ByteString;
import com.swirlds.platform.Address;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.hyperledger.fabric.protos.orderer.TransactionFeedGrpc;
import org.hyperledger.fabric.protos.orderer.TransactionOuterClass;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class OrdererClient implements ConsensusHandler {

    private final ManagedChannel channel;

    private final TransactionFeedGrpc.TransactionFeedBlockingStub blockingStub;

    public OrdererClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // TODO TLS disabled.
                .usePlaintext()
                .build());
    }

    OrdererClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = TransactionFeedGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void sendTransaction(String data) {

        TransactionOuterClass.CreateResponse response;
        try {
            TransactionOuterClass.Transaction transaction = TransactionOuterClass.Transaction.newBuilder().setPayload(ByteString.copyFrom(data, "UTF-8")).build();
            response = blockingStub.create(transaction);
        } catch (StatusRuntimeException e) {
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(long id, boolean consensus, Instant timestamp, byte[] transaction, Address address) {
        sendGossipedTransaction(id, consensus, timestamp, transaction, address);
        System.out.println("Sent consensus (ordered) message.");
    }


    public void sendGossipedTransaction(long id, boolean consensus, Instant timestamp, byte[] transaction, Address address) {
        try {
            TransactionOuterClass.GossipedTransaction gossiped = TransactionOuterClass.GossipedTransaction
                    .newBuilder()
                    .setId(id)
                    .setConsensus(consensus)
                    .setTimestamp(timestamp.toEpochMilli())
                    .setTransaction(ByteString.copyFrom(transaction))
//              .setAddressId(address.getId())
                    .build();
            blockingStub.handle(gossiped);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("RPC failed: {0}", e);
        }
    }
}
