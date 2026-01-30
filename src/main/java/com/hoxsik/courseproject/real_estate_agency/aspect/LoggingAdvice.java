package com.hoxsik.courseproject.real_estate_agency.aspect;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAdvice {
    private static final Logger log = LogManager.getLogger(LoggingAdvice.class);

    @Before("execution(* com.hoxsik.courseproject.real_estate_agency.controllers.*(..))")
    public void logEndpointRequest(JoinPoint joinPoint) {
        log.info("Endpoint reached: " + joinPoint.getSignature().toShortString());
    }

    @AfterReturning(pointcut = "execution(* com.hoxsik.courseproject.real_estate_agency.controllers.*(..))", returning = "response")
    public void logEndpointResponse(JoinPoint joinPoint, Object response) {
        log.info("Endpoint response: " + response.toString());
    }

    @AfterThrowing(pointcut = "within(com.hoxsik.courseproject.real_estate_agency.*)", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception in " + joinPoint.getSignature().toShortString() + ": " + exception.getMessage());
    }
}
