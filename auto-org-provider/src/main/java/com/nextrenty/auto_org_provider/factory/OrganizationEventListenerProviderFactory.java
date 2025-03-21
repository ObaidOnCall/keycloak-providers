package com.nextrenty.auto_org_provider.factory;

import org.keycloak.Config.Scope;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import com.nextrenty.auto_org_provider.provider.OrganizationEventListenerProvider;

public class OrganizationEventListenerProviderFactory implements EventListenerProviderFactory{

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new OrganizationEventListenerProvider(session);
    }

    @Override
    public void init(Scope config) {
        /****
         * 
         */
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        /***
         * 
         */
    }

    @Override
    public void close() {
        /**
         * 
         */
    }

    @Override
    public String getId() {
       return "organization-event-listener" ;
    }
    
}
