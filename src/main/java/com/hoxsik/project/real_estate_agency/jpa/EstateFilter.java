package com.hoxsik.project.real_estate_agency.jpa;

import com.hoxsik.project.real_estate_agency.jpa.entities.Estate;
import com.hoxsik.project.real_estate_agency.jpa.entities.Offer;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Availability;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Condition;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.EstateType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EstateFilter {
    /**
     * Фильтр объектов с опубликованным предложением (INNER JOIN offer).
     * Диапазон цены применяется к цене из {@link Offer}, а не к {@code Estate.offeredPrice}.
     */
    public static Specification<Estate> filterEstates(
            String type,
            Integer bathrooms,
            Integer rooms,
            Boolean garage,
            Integer storey,
            String location,
            Boolean balcony,
            String availability,
            Double size,
            String condition,
            Double priceFrom,
            Double priceTo,
            LocalDateTime postFrom,
            LocalDateTime postTo
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Estate, Offer> offerJoin = root.join("offer", JoinType.INNER);

            if (Objects.nonNull(type)) {
                predicates.add(criteriaBuilder.equal(root.get("type"), EstateType.valueOf(type.toUpperCase(Locale.ROOT))));
            }
            if (Objects.nonNull(bathrooms)) {
                predicates.add(criteriaBuilder.equal(root.get("bathrooms"), bathrooms));
            }
            if (Objects.nonNull(rooms)) {
                predicates.add(criteriaBuilder.equal(root.get("rooms"), rooms));
            }
            if (Objects.nonNull(garage)) {
                predicates.add(criteriaBuilder.equal(root.get("garage"), garage));
            }
            if (Objects.nonNull(storey)) {
                predicates.add(criteriaBuilder.equal(root.get("storey"), storey));
            }
            if (Objects.nonNull(location) && !location.isBlank()) {
                String pattern = "%" + location.toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), pattern));
            }
            if (Objects.nonNull(balcony)) {
                predicates.add(criteriaBuilder.equal(root.get("balcony"), balcony));
            }
            if (Objects.nonNull(availability)) {
                predicates.add(criteriaBuilder.equal(root.get("availability"), Availability.valueOf(availability.toUpperCase(Locale.ROOT))));
            }
            if (Objects.nonNull(size)) {
                predicates.add(criteriaBuilder.equal(root.get("size"), size));
            }
            if (Objects.nonNull(condition)) {
                predicates.add(criteriaBuilder.equal(root.get("condition"), Condition.valueOf(condition.toUpperCase(Locale.ROOT))));
            }
            if (Objects.nonNull(priceFrom)) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(offerJoin.get("price"), priceFrom));
            }
            if (Objects.nonNull(priceTo)) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(offerJoin.get("price"), priceTo));
            }
            if (Objects.nonNull(postFrom)) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("postDate"), postFrom));
            }
            if (Objects.nonNull(postTo)) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("postDate"), postTo));
            }

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
