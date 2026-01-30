package com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate;

public enum Availability {
    FOR_SALE,
    FOR_RENT;

    public String getDisplayName() {
        return switch (this) {
            case FOR_SALE -> "Продажа";
            case FOR_RENT -> "Аренда";
        };
    }
}