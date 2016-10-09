package space.cyclic.reference;

import javax.xml.ws.Service;

public class SpectreFactory {

    public static Spectre createSpectre(Class<? extends Service> serviceToProxy) {
        return new Spectre(serviceToProxy);
    }

    public static void main(String... args) {
//        Spectre spectre = SpectreFactory.createSpectre()
    }
}
