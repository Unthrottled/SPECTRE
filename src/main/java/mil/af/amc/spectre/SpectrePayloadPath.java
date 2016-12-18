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

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SpectrePayloadPath {
    private Class<?> classThatHasThePayload;
    private Field payloadField;
    private List<SpectrePayloadPath> payloadPathsFromField = null;

    public SpectrePayloadPath(Class<?> aClass, Field field, List<SpectrePayloadPath> payloadPathsFromField) {
        classThatHasThePayload = aClass;
        this.payloadField = field;
        this.payloadField.setAccessible(true);
        this.payloadPathsFromField = payloadPathsFromField;
    }

    public SpectrePayloadPath(Class<?> aClass, Field payloadField) {
        classThatHasThePayload = aClass;
        this.payloadField = payloadField;
    }

    public List<SpectrePayloadContainer> getPayloadFromPath(Object webServiceResponse) {
        if (webServiceResponse == null) {
            return Collections.emptyList();
        } else {
            return getPayloadFromObject(webServiceResponse);
        }
    }

    private List<SpectrePayloadContainer> getPayloadFromObject(Object webServiceResponse) {
        if (payloadPathsFromField == null) {
            return getPayloadFromField(webServiceResponse);
        } else {
            return getPayloadFromPaths(webServiceResponse);
        }
    }

    private List<SpectrePayloadContainer> getPayloadFromPaths(Object webServiceResponse) {
        return payloadPathsFromField.stream()
                .map(spectrePayloadPath -> {
                    try {
                        Object fieldWithPayload = payloadField.get(webServiceResponse);
                        return spectrePayloadPath.getPayloadFromPath(fieldWithPayload);
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException();
                    }
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<SpectrePayloadContainer> getPayloadFromField(Object webServiceResponse) {
        if (classThatHasThePayload.equals(webServiceResponse.getClass())){
            try {
                this.payloadField.setAccessible(true);
                Object payload = payloadField.get(webServiceResponse);
                return Collections.singletonList(new SpectrePayloadContainer(payload));
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Object Provided is not of type {0}, but of {1}.",
                    classThatHasThePayload.getName(), webServiceResponse.getClass().getName()));
        }
    }
}
