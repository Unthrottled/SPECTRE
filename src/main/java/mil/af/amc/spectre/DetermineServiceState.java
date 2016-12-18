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
import java.lang.reflect.Method;

public final class DetermineServiceState implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(DetermineServiceState.class);
    private static final ProxyIdentifier PROXY_IDENTIFIER = new ProxyIdentifier();
    private static final Coroner CORONER = new Coroner();
    private final Method methodToCall;
    private final Object[] args;
    private final BindingProvider serviceBindingProvider;
    private final ServiceState serviceState;

    public DetermineServiceState(BindingProvider serviceBindingProvider, ServiceState serviceState, Method methodToCall, Object[] args) {
        this.methodToCall = methodToCall;
        this.serviceState = serviceState;
        this.args = args;
        this.serviceBindingProvider = serviceBindingProvider;
    }

    @Override
    public void run() {
        try {
            methodToCall.invoke(serviceBindingProvider, args);
            serviceState.serviceReAnimated();
            LOGGER.debug("Service: " + PROXY_IDENTIFIER.getName(serviceBindingProvider) + "\r Method: " +
                    methodToCall.getName() + " Has Recovered");
        } catch (Exception | Error throwAway) {
            if (CORONER.didServiceRecover(throwAway, methodToCall)){
                serviceState.serviceReAnimated();
            } else {
                serviceState.serviceDied();
            }
        }
    }
}