package com.dappcoder.grpc.server;

import io.grpc.stub.StreamObserver;
import org.hyperledger.fabric.protos.orderer.Hashgraph;
import org.hyperledger.fabric.protos.orderer.HashgraphFeedGrpc;

import java.util.ArrayList;
import java.util.List;

public class HashgraphFeedImpl extends HashgraphFeedGrpc.HashgraphFeedImplBase {

    private final List<HashgraphFeedHandler> handlers = new ArrayList<>();

    public void addMessageHandler(HashgraphFeedHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void create(Hashgraph.Transaction request, StreamObserver<Hashgraph.CreateResponse> responseObserver) {
        handlers.forEach(handler -> {
            boolean accepted = handler.handle(request.getPayload().toByteArray());
            Hashgraph.CreateResponse response = Hashgraph.CreateResponse.newBuilder()
                    .setAccepted(accepted)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        });
    }

}
