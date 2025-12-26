package org.b2code.rest.provider;

import lombok.RequiredArgsConstructor;
import org.b2code.rest.resource.LoginRecordResource;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

/**
 * Realm resource provider for exposing login record endpoints.
 * This makes the endpoints available at /realms/{realm}/geoaware/*
 */
@RequiredArgsConstructor
public class LoginRecordResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    @Override
    public Object getResource() {
        return new LoginRecordResource(session);
    }

    @Override
    public void close() {
        // NOOP
    }
}
