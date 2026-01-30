package com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate;

public enum EstateType {
    APARTMENT,
    BUNGALOW,
    COTTAGE,
    MANSION;

    public String getDisplayName() {
        return switch (this) {
            case APARTMENT -> "Квартира";
            case BUNGALOW -> "Частный дом";
            case COTTAGE -> "Коттедж";
            case MANSION -> "Особняк";
        };
    }
}