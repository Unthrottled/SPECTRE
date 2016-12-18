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

import mil.af.meis.mobilityaircraft._1_0.AircraftLongIdentityType;
import mil.af.meis.mobilityaircraft._1_0.ContractServiceBuild;
import mil.af.meis.mobilityaircraft._1_0.GetAircraftResponse;
import mil.af.meis.mobilityaircraft._1_0.MobilityAircraftType;
import mil.af.meis.mobilityairmission12140721.WrappedMissionsResponseType;
import mil.af.meis.mobilitycrew11120330.WrappedMobilityCrewResponseType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SpectrePayloadContainerFinderTest {
    SpectrePayloadFinder spectrePayloadFinder = new SpectrePayloadFinder();

    @Test
    public void testFindPayload() throws Exception {
        SpectrePayloadAccessor payloadContainer = spectrePayloadFinder.findPayload(GetAircraftResponse.class);
        MobilityAircraftType mobilityAircraftType = new MobilityAircraftType();
        mobilityAircraftType.setAircraftIdentity(new AircraftLongIdentityType());
        mobilityAircraftType.getAircraftIdentity().setAircraftTailNumber("Poober");
        MobilityAircraftType mobilityAircraftTypeTwo = new MobilityAircraftType();
        mobilityAircraftTypeTwo.setAircraftIdentity(new AircraftLongIdentityType());
        mobilityAircraftTypeTwo.getAircraftIdentity().setAircraftTailNumber("Poober");
        GetAircraftResponse.MobilityAircrafts mobilityAircrafts = new GetAircraftResponse.MobilityAircrafts();
        mobilityAircrafts.setMobilityAircraft(Arrays.asList(mobilityAircraftType, mobilityAircraftTypeTwo));
        GetAircraftResponse getAircraftResponse = new GetAircraftResponse();
        getAircraftResponse.setMobilityAircrafts(mobilityAircrafts);
        List<SpectrePayloadContainer> payloadContainers = payloadContainer.getPayloadFromWebResponse(getAircraftResponse);
        List<SpectrePayload> goodies = payloadContainers.stream()
                .map(SpectrePayloadContainer::getPayloads)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println();
        payloadContainer = spectrePayloadFinder.findPayload(ContractServiceBuild.class);
        System.out.println();
        payloadContainer = spectrePayloadFinder.findPayload(WrappedMobilityCrewResponseType.class);
        System.out.println();
        payloadContainer = spectrePayloadFinder.findPayload(WrappedMissionsResponseType.class);
        System.out.println();
    }
}