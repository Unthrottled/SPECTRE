package space.cyclic.reference;

import javax.xml.ws.BindingProvider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServiceCorner implements Runnable {
    private final BindingProvider webServiceConnection;
    private final Method webMethodToInvoke;
    private final Object[] webMethodArguments;

    public ServiceCorner(BindingProvider webServiceConnection, Method webMethodToInvoke, Object[] webMethodArguments) {
        this.webServiceConnection = webServiceConnection;
        this.webMethodToInvoke = webMethodToInvoke;
        this.webMethodArguments = webMethodArguments;
    }

    @Override
    public void run() {
        try {
            webMethodToInvoke.invoke(webServiceConnection, webMethodArguments);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
