import org.junit.* ;
import static org.junit.Assert.* ;

import com.hbm.devices.scan.CommunicationPath;
import com.hbm.devices.scan.MessageParser;
import com.hbm.devices.scan.filter.Filter;
import com.hbm.devices.scan.filter.FamilytypeMatch;
import com.hbm.devices.scan.FakeMessageReceiver;
import java.util.Observable;
import java.util.Observer;

public class FilterTest {

	private CommunicationPath ap;
	private FakeMessageReceiver fsmmr;

	@Before
	public void setup() {
		fsmmr = new FakeMessageReceiver();
		MessageParser jf = new MessageParser();
		fsmmr.addObserver(jf);
		String[] families = {"QuantumX"};
		Filter ftFilter = new Filter(new FamilytypeMatch(families));
		jf.addObserver(ftFilter);
		ftFilter.addObserver(new Observer(){
			public void update(Observable o, Object arg) {
				ap = (CommunicationPath)arg;
			}
		});
	}

	@Test
	public void parseMissingFamilyTypeMessage() {
		fsmmr.emitMissingFamilyTypeMessage();
		assertTrue(ap == null);
	}
}

