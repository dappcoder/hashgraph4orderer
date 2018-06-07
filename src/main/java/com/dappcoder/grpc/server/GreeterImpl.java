package com.dappcoder.grpc.server;

import com.dappcoder.proto.helloworld.GreeterGrpc;
import com.dappcoder.proto.helloworld.HelloReply;
import com.dappcoder.proto.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class GreeterImpl extends GreeterGrpc.GreeterImplBase implements ConsensusHandler {

    private final List<GrpcServer.MessageHandler> messageHandlers = new ArrayList<>();

    public void addMessageHandler(GrpcServer.MessageHandler handler) {
        messageHandlers.add(handler);
    }

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        messageHandlers.forEach(handler -> handler.handle(req.getName()));

        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void handle(String consensus) {
        System.out.println("todo: BUILDING REPLY WITH CONSENSUS");
    }
}
