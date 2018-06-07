import com.dappcoder.grpc.server.GrpcServer;
import com.swirlds.platform.*;
import com.txmq.exo.core.ExoPlatformLocator;

import java.io.IOException;

public class HashgraphSocketMain implements SwirldMain {

    @Override
    public void init(Platform platform, long l) {
        Console console = platform.createConsole(true);
        console.out.println("Initialized " + platform.getAddress().getSelfName());

        long selfId = platform.getAddress().getId();

        int port = platform.getState().getAddressBookCopy().getAddress(selfId).getPortExternalIpv4() + 1000;

        final GrpcServer server = new GrpcServer();
        try {
            server.start(port);
            server.blockUntilShutdown();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Could not start GRPC Server", e);
        }

    }

    @Override
    public void run() {

    }

    @Override
    public void preEvent() {

    }

    @Override
    public SwirldState newState() {
        return new HashgraphSocketState();
    }

    public static void main(String[] args) {
        Browser.main(args);
    }
}
