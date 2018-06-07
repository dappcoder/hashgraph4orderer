import com.swirlds.platform.*;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HashgraphSocketState implements SwirldState {

    private List<String> strings = new ArrayList<String>();

    private AddressBook addressBook;

    public synchronized List<String> getStrings() {
        return strings;
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
        addressBook = ((HashgraphSocketState) old).addressBook.copy();
    }

    @Override
    public synchronized void handleTransaction(long l, boolean b, Instant instant, byte[] bytes, Address address) {

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
