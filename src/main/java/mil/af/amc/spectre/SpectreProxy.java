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

import org.apache.log4j.Logger;

import javax.xml.ws.BindingProvider;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SpectreProxy implements InvocationHandler, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(SpectreProxy.class);
    private static final ProxyIdentifier PROXY_IDENTIFIER = new ProxyIdentifier();
    private static final Coroner CORONER = new Coroner();
    private static final ServiceState SERVICE_STATE = new ServiceState();
    private final ProxyParameters proxyParameters;
    private final TotallyNotConfigurator totallyNotConfigurator;
    private final SpectreWebService spectreWebService;
    private final ExecutorService executorService;

    SpectreProxy(ProxyParameters proxyParameters, SpectreWebService spectreWebService, TotallyNotConfigurator totallyNotConfigurator) {
        this.proxyParameters = proxyParameters;
        this.spectreWebService = spectreWebService;
        this.totallyNotConfigurator = totallyNotConfigurator;
        executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (SERVICE_STATE.isServiceAlive()) {
            return tryToCallWebService(method, args);
        } else {
            return callSpectreService(method, args);
        }
    }

    private Object callSpectreService(Method method, Object[] args) throws SpectreException {
        executorService.submit(new DetermineServiceState(totallyNotConfigurator.createBindingProvider(proxyParameters),
                SERVICE_STATE, method, args));
        return spectreWebService.invoke(method, args);
    }

    private Object tryToCallWebService(Method method, Object[] args) throws SpectreException {
        BindingProvider bindingProvider = totallyNotConfigurator.createBindingProvider(proxyParameters);
        try {
            LOGGER.debug("Invoking proxy to Service: " + PROXY_IDENTIFIER.getName(bindingProvider) + " Method invoked: " +
                    method.getName());
            return spectreWebService.assimilateResponse(method.invoke(bindingProvider, args), method, args);
        } catch (Exception | Error serviceException) {
            if (CORONER.didServiceDie(serviceException, method)) {
                SERVICE_STATE.serviceDied();
                LOGGER.debug("Service: " + PROXY_IDENTIFIER.getName(bindingProvider) + " Method invoked: " +
                        method.getName() + " is dead Jim....", serviceException);
            } else {
                SERVICE_STATE.serviceReAnimated();
                LOGGER.debug("Service: " + PROXY_IDENTIFIER.getName(bindingProvider) + " Method invoked: " +
                        method.getName() + " Responded with valid exception", serviceException);
            }
            throw new SpectreException(serviceException);
        }
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
