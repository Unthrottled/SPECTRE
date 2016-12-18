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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpectrePayloadAccessor {
    private Class<?> classThatHasThePayload;
    private Collection<SpectrePayloadPath> spectrePayloadPaths;

    public SpectrePayloadAccessor(Class<?> classThatHasThePayload, List<SpectrePayloadPath> spectrePayloadPaths) {
        this.classThatHasThePayload = classThatHasThePayload;
        this.spectrePayloadPaths = spectrePayloadPaths;
    }

    public List<SpectrePayloadContainer> getPayloadFromWebResponse(Object webServiceResponse) {
        Object webServiceResponseObject = Objects.requireNonNull(webServiceResponse);
        if (isRightObject(webServiceResponse)) {
            return getPayloadFromObject(webServiceResponseObject);
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Object Provided is not of type {0}, but of {1}.",
                    classThatHasThePayload.getName(), webServiceResponseObject.getClass().getName()));
        }
    }

    private List<SpectrePayloadContainer> getPayloadFromObject(Object webServiceResponseObject) {
        if (spectrePayloadPaths == null || spectrePayloadPaths.isEmpty()) {
            return Collections.singletonList(new SpectrePayloadContainer(webServiceResponseObject));
        } else {
            return getPayloadCollectionFromNestedField(webServiceResponseObject);
        }
    }

    private List<SpectrePayloadContainer> getPayloadCollectionFromNestedField(Object webServiceResponseObject) {
        return spectrePayloadPaths.stream()
                .map(spectrePayloadPath -> spectrePayloadPath.getPayloadFromPath(webServiceResponseObject))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private boolean isRightObject(Object webServiceResponse) {
        return classThatHasThePayload.isAssignableFrom(webServiceResponse.getClass());
    }
}
