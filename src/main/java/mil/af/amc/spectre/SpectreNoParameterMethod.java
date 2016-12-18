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

import javax.xml.ws.Service;
import java.lang.reflect.Method;

public class SpectreNoParameterMethod extends SpectreMethod {

    public SpectreNoParameterMethod(Method methodToFake, Class<? extends Service> serviceToProxy) {
        super(methodToFake, serviceToProxy);
    }

    public void assimilateResponse(Object webServiceResponse, Method method, Object... args) {
        loadMethodWithResponse(webServiceResponse, method);
        updateMethodState(webServiceResponse);
        refinePayloadObject();
    }

    protected Object getResponseFromArguments(Object... args) {
        return null;
    }

    private void refinePayloadObject() {
        if (isHasPayload()){
            //Run through responses and try to refine the payload object.
            IMap<String, Object> webServiceMethodPayload = getPayloadMap();
            Object payloadObject = webServiceMethodPayload.get(getMethodIdentifier());
        }
    }

    private void updateMethodState(Object webServiceResponse) {
        if (!isHasPayload() && webServiceResponse != null){
            setHasPayload(true);
        }
    }

    private void loadMethodWithResponse(Object webServiceResponse, Method method, Object... args) {
        if (isResponseUseful(webServiceResponse)){
            IMap<Long, Object> webResponseMap = getWebResponseMap();
            webResponseMap.put(getIdGenerator().newId(), webServiceResponse);
        }
    }

    private boolean isResponseUseful(Object webServiceResponse) {
        //Need to determine if is of same class.
        return webServiceResponse != null;
    }
}
