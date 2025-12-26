package org.b2code.rest.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.b2code.geoip.persistence.entity.GeoIpInfo;

/**
 * DTO representing geographical location information for REST API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeoLocationRepresentation {

    private String ipAddress;
    private String city;
    private String postalCode;
    private String country;
    private String countryIsoCode;
    private String continent;
    private Double latitude;
    private Double longitude;
    private Integer accuracyRadius;

    public static GeoLocationRepresentation fromEntity(GeoIpInfo geoIpInfo) {
        if (geoIpInfo == null) {
            return null;
        }
        return GeoLocationRepresentation.builder()
                .ipAddress(geoIpInfo.getIp())
                .city(geoIpInfo.getCity())
                .postalCode(geoIpInfo.getPostalCode())
                .country(geoIpInfo.getCountry())
                .countryIsoCode(geoIpInfo.getCountryIsoCode())
                .continent(geoIpInfo.getContinent())
                .latitude(geoIpInfo.getLatitude())
                .longitude(geoIpInfo.getLongitude())
                .accuracyRadius(geoIpInfo.getAccuracyRadius())
                .build();
    }
}
