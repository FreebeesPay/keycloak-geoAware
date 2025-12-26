package org.b2code.rest.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.b2code.geoip.persistence.entity.Device;

/**
 * DTO representing device information for REST API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceInfoRepresentation {

    private String os;
    private String osVersion;
    private String browser;
    private String deviceType;

    @JsonProperty("isMobile")
    private Boolean isMobile;

    public static DeviceInfoRepresentation fromEntity(Device device) {
        if (device == null) {
            return null;
        }
        return DeviceInfoRepresentation.builder()
                .os(device.getOs())
                .osVersion(device.getOsVersion())
                .browser(device.getBrowser())
                .deviceType(device.getDeviceType())
                .isMobile(device.getIsMobile())
                .build();
    }
}
