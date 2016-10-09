package space.cyclic.reference;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

public class SpectreConnection {
    private final Class<? extends Service> serviceToConnectTo;
    private final SpectreParameters spectreParameters;

    SpectreConnection(Class<? extends Service> serviceToConnectTo, SpectreParameters spectreParameters) {
        this.serviceToConnectTo = serviceToConnectTo;
        this.spectreParameters = spectreParameters;
    }

    public BindingProvider createConnection() {
        return null;
    }
}
