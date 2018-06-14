package com.dappcoder.grpc.server;

public interface MessageHandler {

    boolean handle(byte[] message);

}
