package com.paymybuddy.paymybuddy.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventsLogger {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationEventsLogger.class);

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        log.info("Successful connection for : {}", event.getAuthentication().getName());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        log.warn("Connection failure for : {}", event.getAuthentication().getName());
    }
}