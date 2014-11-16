import java.util.logging.Logger;

import com.hbm.devices.configure.ConfigCallback;
import com.hbm.devices.configure.ConfigQuery;
import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.Response;

public class SimpleCallback implements ConfigCallback {

    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    @Override
    public void onSuccess(ConfigQuery configQuery, Response response) {
        LOGGER.info("Success:\n");
        LOGGER.info(" result: " + response.getResult() + "\n");
    }

    @Override
    public void onError(ConfigQuery configQuery, Response response) {
        LOGGER.info("Error:\n");
        LOGGER.info(" code: " + response.getError().getCode() + "\n");
        LOGGER.info(" message: " + response.getError().getMessage() + "\n");
        LOGGER.info(" data: " + response.getError().getData() + "\n");
    }

    @Override
    public void onTimeout(ConfigQuery configQuery) {
        LOGGER.info("No response is received in " + configQuery.getTimeout() + "ms\n");
    }
}
