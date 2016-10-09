package space.cyclic.reference;

import javax.xml.ws.Service;
import java.util.HashMap;

public class SpectreServiceFactory {
    private static final HashMap<Class<? extends Service>, Service> serviceInstances = new HashMap<>();

    public static Service getServiceInstance(Class<? extends Service> serviceToCreate) throws SpectreException {
        try {
            if (!serviceInstances.containsKey(serviceToCreate)) {
                serviceInstances.put(serviceToCreate, serviceToCreate.newInstance());
            }
            return serviceInstances.get(serviceInstances);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SpectreException(e);
        }
    }
}
