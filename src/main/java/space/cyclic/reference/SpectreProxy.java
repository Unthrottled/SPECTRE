package space.cyclic.reference;

import javax.xml.ws.BindingProvider;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpectreProxy implements InvocationHandler, AutoCloseable {
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final ServiceStatus currentServiceStatus = new ServiceStatus();
    private final SpectreWebService spectreWebService;
    private final SpectreConnection spectreConnection;

    public SpectreProxy(SpectreWebService spectreWebService, SpectreConnection spectreConnection) {
        this.spectreWebService = spectreWebService;
        this.spectreConnection = spectreConnection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (currentServiceStatus.isServiceAlive()) {
            BindingProvider webConnection = spectreConnection.createConnection();
            Object webServiceResponse = method.invoke(webConnection, method, args);
            return spectreWebService.assimilateResponse(webServiceResponse, method, args);
        } else {
            return spectreWebService.invoke(method, args);
        }
    }

    @Override
    public void close() throws Exception {
        executorService.shutdown();
    }
}
