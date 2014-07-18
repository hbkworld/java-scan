import org.junit.* ;
import static org.junit.Assert.* ;

import com.hbm.devices.scan.CommunicationPath;
import com.hbm.devices.scan.MessageParser;
import com.hbm.devices.scan.FakeMessageReceiver;
import java.util.Observable;
import java.util.Observer;

public class MessageParserTest {

	private CommunicationPath ap;
	private FakeMessageReceiver fsmmr;

	@Before
	public void setup() {
		fsmmr = new FakeMessageReceiver();
		MessageParser jf = new MessageParser();
		fsmmr.addObserver(jf);
		jf.addObserver(new Observer(){
			public void update(Observable o, Object arg) {
				ap = (CommunicationPath)arg;
			}
		});
	}

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
}

