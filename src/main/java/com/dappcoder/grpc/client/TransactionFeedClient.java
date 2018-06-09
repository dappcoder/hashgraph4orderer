/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dappcoder.grpc.client;

import com.dappcoder.proto.hashgraph.CreateResponse;
import com.dappcoder.proto.hashgraph.Transaction;
import com.dappcoder.proto.hashgraph.TransactionFeedGrpc;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionFeedClient {

  private static final Logger logger = Logger.getLogger(TransactionFeedClient.class.getName());

  private final ManagedChannel channel;
  private final TransactionFeedGrpc.TransactionFeedBlockingStub blockingStub;

  /** Construct client connecting to HelloWorld server at {@code host:port}. */
  public TransactionFeedClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build());
  }

  /** Construct client for accessing RouteGuide server using the existing channel. */
  TransactionFeedClient(ManagedChannel channel) {
    this.channel = channel;
    blockingStub = TransactionFeedGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /** Say hello to server. */
  public void sendTransaction(String data) {
    logger.info("Will try to send transaction " + data + " ...");

    CreateResponse response;
    try {
      Transaction transaction = Transaction.newBuilder().setPayload(ByteString.copyFrom(data, "UTF-8")).build();
      response = blockingStub.create(transaction);
      logger.info("Transaction accepted: " + response.getAccepted());
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }


  public static void main(String[] args) throws Exception {
    TransactionFeedClient client = new TransactionFeedClient("localhost", 51207);
    try {
      /* Access a service running on the local machine on port 50051 */
      String data = "world";
      if (args.length > 0) {
        data = args[0]; /* Use the arg as the name to sendTransaction if provided */
      }
      client.sendTransaction(data);
    } finally {
      client.shutdown();
    }
  }
}
