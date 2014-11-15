import static org.junit.Assert.assertTrue;

import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import com.hbm.devices.scan.CommunicationPath;
import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.MessageParser;
import com.hbm.devices.scan.messages.Response;

public class MessageParserTest {

    private CommunicationPath ap;
    private Response res;
    private FakeMessageReceiver fsmmr;

    @Before
    public void setup() {
        fsmmr = new FakeMessageReceiver();
        MessageParser jf = new MessageParser();
        fsmmr.addObserver(jf);
        jf.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (arg instanceof CommunicationPath) {
                    ap = (CommunicationPath) arg;
                } else if (arg instanceof Response) {
                    res = (Response) arg;
                }
            }
        });
    }

    /*
     * TESTING ANNOUNCE
     */
    @Test
    public void parseCorrectMessage() {
        fsmmr.emitSingleCorrectMessage();
        assertTrue(ap != null);
    }

    @Test
    public void parseInvalidJsonMessage() {
        fsmmr.emitInvalidJsonMessage();
        assertTrue(ap == null);
    }

    @Test
    public void parseEmptyMessage() {
        fsmmr.emitEmptyString();
        assertTrue(ap == null);
    }

    @Test
    public void parseNullMessage() {
        fsmmr.emitNull();
        assertTrue(ap == null);
    }

    @Test
    public void parseMissingDeviceMessage() {
        fsmmr.emitMissingDeviceMessage();
        assertTrue(ap == null);
    }

    @Test
    public void parseMissingDeviceUuidMessage() {
        fsmmr.emitMissingDeviceUuidMessage();
        assertTrue(ap == null);
    }

    @Test
    public void parseMissingParamsMessage() {
        fsmmr.emitMissingParamsMessage();
        assertTrue(ap == null);
    }

    @Test
    public void parseNoInterfaceNameMessage() {
        fsmmr.emitNoInterfaceNameMessage();
        assertTrue(ap == null);
    }

    @Test
    public void parseNoInterfaceMessage() {
        fsmmr.emitNoInterfaceMessage();
        assertTrue(ap == null);
    }

    @Test
    public void parseNoNetSettingsMessage() {
        fsmmr.emitNoNetSettingsMessage();
        assertTrue(ap == null);
    }

    @Test
    public void parseMissingRouterUuidMessage() {
        fsmmr.emitMissingRouterUuidMessage();
        assertTrue(ap == null);
    }

    /*
     * TESTING RESPONSE
     */

    @Test
    public void parseCorrectSuccessReponseMessage() {
        fsmmr.emitSingleCorrectSuccessResponseMessage("TEST-UUID");
        assertTrue(res != null);
    }

    @Test
    public void parseCorrectErrorReponseMessage() {
        fsmmr.emitSingleCorrectErrorResponseMessage();
        assertTrue(res != null);
    }

    @Test
    public void parseInvalidSuccessErrorReponseMessage() {
        fsmmr.emitInvalidErrorSuccessReponseMessage();
        assertTrue(res == null);
    }

    @Test
    public void parseNoSuccessIdResponseMessage() {
        fsmmr.emitNoSuccessIdResponseMessage();
        assertTrue(res == null);
    }

    @Test
    public void parseMissingErrorObjectResponseMessage() {
        fsmmr.emitMissingErrorObjectResponseMessage();
        assertTrue(res == null);
    }

    @Test
    public void parseNoErrorCodeResponseMessage() {
        fsmmr.emitNoErrorCodeResponseMessage();
        assertTrue(res == null);
    }

    @Test
    public void parseMissingErrorMessageReponseMessage() {
        fsmmr.emitMissingErrorMessageReponseMessage();
        assertTrue(res == null);
    }

    @Test
    public void parseNoErrorMessageResponseMessage() {
        fsmmr.emitNoErrorMessageResponseMessage();
        assertTrue(res == null);
    }

    // @Test(expected = IndexOutOfBoundsException.class)
    // public void parseBLA() {
    //
    // }
}
