package com.hoxsik.courseproject.real_estate_agency.jpa.entities;

import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.Availability;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.Condition;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.enums.estate.EstateType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "estate", schema = "real_estate")
@Data
public class Estate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @Enumerated(value = EnumType.STRING)
    private EstateType type;

    private int bathrooms;

    private int rooms;

    private boolean garage;

    private int storey;

    private String location;

    private boolean balcony;

    private String description;

    @Enumerated(value = EnumType.STRING)
    private Availability availability;

    private double size;

    @Enumerated(value = EnumType.STRING)
    private Condition condition;

    private double offeredPrice;

    private LocalDateTime postDate;

    @OneToOne(mappedBy = "estate")
    private Offer offer;

    @OneToMany(mappedBy = "estate")
    private Set<Photo> photos;

    @OneToMany(mappedBy = "estate")
    private Set<Document> documents;

    @OneToOne(mappedBy = "estate")
    private ArchivedOffer archivedOffer;

    @Column(name = "is_submitted")
    private Boolean isSubmitted;



    @PrePersist
    private void prePersist() {
        postDate = LocalDateTime.now();
        isSubmitted = false;
    }
}
