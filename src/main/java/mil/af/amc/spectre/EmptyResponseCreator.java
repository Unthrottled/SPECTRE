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

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public final class EmptyResponseCreator {
    private static final Logger LOGGER = Logger.getLogger(SpectreMethod.class);

    public static Object buildObject(Class<?> responseType) {
        try {
            if (isPrimitive(responseType)) {
                if (responseType.equals(String.class)) {
                    return "PooButt";
                } else if (responseType.equals(Integer.class) ||
                        responseType.equals(Double.class) || responseType.equals(Float.class) ||
                        responseType.equals(Character.class)) {
                    return 9001;
                } else {
                    return false;
                }
            } else if (XMLGregorianCalendar.class.isAssignableFrom(responseType)) {
                return null;
            } else {
                Object thing = responseType.getConstructor().newInstance();
                for (Field field : responseType.getDeclaredFields()) {
                    if ((field.getModifiers() & Modifier.FINAL) != Modifier.FINAL) {
                        field.setAccessible(true);
                        field.set(thing, buildObject(field.getType()));
                    }
                }
                return thing;
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.error("Unable to create default response!!", e);
        }

        return null;
    }

    private static boolean isPrimitive(Class<?> t) {
        return t.isPrimitive() ||
                t.equals(String.class) || t.equals(Boolean.class) ||
                t.equals(Integer.class) ||
                t.equals(Double.class) || t.equals(Float.class) ||
                t.equals(Character.class);
    }
}
