package com.spacefleet.spaceshipapi.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class NegativeIdAspect {

    private static final Logger logger = LoggerFactory.getLogger(NegativeIdAspect.class);

    @Before("execution(* com.spacefleet.spaceshipapi.controller.SpaceshipController.getById(..))")
    public void logIfIdIsNegative(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof String id && id.startsWith("-")) {
            logger.warn("NegativeIdAspect, Requested spaceship with ID starting with negative sign: {}", id);
        }
    }

}
