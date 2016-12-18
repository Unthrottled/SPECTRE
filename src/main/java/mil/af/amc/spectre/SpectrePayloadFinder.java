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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpectrePayloadFinder {
    private static final PayloadPredicate PAYLOAD_PREDICATE = new PayloadPredicate();

    public SpectrePayloadAccessor findPayload(Class<?> webServiceResponseClass) {
        if (webServiceResponseClass != null) {
            return getPayload(webServiceResponseClass);
        }
        return null;
    }

    private SpectrePayloadAccessor getPayload(Class<?> webServiceResponse) {
        List<SpectrePayloadPath> payloadPaths = Optional.ofNullable(getPayLoadPath(webServiceResponse))
                .orElseGet(Collections::emptyList);
        return new SpectrePayloadAccessor(webServiceResponse, payloadPaths);
    }

    private List<SpectrePayloadPath> getPayLoadPath(Class<?> webServiceResponse) {
        Collection<Field> possiblePayloadFields = Stream.of(webServiceResponse.getDeclaredFields())
                .filter(field -> {
                    Class<?> fieldType = field.getType();
                    System.out.println(fieldType.getName() + " " + fieldType.getPackage());
                    return !(fieldType.isPrimitive() ||
                            fieldType.isEnum() ||
                            "java.lang".equals(getPackageName(fieldType)));
                })
                .collect(Collectors.toList());

        //Class Has No Possible That Can Contain a Payload
        //Therefore the root class must be the payload
        if (possiblePayloadFields.isEmpty()) {
            return null;
        }

        //Class Has Fields that could contain a payload
        Collection<Field> payloadFields = possiblePayloadFields.stream()
                .filter(field -> PAYLOAD_PREDICATE.test(field.getClass()))
                .collect(Collectors.toList());

        if (payloadFields.isEmpty()) {

            //Try to Find the Payload Nested In the Fields
            List<SpectrePayloadPath> payloadPaths = possiblePayloadFields.stream()
                    .map(field -> {
                        List<SpectrePayloadPath> payloadPathsFromField = getPayLoadPath(field.getType());
                        if (payloadPathsFromField == null) {
                            return null;
                        } else {
                            return new SpectrePayloadPath(webServiceResponse, field, payloadPathsFromField);
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (payloadPaths.isEmpty()) {
                //NoPayloads found so the root must be the payload.
                return null;
            } else {
                //Hey Alright Payloads!!
                return payloadPaths;
            }

        } else {
            //This class has a payload return to the top!
            return payloadFields.stream().map(field -> new SpectrePayloadPath(webServiceResponse, field)).collect(Collectors.toList());
        }
    }

    private CharSequence getPackageName(Class<?> fieldType) {
        if (fieldType != null && fieldType.getPackage() != null) return fieldType.getPackage().getName();
        return null;
    }
}
