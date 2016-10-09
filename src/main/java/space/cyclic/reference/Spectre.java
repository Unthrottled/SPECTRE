package space.cyclic.reference;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import java.lang.reflect.Proxy;

public class Spectre implements AutoCloseable {
    private final Class<? extends Service> serviceToProxy;
    private final SpectreWebService spectreWebService;

    /**
     * Service
     * Proxy
     * Ensures
     * Continual
     * Transactions
     * Reach
     * End-User
     * @param serviceToProxy
     */
    Spectre(Class<? extends Service> serviceToProxy) {
        this.serviceToProxy = serviceToProxy;
        spectreWebService = new SpectreWebService(serviceToProxy);
    }

    public BindingProvider getSpectreService(SpectreParameters spectreParameters) {
        SpectreConnection spectreConnection = new SpectreConnection(serviceToProxy, spectreParameters);
        BindingProvider webServiceConnection = spectreConnection.createConnection();
        return (BindingProvider) Proxy.newProxyInstance(
                webServiceConnection.getClass().getClassLoader(),
                webServiceConnection.getClass().getInterfaces(),
                new SpectreProxy(spectreWebService, spectreConnection));
    }

    @Override
    public void close() throws Exception {
        spectreWebService.close();
    }
}
