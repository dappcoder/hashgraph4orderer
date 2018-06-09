package com.dappcoder.grpc.server;

import com.dappcoder.proto.hashgraph.CreateResponse;
import com.dappcoder.proto.hashgraph.Transaction;
import com.dappcoder.proto.hashgraph.TransactionFeedGrpc;
import com.swirlds.platform.Address;
import io.grpc.stub.StreamObserver;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TransactionFeedImpl extends TransactionFeedGrpc.TransactionFeedImplBase implements ConsensusHandler {

    private final List<GrpcServer.MessageHandler> messageHandlers = new ArrayList<>();

    public void addMessageHandler(GrpcServer.MessageHandler handler) {
        messageHandlers.add(handler);
    }

    @Override
    public void create(Transaction request, StreamObserver<CreateResponse> responseObserver) {
        messageHandlers.forEach(handler -> {
            boolean accepted = handler.handle(request.getPayload().toStringUtf8());
            CreateResponse response = CreateResponse.newBuilder()
                    .setAccepted(accepted)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        });
    }

    @Override
    public void handle(long id, boolean consensus, Instant timestamp, byte[] transaction, Address address) {
        // TODO Send back consensus
    }
}
