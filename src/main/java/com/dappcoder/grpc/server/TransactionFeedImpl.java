package com.dappcoder.grpc.server;

import io.grpc.stub.StreamObserver;
import org.hyperledger.fabric.protos.orderer.TransactionFeedGrpc;
import org.hyperledger.fabric.protos.orderer.TransactionOuterClass;

import java.util.ArrayList;
import java.util.List;

public class TransactionFeedImpl extends TransactionFeedGrpc.TransactionFeedImplBase  {

    private final List<MessageHandler> messageHandlers = new ArrayList<>();

    public void addMessageHandler(MessageHandler handler) {
        messageHandlers.add(handler);
    }

    @Override
    public void create(TransactionOuterClass.Transaction request, StreamObserver<TransactionOuterClass.CreateResponse> responseObserver) {
        messageHandlers.forEach(handler -> {
            boolean accepted = handler.handle(request.getPayload().toByteArray());
            TransactionOuterClass.CreateResponse response = TransactionOuterClass.CreateResponse.newBuilder()
                    .setAccepted(accepted)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        });
    }

}
