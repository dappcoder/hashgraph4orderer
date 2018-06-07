package com.dappcoder.grpc.server;

import com.dappcoder.proto.helloworld.GreeterGrpc;
import com.dappcoder.proto.helloworld.HelloReply;
import com.dappcoder.proto.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class GreeterImpl extends GreeterGrpc.GreeterImplBase implements ConsensusHandler {

    private final List<GrpcServer.MessageHandler> messageHandlers = new ArrayList<>();

    private final List<RegistryEntry> registry = new ArrayList<>();

    public void addMessageHandler(GrpcServer.MessageHandler handler) {
        messageHandlers.add(handler);
    }

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        messageHandlers.forEach(handler -> handler.handle(req.getName()));
        registry.add(new RegistryEntry(responseObserver, req));
    }

    @Override
    public void handle(String consensus) {
        System.out.println("todo: BUILDING REPLY WITH CONSENSUS");

        registry.forEach(entry -> {
            HelloReply reply = HelloReply.newBuilder().setMessage("CONSENSUS Hello " + entry.getRequest().getName()).build();
            entry.getObserver().onNext(reply);
            entry.getObserver().onCompleted();
        });

    }

    class RegistryEntry {

        private StreamObserver<HelloReply> observer;

        private HelloRequest request;

        public RegistryEntry(StreamObserver<HelloReply> observer, HelloRequest request) {
            this.observer = observer;
            this.request = request;
        }

        public StreamObserver<HelloReply> getObserver() {
            return observer;
        }

        public HelloRequest getRequest() {
            return request;
        }
    }
}
