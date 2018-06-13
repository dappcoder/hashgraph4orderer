package com.dappcoder.grpc.server;

import com.swirlds.platform.Address;
import io.grpc.stub.StreamObserver;
import org.hyperledger.fabric.protos.orderer.TransactionFeedGrpc;
import org.hyperledger.fabric.protos.orderer.TransactionOuterClass;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TransactionFeedImpl extends TransactionFeedGrpc.TransactionFeedImplBase implements ConsensusHandler {

    private final List<GrpcServer.MessageHandler> messageHandlers = new ArrayList<>();

    public void addMessageHandler(GrpcServer.MessageHandler handler) {
        messageHandlers.add(handler);
    }

    @Override
    public void create(TransactionOuterClass.Transaction request, StreamObserver<TransactionOuterClass.CreateResponse> responseObserver) {
        messageHandlers.forEach(handler -> {
            boolean accepted = handler.handle(request.getPayload().toStringUtf8());
            TransactionOuterClass.CreateResponse response = TransactionOuterClass.CreateResponse.newBuilder()
                    .setAccepted(accepted)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        });
    }

    @Override
    public void handle(long id, boolean consensus, Instant timestamp, byte[] transaction, Address address) {

        // TODO Send back consensus
        System.out.println("TODO: Sending consensus (ordered) message");
    }
}
