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

import javax.xml.ws.soap.SOAPFaultException;
import java.lang.reflect.Method;

public final class Coroner {
    public boolean didServiceRecover(Throwable exceptionFromServiceCall, Method method){
        return !didServiceDie(exceptionFromServiceCall, method);
    }

    public boolean didServiceDie(Throwable exceptionFromServiceCall, Method method) {
        for (Class<?> e : method.getExceptionTypes()) {
            if (e.isAssignableFrom(exceptionFromServiceCall.getCause().getClass()) || e.isAssignableFrom(exceptionFromServiceCall.getClass())) {
                return true;
            }
        }

        if (exceptionFromServiceCall instanceof SOAPFaultException) {
            SOAPFaultException soapFaultException = (SOAPFaultException) exceptionFromServiceCall.getCause();
            String message = soapFaultException.getCause().getMessage();
            String faultCode = soapFaultException.getFault().getFaultCode();
            if (!message.contains("4030") &&
                    (faultCode.toLowerCase().contains("client") ||
                            faultCode.toLowerCase().contains("server"))) {
                return true;
            }
        }


        return false;
    }
}
