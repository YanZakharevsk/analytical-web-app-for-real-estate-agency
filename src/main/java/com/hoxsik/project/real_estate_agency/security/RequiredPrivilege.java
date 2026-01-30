package com.hoxsik.project.real_estate_agency.security;

import com.hoxsik.project.real_estate_agency.jpa.entities.enums.Privilege;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredPrivilege {
    Privilege value();
}
