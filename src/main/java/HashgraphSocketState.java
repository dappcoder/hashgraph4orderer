import com.dappcoder.grpc.server.ConsensusHandler;
import com.swirlds.platform.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HashgraphSocketState implements SwirldState {

    private List<String> strings = new ArrayList<String>();

    private AddressBook addressBook;

    private List<ConsensusHandler> consensusHandlers = new ArrayList<>();

    public synchronized List<String> getStrings() {
        return strings;
    }

    public synchronized void addConsensusHandler(ConsensusHandler handler) {
        this.consensusHandlers.add(handler);
    }

    @Override
    public synchronized void init(Platform platform, AddressBook addressBook) {
        this.addressBook = addressBook;
    }

    @Override
    public synchronized AddressBook getAddressBookCopy() {
        return addressBook.copy();
    }

    @Override
    public synchronized void copyFrom(SwirldState old) {
        HashgraphSocketState oldHSState = (HashgraphSocketState) old;
        strings = new ArrayList<>(oldHSState.strings);
        addressBook = oldHSState.addressBook.copy();
        consensusHandlers = new ArrayList<>(oldHSState.consensusHandlers);
    }

    @Override
    public synchronized void handleTransaction(long id, boolean consensus,
                                               Instant timestamp, byte[] transaction, Address address) {
        if (consensus) {
            String message = new String(transaction, StandardCharsets.UTF_8);
            strings.add(message);
            consensusHandlers.forEach(handler -> handler.handle(id, consensus, timestamp, transaction, address));
        }
    }

    @Override
    public void noMoreTransactions() {

    }

    @Override
    public synchronized FastCopyable copy() {
        HashgraphSocketState copy = new HashgraphSocketState();
        copy.copyFrom(this);
        return copy;
    }

    @Override
    public synchronized void copyTo(FCDataOutputStream outStream) {
        try {
            Utilities.writeStringArray(outStream,
                    strings.toArray(new String[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized void copyFrom(FCDataInputStream inStream) throws IOException {
        try {
            strings = new ArrayList<String>(
                    Arrays.asList(Utilities.readStringArray(inStream)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
