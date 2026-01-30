package com.hoxsik.courseproject.real_estate_agency.services;

import com.hoxsik.courseproject.real_estate_agency.dto.Mapper;
import com.hoxsik.courseproject.real_estate_agency.dto.request.OfferRequest;
import com.hoxsik.courseproject.real_estate_agency.dto.response.OfferResponse;
import com.hoxsik.courseproject.real_estate_agency.dto.response.Response;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Customer;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Estate;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Offer;
import com.hoxsik.courseproject.real_estate_agency.jpa.repositories.OfferRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;
    private final EstateService estateService;
    private final CustomerService customerService;
    private final ArchivedOfferService archivedOfferService;
    private final OfferVisitService offerVisitService;

    @PersistenceContext // ← Важно! Эта аннотация для EntityManager
    private EntityManager entityManager;

    @Transactional
    public Response postOffer(Long id, OfferRequest offerRequest){
        Optional<Estate> optionalEstate = estateService.getByID(id);

        if (optionalEstate.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "Не найдено недвижимости по запрашиваемому ID");

        Estate estate = optionalEstate.get();

        offerRepository.save(createOffer(estate, offerRequest));
        estate.setIsSubmitted(true);

        return new Response(true, HttpStatus.CREATED, "Successfully posted the offer");
    }

    public Optional<List<Offer>> getFilteredOffers(Integer bathrooms, Integer rooms, Boolean garage, Integer storey,
                                                   String location, Boolean balcony, Double size, String condition,
                                                   String type, String availability, Double priceFrom, Double priceTo,
                                                   LocalDateTime postFrom, LocalDateTime postTo) {

        Optional<List<Estate>> optionalEstates = estateService.getFilteredEstates(bathrooms, rooms, garage, storey, location,
                balcony, size, condition, type, availability,
                priceFrom, priceTo, postFrom, postTo);

        return optionalEstates.map(estates -> estates.stream().map(offerRepository::findByEstate).toList());
    }

    @Transactional
    public Response blockOffer(String username, Long id) {
        Optional<Offer> optionalOffer = offerRepository.findById(id);

        if (optionalOffer.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "Предложения по указанному идентификатору не найдено");

        Optional<Customer> customer = customerService.getByUsername(username);

        if (customer.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "Клиент с указанным именем пользователя не найден.");

        Offer offer = optionalOffer.get();

        offer.setBlocked(true);
        offer.setBlockedBy(customer.get());

        return new Response(true, HttpStatus.OK, "Предложение успешно заблокировано");
    }

    @Transactional
    public Response unblockOffer(String username, Long id) {
        Optional<Offer> optionalOffer = offerRepository.findById(id);

        if (optionalOffer.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "Предложения по указанному идентификатору не найдено");

        Offer offer = optionalOffer.get();

        if (!offer.getEstate().getAgent().getUser().getUsername().equals(username)  && !offer.getBlockedBy().getUser().getUsername().equals(username)) {
            return new Response(false, HttpStatus.BAD_REQUEST, "У вас нет прав разблокировать это предложение");
        }

        offer.setBlocked(false);
        offer.setBlockedBy(null);

        offerRepository.save(offer);

        return new Response(true, HttpStatus.OK, "Предложение успешно разблокировано");
    }

    public Optional<List<Offer>> getBlockedOffersByAgentUsername(String username) {
        return offerRepository.findBlockedOffersByAgentUsername(username);
    }

    @Transactional
    public Response finalizeOffer(String username, Long id) {
        Optional<Offer> optionalOffer = offerRepository.findById(id);
        Response response = validateFinalizeRequest(optionalOffer, username);

        if (!response.isSuccess())
            return response;

        Offer offer = optionalOffer.get();

        archivedOfferService.archiveOffer(offer);
        offerRepository.delete(offer);

        return new Response(true, HttpStatus.OK, "Successfully finalized the offer");
    }

    public Optional<List<Offer>> getBlockedOffersByCustomerUsername(String username) {
        return offerRepository.findBlockedOffersByCustomerUsername(username);
    }

    public Response addOfferToFavorites(String username, Long id) {
        Optional<Customer> customer = customerService.getByUsername(username);

        if (customer.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "Клиент с указанным именем пользователя не найден");

        Optional<Offer> offer = offerRepository.findById(id);

        if (offer.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "Предложения по указанному идентификатору не найдено");

        offer.get().getCustomers().add(customer.get());
        offerRepository.save(offer.get());

        return new Response(true,  HttpStatus.CREATED, "Предложение успешно добавлено в избранное");
    }

    @Transactional(readOnly = true)
    public Optional<List<Offer>> getFavoriteOffers(String username) {
        try {
            List<Offer> offers = entityManager.createQuery(
                            "SELECT DISTINCT o FROM Offer o " +
                                    "LEFT JOIN FETCH o.estate e " +
                                    "LEFT JOIN FETCH e.agent a " +
                                    "LEFT JOIN FETCH a.user " +
                                    "JOIN o.customers c " +
                                    "JOIN c.user u " +
                                    "WHERE u.username = :username", Offer.class)
                    .setParameter("username", username)
                    .getResultList();

            // Если offers пустой, все равно возвращаем Optional с пустым списком
            return Optional.of(offers);

        } catch (Exception e) {
            // Логируй ошибку
            return Optional.empty();
        }
    }

    public Response removeOfferFromFavorites(String username, Long id) {
        Optional<Customer> customer = customerService.getByUsername(username);

        if (customer.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "Клиент с указанным именем пользователя не найден");

        Optional<Offer> offer = offerRepository.findById(id);

        if (offer.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "Предложения по указанному имени пользователя не найдены");

        offer.get().getCustomers().remove(customer.get());
        offerRepository.save(offer.get());

        return new Response(true, HttpStatus.OK, "Предложение успешно удалено из избранного");
    }

    public Optional<Offer> checkOffer(String username, Long id) {
        Optional<Offer> offer = offerRepository.findById(id);
        Optional<Customer> customer = customerService.getByUsername(username);

        if (offer.isEmpty() || customer.isEmpty())
            return Optional.empty();

        offerVisitService.addOfferVisit(customer.get(), offer.get());

        return offer;
    }

    private Response validateFinalizeRequest(Optional<Offer> offer, String username) {
        if (offer.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "Предложения по указанному идентификатору не найдено");

        if (!offer.get().isBlocked())
            return new Response(false, HttpStatus.BAD_REQUEST, "Предложение не было заблокировано ни одним клиентом");

        if (!offer.get().getEstate().getAgent().getUser().getUsername().equals(username))
            return new Response(false, HttpStatus.BAD_REQUEST, "Предложение не назначено агенту с указанным именем пользователя");

        return new Response(true, HttpStatus.OK, "Запрос на завершение действителен");
    }

    private Offer createOffer(Estate estate, OfferRequest offerRequest) {
        Offer offer = new Offer();

        offer.setDescription(offerRequest.getDescription());
        offer.setPrice(offerRequest.getPrice());
        offer.setEstate(estate);

        return offer;
    }
}
