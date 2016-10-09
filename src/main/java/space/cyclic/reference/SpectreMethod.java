package space.cyclic.reference;

import java.lang.reflect.Method;

public class SpectreMethod {
    private final Method methodToRecreate;

    SpectreMethod(Method methodToRecreate) {
        this.methodToRecreate = methodToRecreate;
    }

    public Object invoke(Object... args) {
        return null;
    }
}
