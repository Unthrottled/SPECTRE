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

import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;
import mil.af.amc.spectre.hazelcast.SpectreHazelcastSingleton;
import org.apache.log4j.Logger;

import javax.xml.ws.Service;
import java.lang.reflect.Method;
import java.util.Objects;

public abstract class SpectreMethod {
    private static final Logger LOGGER = Logger.getLogger(SpectreMethod.class);
    protected final Method methodToFake;
    private boolean hasPayload = false;
    protected final Class<? extends Service> serviceToProxy;

    public SpectreMethod(Method methodToFake, Class<? extends Service> serviceToProxy) {
        this.serviceToProxy = serviceToProxy;
        this.methodToFake = Objects.requireNonNull(methodToFake);
    }

    public Method getMethod() {
        return methodToFake;
    }

    public final Object invoke(Object... args) {
        if (hasPayload){
            return getResponseFromArguments(args);
        } else {
            return tryToCreateEmptyResponse();
        }
    }

    protected abstract Object getResponseFromArguments(Object... args);

    public abstract void assimilateResponse(Object webServiceResponse, Method method, Object... args);

    public String getMethodIdentifier(){
        String methodName = methodToFake.getName();
        String serviceName = serviceToProxy.getName();
        String serviceVersion = serviceToProxy.getPackage().getImplementationVersion();
        String spectreVersion = SpectreVersion.CURRENT.value();
        return methodName + serviceName + serviceVersion + spectreVersion;
    }

    protected  boolean isHasPayload(){
        return hasPayload;
    }

    protected  void setHasPayload(boolean hasPayload){
        this.hasPayload = hasPayload;
    }
    private Object tryToCreateEmptyResponse() {
        return EmptyResponseCreator.buildObject(methodToFake.getReturnType());
    }

    protected IMap<Long, Object> getWebResponseMap() {
        return SpectreHazelcastSingleton.INSTANCE.getHazelcastInstance().getMap(getMethodIdentifier() + "-RESPONSES");
    }

    protected IMap<String, Object> getPayloadMap(){
        return SpectreHazelcastSingleton.INSTANCE.getHazelcastInstance().getMap(getMethodIdentifier() + "-PAYLOAD");
    }

    protected IdGenerator getIdGenerator(){
        return SpectreHazelcastSingleton.INSTANCE.getHazelcastInstance().getIdGenerator(getMethodIdentifier() + "-RESPONSES");
    }
}
