package org.b2code.rest.service;

import lombok.RequiredArgsConstructor;
import org.b2code.geoip.persistence.entity.LoginRecordEntity;
import org.b2code.geoip.persistence.repository.LoginRecordRepository;
import org.b2code.rest.representation.LoginRecordRepresentation;
import org.keycloak.models.KeycloakSession;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for handling login record business logic.
 */
@RequiredArgsConstructor
public class LoginRecordService {

    private final KeycloakSession session;

    private LoginRecordRepository getRepository() {
        return session.getProvider(LoginRecordRepository.class);
    }

    /**
     * Find login records with pagination and filtering.
     *
     * @param userId Optional user ID filter
     * @param startDate Optional start date filter (epoch millis)
     * @param endDate Optional end date filter (epoch millis)
     * @param firstResult Optional offset for pagination
     * @param maxResults Optional maximum number of results
     * @return List of login record representations
     */
    public List<LoginRecordRepresentation> findLoginRecords(
            String userId,
            Long startDate,
            Long endDate,
            Integer firstResult,
            Integer maxResults) {

        Instant startInstant = startDate != null ? Instant.ofEpochMilli(startDate) : null;
        Instant endInstant = endDate != null ? Instant.ofEpochMilli(endDate) : null;

        Collection<LoginRecordEntity> entities = getRepository()
                .findWithFilters(userId, startInstant, endInstant, firstResult, maxResults);

        return entities.stream()
                .map(LoginRecordRepresentation::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Count login records matching the given filters.
     *
     * @param userId Optional user ID filter
     * @param startDate Optional start date filter (epoch millis)
     * @param endDate Optional end date filter (epoch millis)
     * @return Total count of matching records
     */
    public long countLoginRecords(String userId, Long startDate, Long endDate) {
        Instant startInstant = startDate != null ? Instant.ofEpochMilli(startDate) : null;
        Instant endInstant = endDate != null ? Instant.ofEpochMilli(endDate) : null;

        return getRepository().countWithFilters(userId, startInstant, endInstant);
    }

    /**
     * Find a specific login record by ID.
     *
     * @param id The login record ID
     * @return Optional containing the login record representation if found
     */
    public Optional<LoginRecordRepresentation> findById(String id) {
        return getRepository()
                .findById(id)
                .map(LoginRecordRepresentation::fromEntity);
    }

    /**
     * Find all login records for a specific user.
     *
     * @param userId The user ID
     * @return List of login record representations
     */
    public List<LoginRecordRepresentation> findByUserId(String userId) {
        Collection<LoginRecordEntity> entities = getRepository().findAllByUserId(userId);
        return entities.stream()
                .map(LoginRecordRepresentation::fromEntity)
                .collect(Collectors.toList());
    }
}
