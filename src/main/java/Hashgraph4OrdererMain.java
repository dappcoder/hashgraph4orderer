import com.dappcoder.grpc.server.ConsensusHandler;
import com.dappcoder.grpc.server.GrpcServer;
import com.swirlds.platform.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;

public class Hashgraph4OrdererMain implements SwirldMain, ConsensusHandler {


    private Platform platform;

    private Console console;

    private GrpcServer server;


    @Override
    public void init(Platform platform, long l) {
        this.platform = platform;
        this.console = platform.createConsole(true);

        int consensusPort = platform.getAddress().getPortExternalIpv4();
        int grpcPort = consensusPort + 1000;
        server = new GrpcServer(grpcPort);

        console.out.println("Initialized " + platform.getAddress().getSelfName());
    }

    @Override
    public void run() {
        try {
            server.start();
            server.getService().addMessageHandler(this::sendAsTransaction);
            Hashgraph4OrdererState state = (Hashgraph4OrdererState) platform.getState();
            state.addConsensusHandler(this);
            server.blockUntilShutdown();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Could not start GRPC Server", e);
        }
    }

    private boolean sendAsTransaction(byte[] message) {
        return this.platform.createTransaction(message);
    }

    @Override
    public void preEvent() {

    }

    @Override
    public SwirldState newState() {
        Hashgraph4OrdererState state = new Hashgraph4OrdererState();
        state.addConsensusHandler(this);
        return state;
    }

    public static void main(String[] args) {
        Browser.main(args);
    }

    @Override
    public void handle(long id, boolean consensus, Instant timestamp, byte[] transaction, Address address) {
        if (console != null) {
            try {
                console.out.println("CONSENSUS: \n" + new String(transaction, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
