package com.hoxsik.project.real_estate_agency.jpa.entities.enums.estate;

public enum Condition {
    NEEDS_RENOVATION,
    DEVELOPER_CONDITION,
    AFTER_RENOVATION,
    NORMAL_USE_SIGNS;

    public String getDisplayName() {
        return switch (this) {
            case NEEDS_RENOVATION -> "Требует ремонта";
            case DEVELOPER_CONDITION -> "Состояние от застройщика";
            case AFTER_RENOVATION -> "После ремонта";
            case NORMAL_USE_SIGNS -> "В хорошем состоянии";
        };
    }
}