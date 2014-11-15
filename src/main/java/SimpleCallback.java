

import com.hbm.devices.configure.ConfigCallback;
import com.hbm.devices.configure.ConfigQuery;
import com.hbm.devices.scan.messages.Response;

public class SimpleCallback implements ConfigCallback {

    @Override
    public void onSuccess(ConfigQuery configQuery, Response response) {
        System.out.println("Success: ");
        System.out.println(" result: " + response.getResult());

    }

    @Override
    public void onError(ConfigQuery configQuery, Response response) {
        System.out.println("Error:");
        System.out.println(" code: " + response.getError().getCode());
        System.out.println(" message: " + response.getError().getMessage());
        System.out.println(" data: " + response.getError().getData());
    }

    @Override
    public void onTimeout(ConfigQuery configQuery) {
        System.out.println("No response is received in " + configQuery.getTimeout() + "ms");

    }

}
