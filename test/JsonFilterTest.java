import org.junit.* ;
import static org.junit.Assert.* ;

import com.hbm.devices.scan.AnnouncePath;
import com.hbm.devices.scan.filter.JsonFilter;
import com.hbm.devices.scan.FakeStringMessageMulticastReceiver;
import com.hbm.devices.scan.messages.Announce;
import java.util.Observable;
import java.util.Observer;

public class JsonFilterTest {

	@Test
	public void parseCorrectMessage() {
		final Closure closure = new Closure();
		FakeStringMessageMulticastReceiver fsmmr = new FakeStringMessageMulticastReceiver();
		JsonFilter jf = new JsonFilter();
		fsmmr.addObserver(jf);
		jf.addObserver(new Observer(){
			public void update(Observable o, Object arg) {
				closure.ap = (AnnouncePath)arg;
			}
		});
		fsmmr.emitSingleCorrectMessage();
		assertTrue(closure.ap != null);
	}
}

class Closure {
	public AnnouncePath ap;
}
