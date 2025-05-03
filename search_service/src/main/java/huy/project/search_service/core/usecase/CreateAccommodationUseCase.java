package huy.project.search_service.core.usecase;

import huy.project.search_service.core.domain.constant.ErrorCode;
import huy.project.search_service.core.domain.entity.AccommodationEntity;
import huy.project.search_service.core.domain.entity.UnitAvailabilityEntity;
import huy.project.search_service.core.domain.entity.UnitEntity;
import huy.project.search_service.core.domain.exception.AppException;
import huy.project.search_service.core.port.IAccommodationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateAccommodationUseCase {
    private final IAccommodationPort accommodationPort;

    public AccommodationEntity createAccommodation(AccommodationEntity accommodation) {
        var existedAcc = accommodationPort.getAccById(accommodation.getId());
        if (existedAcc != null) {
            log.error("Accommodation with id {} already exists", accommodation.getId());
            throw new AppException(ErrorCode.ACCOMMODATION_ALREADY_EXISTS);
        }

        // Pre-generate availability for each unit
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date startDate = calendar.getTime();

        calendar.add(Calendar.YEAR, 1);
        Date nextYear = calendar.getTime();

        for (UnitEntity unit : accommodation.getUnits()) {
            unit.setAvailability(new ArrayList<>());
            preGenerateAvailability(unit, startDate, nextYear);
        }

        return accommodationPort.save(accommodation);
    }

    private void preGenerateAvailability(UnitEntity unit, Date startDate, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            Date currentDate = calendar.getTime();

            unit.getAvailability().add(UnitAvailabilityEntity.builder()
                            .date(currentDate)
                            .availableCount(unit.getQuantity())
                    .build());

            calendar.add(Calendar.DATE, 1);
        }
    }
}
