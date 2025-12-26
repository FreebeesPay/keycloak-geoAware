package org.b2code.geoip.persistence.repository;

import org.b2code.geoip.persistence.entity.Device;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.persistence.entity.LoginRecordEntity;
import org.keycloak.provider.Provider;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

public interface LoginRecordRepository extends Provider {

    LoginRecordEntity create(LoginRecordEntity loginRecord);

    Optional<LoginRecordEntity> findLatestByUserId(String userId);

    Optional<LoginRecordEntity> findByIpAndTimestampAfter(String ipAddress, Instant timestamp);

    Collection<LoginRecordEntity> findAllByUserId(String userId);

    /**
     * Find login records with pagination and filtering support.
     *
     * @param userId Optional user ID filter
     * @param startDate Optional start date filter (inclusive)
     * @param endDate Optional end date filter (inclusive)
     * @param firstResult Optional offset for pagination (0-based)
     * @param maxResults Optional maximum number of results
     * @return Collection of login records matching the criteria
     */
    Collection<LoginRecordEntity> findWithFilters(String userId, Instant startDate, Instant endDate, Integer firstResult, Integer maxResults);

    /**
     * Count login records matching the given filters.
     *
     * @param userId Optional user ID filter
     * @param startDate Optional start date filter (inclusive)
     * @param endDate Optional end date filter (inclusive)
     * @return Total count of matching records
     */
    long countWithFilters(String userId, Instant startDate, Instant endDate);

    /**
     * Find a specific login record by ID.
     *
     * @param id The login record ID
     * @return Optional containing the login record if found
     */
    Optional<LoginRecordEntity> findById(String id);

    boolean isKnownIp(String userId, String ipAddress);

    void deleteByUserId(String userId);

    void deleteByRealmId(String realmId);

    long cleanupOldRecords(int hoursToKeep);

    boolean hasDeviceBeenUsed(String userId, Device device);

    boolean hasLocationBeenUsed(String userId, GeoIpInfo location);
}
