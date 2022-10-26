package com.github.cargoclean.core.usecase.handling;

import com.github.cargoclean.core.GenericCargoError;
import com.github.cargoclean.core.model.Constants;
import com.github.cargoclean.core.model.cargo.TrackingId;
import com.github.cargoclean.core.model.handling.HandlingEvent;
import com.github.cargoclean.core.model.handling.HandlingEventType;
import com.github.cargoclean.core.model.location.UnLocode;
import com.github.cargoclean.core.model.voyage.VoyageNumber;
import com.github.cargoclean.core.port.operation.PersistenceGatewayOutputPort;
import com.github.cargoclean.core.port.presenter.handling.HandlingPresenterOutputPort;
import com.github.cargoclean.core.validator.InvalidDomainObjectError;
import com.github.cargoclean.core.validator.Validator;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
public class HandlingUseCase implements HandlingInputPort {

    private final HandlingPresenterOutputPort presenter;

    private final Validator validator;

    private final PersistenceGatewayOutputPort gatewayOps;

    @Override
    public void recordHandlingEvent(String voyageNumberStr, String locationStr, String cargoIdStr,
                                    Instant completionTime, HandlingEventType type) {

        TrackingId cargoId;
        HandlingEvent handlingEvent;
        try {

            // parse identifiers
            VoyageNumber voyageNumber = Optional.ofNullable(voyageNumberStr)
                    .map(VoyageNumber::of).orElse(null);
            UnLocode location = UnLocode.of(locationStr);
            cargoId = TrackingId.of(cargoIdStr);

            // construct and validate new handling event
            try {
                handlingEvent = validator.validate(HandlingEvent.builder()
                        .eventId(gatewayOps.nextEventId())
                        .voyageNumber(voyageNumber)
                        .location(location)
                        .cargoId(cargoId)
                        .completionTime(completionTime.atZone(Constants.DEFAULT_ZONE_ID))
                        .registrationTime(Instant.now().atZone(Constants.DEFAULT_ZONE_ID))
                        .type(type)
                        .build());
            } catch (InvalidDomainObjectError e) {
                presenter.presentInvalidParametersError(e);
                return;
            }

            // record handling event
            gatewayOps.recordHandlingEvent(handlingEvent);

        } catch (GenericCargoError e) {
            presenter.presentError(e);
            return;
        }

        presenter.presentResultOfRegisteringHandlingEvent(cargoId, handlingEvent);
    }
}
