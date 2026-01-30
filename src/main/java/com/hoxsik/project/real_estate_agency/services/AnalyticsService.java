package com.hoxsik.project.real_estate_agency.services;

import com.hoxsik.project.real_estate_agency.dto.request.PriceDynamicsRequest;
import com.hoxsik.courseproject.real_estate_agency.dto.response.*;

import com.hoxsik.project.real_estate_agency.dto.response.*;
import com.hoxsik.project.real_estate_agency.jpa.entities.ArchivedOffer;
import com.hoxsik.project.real_estate_agency.jpa.entities.Estate;
import com.hoxsik.project.real_estate_agency.jpa.entities.Offer;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Availability;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.Condition;
import com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate.EstateType;
import com.hoxsik.project.real_estate_agency.jpa.repositories.AgentRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.ArchivedOfferRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.EstateRepository;
import com.hoxsik.project.real_estate_agency.jpa.repositories.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OfferRepository offerRepository;
    private final ArchivedOfferRepository archivedOfferRepository;
    private final AgentRepository agentRepository;

    private final EstateRepository estateRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public List<PriceDynamicsResponse> getPriceDynamics(PriceDynamicsRequest request) {

        List<Offer> offers = offerRepository.findAllForSale();
        List<ArchivedOffer> archivedOffers = archivedOfferRepository.findAllForSale();

        Map<String, List<Double>> priceByMonth = new HashMap<>();
        Map<String, Long> countByMonth = new HashMap<>();

        for (Offer o : offers) {
            if (!matchesSegment(o.getEstate().getRooms(), request))
                continue;

            String month = o.getPostDate().format(FORMATTER);
            double price = calculatePrice(o.getPrice(), o.getEstate().getSize(), request.getPriceIndex());

            priceByMonth.computeIfAbsent(month, k -> new ArrayList<>()).add(price);
            countByMonth.merge(month, 1L, Long::sum);
        }

        for (ArchivedOffer ao : archivedOffers) {
            if (!matchesSegment(ao.getEstate().getRooms(), request))
                continue;

            String month = ao.getArchiveDate().format(FORMATTER);
            double price = calculatePrice(ao.getPrice(), ao.getEstate().getSize(), request.getPriceIndex());

            priceByMonth.computeIfAbsent(month, k -> new ArrayList<>()).add(price);
            countByMonth.merge(month, 1L, Long::sum);
        }

        return priceByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    String date = entry.getKey();
                    List<Double> prices = entry.getValue();

                    double avgPrice = prices.stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0);

                    return new PriceDynamicsResponse(
                            date,
                            Math.round(avgPrice),
                            countByMonth.getOrDefault(date, 0L)
                    );
                })
                .collect(Collectors.toList());
    }

    private boolean matchesSegment(int rooms, PriceDynamicsRequest request) {
        if ("ROOMS".equals(request.getSegment())) {
            return request.getRooms() != null && rooms == request.getRooms();
        }
        return true;
    }

    private double calculatePrice(double price, double size, String index) {
        if ("PER_M2".equals(index)) {
            return price / size;
        }
        return price;
    }

    public PriceIndexResponse getApartmentPriceIndex() {

        List<Estate> estates = estateRepository.findApartmentsForSale(
                EstateType.APARTMENT,
                Availability.FOR_SALE
        );

        Map<YearMonth, List<Estate>> groupedByMonth =
                estates.stream()
                        .collect(Collectors.groupingBy(
                                e -> YearMonth.from(e.getPostDate())
                        ));

        List<PriceIndexPointResponse> points =
                groupedByMonth.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> {
                            YearMonth month = entry.getKey();
                            List<Estate> monthlyEstates = entry.getValue();

                            Double secondary = avgPricePerMeter(monthlyEstates, false);
                            Double newbuild = avgPricePerMeter(monthlyEstates, true);

                            YearMonth ym = entry.getKey();

                            return new PriceIndexPointResponse(
                                    ym.toString(), // "2025-03"
                                    secondary,
                                    newbuild
                            );
                        })
                        .toList();

        PriceIndexSummaryResponse summary = calculateSummary(points);

        return new PriceIndexResponse(points, summary);
    }

    private Double avgPricePerMeter(List<Estate> estates, boolean newbuild) {
        return estates.stream()
                .filter(e -> newbuild
                        ? e.getCondition() == Condition.DEVELOPER_CONDITION
                        : e.getCondition() != Condition.DEVELOPER_CONDITION)
                .mapToDouble(e -> e.getOfferedPrice() / e.getSize())
                .average()
                .orElse(0);
    }

    private PriceIndexSummaryResponse calculateSummary(List<PriceIndexPointResponse> points) {
        if (points.isEmpty()) {
            return new PriceIndexSummaryResponse(0, 0, 0);
        }

        double first = averageIndex(points.get(0));
        double last = averageIndex(points.get(points.size() - 1));

        double totalChange = percentChange(first, last);

        double monthChange = 0;
        if (points.size() > 1) {
            double prev = averageIndex(points.get(points.size() - 2));
            monthChange = percentChange(prev, last);
        }

        return new PriceIndexSummaryResponse(last, monthChange, totalChange);
    }

    private double averageIndex(PriceIndexPointResponse p) {
        if (p.getSecondaryIndex() > 0 && p.getNewbuildIndex() > 0) {
            return (p.getSecondaryIndex() + p.getNewbuildIndex()) / 2;
        }
        return Math.max(p.getSecondaryIndex(), p.getNewbuildIndex());
    }

    private double percentChange(double from, double to) {
        if (from == 0) return 0;
        return ((to - from) / from) * 100;
    }

    public RentPriceResponse getRentPriceIndex() {

        List<Estate> estates = estateRepository.findAllForRent();

        Map<YearMonth, List<Estate>> grouped =
                estates.stream()
                        .collect(Collectors.groupingBy(
                                e -> YearMonth.from(e.getPostDate())
                        ));

        List<RentPricePointResponse> points =
                grouped.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> {
                            YearMonth ym = entry.getKey();
                            List<Estate> list = entry.getValue();

                            return new RentPricePointResponse(
                                    ym.toString(),
                                    avgRent(list, 1),
                                    avgRent(list, 2),
                                    avgRent(list, 3)
                            );
                        })
                        .toList();

        RentPriceSummaryResponse summary = buildSummary(points);

        return new RentPriceResponse(points, summary);
    }

    private double avgRent(List<Estate> estates, int rooms) {
        return estates.stream()
                .filter(e -> e.getRooms() == rooms)
                .mapToDouble(Estate::getOfferedPrice)
                .average()
                .orElse(0);
    }

    private RentPriceSummaryItem buildItem(
            List<RentPricePointResponse> points,
            ToDoubleFunction<RentPricePointResponse> extractor
    ) {
        if (points.size() < 2) {
            return new RentPriceSummaryItem(0, 0);
        }

        double first = extractor.applyAsDouble(points.get(0));
        double last = extractor.applyAsDouble(points.get(points.size() - 1));
        double prev = extractor.applyAsDouble(points.get(points.size() - 2));

        double total = percentChange(first, last);
        double month = percentChange(prev, last);

        return new RentPriceSummaryItem(month, total);
    }

    private RentPriceSummaryResponse buildSummary(List<RentPricePointResponse> points) {

        return new RentPriceSummaryResponse(
                buildItem(points, RentPricePointResponse::getOneRoom),
                buildItem(points, RentPricePointResponse::getTwoRooms),
                buildItem(points, RentPricePointResponse::getThreeRooms)
        );
    }

    public AgentStatsResponse getAgentStats() {
        List<Offer> offers = offerRepository.findAll();
        List<ArchivedOffer> archived = archivedOfferRepository.findAll();

        List<AgentPercentDto> offersPercent =
                buildPercentByAgent(
                        offers.stream()
                                .map(o -> {
                                    if (o.getEstate() != null &&
                                            o.getEstate().getAgent() != null &&
                                            o.getEstate().getAgent().getUser() != null) {
                                        return o.getEstate().getAgent().getUser().getFullName();
                                    }
                                    return "Без агента";
                                })
                                .toList()
                );

        List<AgentPercentDto> archivedPercent =
                buildPercentByAgent(
                        archived.stream()
                                .map(a -> {
                                    if (a.getEstate() != null &&
                                            a.getEstate().getAgent() != null &&
                                            a.getEstate().getAgent().getUser() != null) {
                                        return a.getEstate().getAgent().getUser().getFullName();
                                    }
                                    return "Без агента";
                                })
                                .toList()
                );

        return new AgentStatsResponse(offersPercent, archivedPercent);
    }

    private List<AgentPercentDto> buildPercentByAgent(List<String> agents) {
        if (agents.isEmpty()) return List.of();

        int total = agents.size();

        return agents.stream()
                .collect(Collectors.groupingBy(a -> a, Collectors.counting()))
                .entrySet()
                .stream()
                .map(e ->
                        new AgentPercentDto(
                                e.getKey(),
                                e.getValue() * 100.0 / total
                        )
                )
                .toList();
    }





}