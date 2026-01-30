package com.hoxsik.project.real_estate_agency.services;

import com.hoxsik.project.real_estate_agency.dto.request.EstimateRequestDto;
import com.hoxsik.project.real_estate_agency.dto.response.EstimateResponseDto;
import com.hoxsik.project.real_estate_agency.jpa.entities.ArchivedOffer;
import com.hoxsik.project.real_estate_agency.jpa.entities.Estate;
import com.hoxsik.project.real_estate_agency.jpa.entities.Offer;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.EstateType;
import com.hoxsik.project.real_estate_agency.jpa.repositories.ArchivedOfferRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EstimateService {

    private final OfferRepository offerRepository;
    private final ArchivedOfferRepository archivedOfferRepository;

    public EstimateResponseDto estimate(EstimateRequestDto dto) {

        double minSize = dto.getArea() - 15;
        double maxSize = dto.getArea() + 15;

        List<Offer> offers = offerRepository.findAnalogOffers(
                dto.getEstateType(),
                dto.getAvailability(),
                dto.getCondition(),
                dto.getRooms(),
                minSize,
                maxSize
        );

        List<ArchivedOffer> archived = archivedOfferRepository.findAnalogArchivedOffers(
                dto.getEstateType(),
                dto.getAvailability(),
                dto.getCondition(),
                dto.getRooms(),
                minSize,
                maxSize
        );

        // фильтрация по этажам
        List<Double> prices = new ArrayList<>();

        offers.stream()
                .filter(o -> floorMatches(o.getEstate(), dto))
                .forEach(o -> prices.add(o.getPrice()));

        archived.stream()
                .filter(a -> floorMatches(a.getEstate(), dto))
                .forEach(a -> prices.add(a.getPrice()));

        if (prices.isEmpty()) {
            return new EstimateResponseDto(
                    false,
                    null,
                    null,
                    "К сожалению, подходящие аналоги на рынке не найдены"
            );
        }

        double estimatedPrice =
                prices.size() == 1
                        ? prices.get(0)
                        : prices.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        long totalOffers = offerRepository.count();
        long totalArchived = archivedOfferRepository.count();

        double percent =
                prices.size() * 100.0 / (totalOffers + totalArchived);

        String message =
                percent < 10
                        ? "Аналогов на рынке немного — стоимость стоит формировать аккуратно"
                        : "Рынок насыщен — можно ориентироваться на рассчитанную цену";

        return new EstimateResponseDto(
                true,
                estimatedPrice,
                percent,
                message
        );
    }

    private boolean floorMatches(Estate estate, EstimateRequestDto dto) {

        if (dto.getEstateType() == EstateType.APARTMENT) {
            int min = dto.getFloor() - 3;
            int max = dto.getFloor() + 3;
            return estate.getStorey() >= min && estate.getStorey() <= max;
        }

        return Objects.equals(estate.getStorey(), dto.getTotalFloors());
    }
}

