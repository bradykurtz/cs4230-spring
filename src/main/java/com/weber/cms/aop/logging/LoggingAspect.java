package com.weber.cms.aop.logging;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    Logger logger = LoggerFactory.getLogger(LoggingAspect.class.getName());
    private ObjectMapper objectMapper;

    @Autowired
    public LoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(com.weber.cms.aop.logging.Log)")
    public void logAround(ProceedingJoinPoint joinPoint) {
        // Anything you want to do before the method
        Signature signature = joinPoint.getSignature();

        logger.info("Entered " + signature.getName());
        try {
            joinPoint.proceed(); // This will call the method that was intended
        } catch (Throwable throwable) {
            logger.info("Exception Thrown In " + signature.getName(), throwable);
        }
        logger.info("Leaving " + signature.getName());
        //Anything you want to do after the method
    }

    @Before("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void logRestRequest(JoinPoint joinPoint) {
        Map<Parameter, Object> paramValue = getParameterInformation(joinPoint);
        StringBuilder logValue = new StringBuilder();
        for (Map.Entry<Parameter, Object> entry: paramValue.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                try {
                    logValue.append(entry.getKey().getName());
                    logValue.append("=");
                    logValue.append(objectMapper.writeValueAsString(entry.getValue()));
                } catch (JsonProcessingException e) {
                    logger.debug("Could not record all values of the method", e);
                }
            }
        }
        if(logValue.length() > 0) {
            logger.info(logValue.toString());
        }
    }


    private Map<Parameter, Object> getParameterInformation(JoinPoint joinPoint) {
        MethodSignature methodSignature = ((MethodSignature) joinPoint.getSignature());
        Method method =  methodSignature.getMethod();
        Map<Parameter, Object> response = new HashMap<>();
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            if (joinPoint.getArgs().length >= i) {
                response.put(parameter, joinPoint.getArgs()[i]);
            } else {
                response.put(parameter, null);
            }
        }
        return response;
    }
}
