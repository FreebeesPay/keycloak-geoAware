package org.b2code.rest.provider;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.b2code.ServerInfoAwareFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

/**
 * Factory for creating LoginRecordResourceProvider instances.
 * Registers the geoaware REST API endpoints.
 */
@AutoService(RealmResourceProviderFactory.class)
public class LoginRecordResourceProviderFactory extends ServerInfoAwareFactory implements RealmResourceProviderFactory {

    public static final String PROVIDER_ID = "geoaware";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new LoginRecordResourceProvider(session);
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
