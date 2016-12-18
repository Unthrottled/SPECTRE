package mil.af.amc.spectre;

// ///////////////////////////////////////////////////////////////////////////
// SECURITY CLASSIFICATION: UNCLASSIFIED
// //////////////////////////////////////////////////////////////////////////
//
// UNLIMITED RIGHTS
//
// DFARS Clause reference 252.227-7013(a)(16) and 252.227-7014(a)(16)
//
// Unlimited Rights.  The Government has the right to use, modify, reproduce, release, perform
// display or disclose this (technical data or computer software) in whole or in part, in
// any manner, and for any purpose whatsoever, and to have or authorize others to do so.
//
// Distribution Statement D. Distribution authorized to the Department of Defense and
// U.S. DoD contractors only in support of US DoD efforts.  Other requests shall be
// referred to the Program Executive Officer, USTRANSCOM
//
// Warning: This document contains data whose export is restricted by the Arms Export
// Control Act (Title 22, U.S.C., Section 2751, et seq.) as amended, or the Export Administration
// Act (Title 50, U.S.C., App 2401 et seq.) as amended. Violations of these Export Administration
// are subject to severe criminal and civil penalties.  Disseminate in accordance with
// provisions of DoD directive 5230.25

import javax.xml.ws.Service;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpectreWebService implements AutoCloseable {
    private final Map<Method, SpectreMethod> methodsToProxy;
    private final Class<? extends Service> serviceToProxy;

    SpectreWebService(Class<? extends Service> serviceToProxy) {
        this.serviceToProxy = serviceToProxy;

        SpectreMethodFactory spectreMethodFactory = new SpectreMethodFactory(serviceToProxy);
        Class<?> webService = ServiceClassMethods.findProviderCreatorMethod(serviceToProxy).getReturnType();
        methodsToProxy = Stream.of(webService.getDeclaredMethods())
                .map(spectreMethodFactory::newMethod)
                .collect(Collectors.toMap(SpectreMethod::getMethod,
                        spectreMethod -> spectreMethod,
                        (spectreMethodOne, spectreMethodTwo) -> spectreMethodOne));
    }

    public Object invoke(Method realWebMethod, Object... args) {
        if (methodsToProxy.containsKey(realWebMethod)) {
            return methodsToProxy.get(realWebMethod).invoke(args);
        }
        return null;
    }

    public Object assimilateResponse(Object webServiceResponse, Method method, Object... args) {
        assimilate(webServiceResponse, method, args);
        return webServiceResponse;
    }

    private void assimilate(Object webServiceResponse, Method method, Object... args) {
        if (methodsToProxy.containsKey(method)) {
            methodsToProxy.get(method).assimilateResponse(webServiceResponse, method, args);
        }
    }

    @Override
    public void close() {

    }
}
