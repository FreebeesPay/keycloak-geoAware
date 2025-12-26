package org.b2code.rest.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.rest.representation.LoginRecordRepresentation;
import org.b2code.rest.service.LoginRecordService;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JAX-RS Resource for managing login record endpoints.
 */
@JBossLog
public class LoginRecordResource {

    private final KeycloakSession session;

    public LoginRecordResource(KeycloakSession session) {
        this.session = session;
    }

    private LoginRecordService getService() {
        return new LoginRecordService(session);
    }

    /**
     * Extract and verify the access token from the Authorization header.
     */
    private AccessToken getAccessToken(HttpHeaders headers) {
        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new WebApplicationException("Missing or invalid Authorization header", Response.Status.UNAUTHORIZED);
        }

        String tokenString = authHeader.substring("Bearer ".length());
        RealmModel realm = session.getContext().getRealm();

        try {
            // Verify and parse the token with public key from realm
            AccessToken token = TokenVerifier.create(tokenString, AccessToken.class)
                    .realmUrl(session.getContext().getUri().getBaseUri() + "realms/" + realm.getName())
                    .publicKey(session.keys().getActiveRsaKey(realm).getPublicKey())
                    .checkActive(true)
                    .checkTokenType(true)
                    .verify()
                    .getToken();

            return token;
        } catch (VerificationException e) {
            log.error("Token verification failed", e);
            throw new WebApplicationException("Invalid token", Response.Status.UNAUTHORIZED);
        }
    }

    /**
     * Get the authenticated user from the token.
     */
    private UserModel getAuthenticatedUser(HttpHeaders headers) {
        AccessToken token = getAccessToken(headers);

        if (token.getSubject() == null) {
            log.error("Token subject is null");
            throw new WebApplicationException("Invalid token - no subject", Response.Status.UNAUTHORIZED);
        }

        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, token.getSubject());

        if (user == null) {
            log.errorf("User not found for subject: %s", token.getSubject());
            throw new WebApplicationException("User not found", Response.Status.UNAUTHORIZED);
        }

        return user;
    }

    /**
     * Check if current user has view-users role (realm or client role).
     * Throws WebApplicationException with 403 if not authorized.
     */
    private void requireViewUsers(HttpHeaders headers) {
        UserModel user = getAuthenticatedUser(headers);
        RealmModel realm = session.getContext().getRealm();

        // Check for realm-management view-users role
        ClientModel realmManagementClient = realm.getClientByClientId("realm-management");
        if (realmManagementClient != null) {
            // Check view-users role
            if (realmManagementClient.getRole("view-users") != null &&
                user.hasRole(realmManagementClient.getRole("view-users"))) {
                return;
            }

            // Check manage-users role (includes view)
            if (realmManagementClient.getRole("manage-users") != null &&
                user.hasRole(realmManagementClient.getRole("manage-users"))) {
                return;
            }
        }

        // Check for realm admin role (fallback)
        if (realm.getRole("admin") != null && user.hasRole(realm.getRole("admin"))) {
            return;
        }

        throw new WebApplicationException("Requires view-users role", Response.Status.FORBIDDEN);
    }

    /**
     * Get all login records with optional filtering and pagination.
     * Requires view-users role.
     *
     * @param userId Optional user ID filter
     * @param startDate Optional start date filter (epoch millis)
     * @param endDate Optional end date filter (epoch millis)
     * @param first Optional offset for pagination (default: 0)
     * @param max Optional maximum number of results (default: 10)
     * @return Response with list of login records
     */
    @GET
    @Path("login-records")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLoginRecords(
            @Context HttpHeaders headers,
            @QueryParam("userId") String userId,
            @QueryParam("startDate") Long startDate,
            @QueryParam("endDate") Long endDate,
            @QueryParam("first") @DefaultValue("0") Integer first,
            @QueryParam("max") @DefaultValue("10") Integer max) {

        requireViewUsers(headers);

        log.debugf("Fetching login records - userId: %s, startDate: %s, endDate: %s, first: %d, max: %d",
                userId, startDate, endDate, first, max);

        LoginRecordService service = getService();
        List<LoginRecordRepresentation> records = service.findLoginRecords(userId, startDate, endDate, first, max);

        return Response.ok(records).build();
    }

    /**
     * Get login records for the current authenticated user.
     * No admin permissions required - users can view their own records.
     *
     * @param first Optional offset for pagination (default: 0)
     * @param max Optional maximum number of results (default: 10)
     * @return Response with list of login records
     */
    @GET
    @Path("login-records/me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyLoginRecords(
            @Context HttpHeaders headers,
            @QueryParam("first") @DefaultValue("0") Integer first,
            @QueryParam("max") @DefaultValue("10") Integer max) {

        UserModel user = getAuthenticatedUser(headers);
        String userId = user.getId();

        log.debugf("Fetching login records for current user: %s", userId);

        LoginRecordService service = getService();
        List<LoginRecordRepresentation> records = service.findLoginRecords(userId, null, null, first, max);

        return Response.ok(records).build();
    }

    /**
     * Get a specific login record by ID.
     * Requires view-users role.
     * NOTE: This must come AFTER /me endpoint to avoid path conflicts.
     *
     * @param id The login record ID
     * @return Response with the login record
     */
    @GET
    @Path("login-records/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLoginRecordById(
            @Context HttpHeaders headers,
            @PathParam("id") String id) {

        // Reject if someone is trying to use special paths
        if ("me".equals(id) || "user".equals(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        requireViewUsers(headers);

        log.debugf("Fetching login record by ID: %s", id);

        LoginRecordService service = getService();
        Optional<LoginRecordRepresentation> record = service.findById(id);

        if (record.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Login record not found"))
                    .build();
        }

        return Response.ok(record.get()).build();
    }

    /**
     * Get login records for a specific user.
     * Requires view-users role.
     *
     * @param userId The user ID
     * @return Response with list of login records
     */
    @GET
    @Path("login-records/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLoginRecordsByUser(
            @Context HttpHeaders headers,
            @PathParam("userId") String userId) {
        requireViewUsers(headers);

        log.debugf("Fetching login records for user: %s", userId);

        LoginRecordService service = getService();
        List<LoginRecordRepresentation> records = service.findByUserId(userId);

        return Response.ok(records).build();
    }
}
