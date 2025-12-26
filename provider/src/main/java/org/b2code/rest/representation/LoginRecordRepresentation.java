package org.b2code.rest.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.b2code.geoip.persistence.entity.LoginRecordEntity;

/**
 * DTO representing a login record for REST API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRecordRepresentation {

    private String id;
    private String userId;
    private Long timestamp;
    private GeoLocationRepresentation location;
    private DeviceInfoRepresentation device;

    public static LoginRecordRepresentation fromEntity(LoginRecordEntity entity) {
        if (entity == null) {
            return null;
        }
        return LoginRecordRepresentation.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .timestamp(entity.getTime() != null ? entity.getTime().toEpochMilli() : null)
                .location(GeoLocationRepresentation.fromEntity(entity.getGeoIpInfo()))
                .device(DeviceInfoRepresentation.fromEntity(entity.getDevice()))
                .build();
    }
}
