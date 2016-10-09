package space.cyclic.reference;

import javax.xml.ws.Service;
import java.lang.reflect.Method;
import java.util.HashMap;

public class SpectreWebService implements AutoCloseable {
    private final HashMap<Method, SpectreMethod> webMethods = new HashMap<Method, SpectreMethod>();

    public SpectreWebService(Class<? extends Service> serviceToProxy) {

    }

    public Object invoke(Method method, Object[] args) {
        return null;
    }

    public Object assimilateResponse(Object response, Method method, Object... methodArguments) {
        return response;
    }

    @Override
    public void close() throws Exception {

    }
}
