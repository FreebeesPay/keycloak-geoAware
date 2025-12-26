# GeoAware Login Records REST API

This document describes the REST API endpoints for accessing login geo-localization data.

## Base URL

All endpoints are available under:
```
/realms/{realm}/geoaware/login-records
```

## Authentication

All endpoints require authentication via Bearer token. Admin endpoints require the `view-users` role or equivalent permissions.

## Endpoints

### 1. List Login Records (Admin)

Get all login records with optional filtering and pagination.

**Endpoint:** `GET /realms/{realm}/geoaware/login-records`

**Required Permission:** `view-users`

**Query Parameters:**
- `userId` (optional): Filter by user ID
- `startDate` (optional): Filter by start date (epoch milliseconds)
- `endDate` (optional): Filter by end date (epoch milliseconds)
- `first` (optional, default: 0): Offset for pagination
- `max` (optional, default: 10): Maximum number of results

**Example Request:**
```bash
curl -X GET "https://keycloak.example.com/realms/myrealm/geoaware/login-records?userId=user-123&first=0&max=20" \
  -H "Authorization: Bearer {access_token}"
```

**Example Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "user-123",
    "timestamp": 1702742400000,
    "location": {
      "ipAddress": "203.0.113.42",
      "city": "Paris",
      "postalCode": "75001",
      "country": "France",
      "countryIsoCode": "FR",
      "continent": "Europe",
      "latitude": 48.8566,
      "longitude": 2.3522,
      "accuracyRadius": 5000
    },
    "device": {
      "os": "Windows",
      "osVersion": "10",
      "browser": "Chrome",
      "deviceType": "Desktop",
      "isMobile": false
    }
  }
]
```

---

### 2. Get Current User's Login Records

Get login records for the currently authenticated user.

**Endpoint:** `GET /realms/{realm}/geoaware/login-records/me`

**Required Permission:** Authenticated user (no admin role required)

**Query Parameters:**
- `first` (optional, default: 0): Offset for pagination
- `max` (optional, default: 10): Maximum number of results

**Example Request:**
```bash
curl -X GET "https://keycloak.example.com/realms/myrealm/geoaware/login-records/me?first=0&max=20" \
  -H "Authorization: Bearer {access_token}"
```

**Example Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "current-user-id",
    "timestamp": 1702742400000,
    "location": {
      "ipAddress": "203.0.113.42",
      "city": "Paris",
      "country": "France",
      "countryIsoCode": "FR",
      "latitude": 48.8566,
      "longitude": 2.3522,
      "accuracyRadius": 5000
    },
    "device": {
      "os": "macOS",
      "osVersion": "14.0",
      "browser": "Safari",
      "deviceType": "Desktop",
      "isMobile": false
    }
  }
]
```

---

### 3. Get Login Record by ID

Get a specific login record by its ID.

**Endpoint:** `GET /realms/{realm}/geoaware/login-records/{id}`

**Required Permission:** `view-users`

**Path Parameters:**
- `id` (required): The login record ID

**Example Request:**
```bash
curl -X GET "https://keycloak.example.com/realms/myrealm/geoaware/login-records/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer {access_token}"
```

**Example Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user-123",
  "timestamp": 1702742400000,
  "location": {
    "ipAddress": "203.0.113.42",
    "city": "Paris",
    "postalCode": "75001",
    "country": "France",
    "countryIsoCode": "FR",
    "continent": "Europe",
    "latitude": 48.8566,
    "longitude": 2.3522,
    "accuracyRadius": 5000
  },
  "device": {
    "os": "Windows",
    "osVersion": "10",
    "browser": "Chrome",
    "deviceType": "Desktop",
    "isMobile": false
  }
}
```

**Error Response (404):**
```json
{
  "error": "Login record not found"
}
```

---

### 4. Get Login Records by User ID

Get all login records for a specific user.

**Endpoint:** `GET /realms/{realm}/geoaware/login-records/user/{userId}`

**Required Permission:** `view-users`

**Path Parameters:**
- `userId` (required): The user ID

**Example Request:**
```bash
curl -X GET "https://keycloak.example.com/realms/myrealm/geoaware/login-records/user/user-123" \
  -H "Authorization: Bearer {access_token}"
```

**Example Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "user-123",
    "timestamp": 1702742400000,
    "location": {
      "ipAddress": "203.0.113.42",
      "city": "Paris",
      "country": "France",
      "countryIsoCode": "FR",
      "latitude": 48.8566,
      "longitude": 2.3522,
      "accuracyRadius": 5000
    },
    "device": {
      "os": "Windows",
      "osVersion": "10",
      "browser": "Chrome",
      "deviceType": "Desktop",
      "isMobile": false
    }
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "userId": "user-123",
    "timestamp": 1702656000000,
    "location": {
      "ipAddress": "198.51.100.1",
      "city": "London",
      "country": "United Kingdom",
      "countryIsoCode": "GB",
      "latitude": 51.5074,
      "longitude": -0.1278,
      "accuracyRadius": 3000
    },
    "device": {
      "os": "iOS",
      "osVersion": "17.0",
      "browser": "Safari",
      "deviceType": "Mobile",
      "isMobile": true
    }
  }
]
```

---

## Data Models

### LoginRecordRepresentation

| Field | Type | Description |
|-------|------|-------------|
| `id` | String | Unique identifier for the login record |
| `userId` | String | Keycloak user ID |
| `timestamp` | Long | Login timestamp in epoch milliseconds |
| `location` | GeoLocationRepresentation | Geographical location information |
| `device` | DeviceInfoRepresentation | Device information |

### GeoLocationRepresentation

| Field | Type | Description |
|-------|------|-------------|
| `ipAddress` | String | IP address used for login |
| `city` | String | City name (nullable) |
| `postalCode` | String | Postal/ZIP code (nullable) |
| `country` | String | Country name (nullable) |
| `countryIsoCode` | String | ISO 3166-1 alpha-2 country code (nullable) |
| `continent` | String | Continent name (nullable) |
| `latitude` | Double | Latitude coordinate (nullable) |
| `longitude` | Double | Longitude coordinate (nullable) |
| `accuracyRadius` | Integer | Accuracy radius in meters (nullable) |

### DeviceInfoRepresentation

| Field | Type | Description |
|-------|------|-------------|
| `os` | String | Operating system name (nullable) |
| `osVersion` | String | Operating system version (nullable) |
| `browser` | String | Browser name (nullable) |
| `deviceType` | String | Device type (e.g., Desktop, Mobile, Tablet) (nullable) |
| `isMobile` | Boolean | Whether the device is mobile (nullable) |

---

## Error Responses

### 401 Unauthorized
Returned when the access token is missing or invalid.

### 403 Forbidden
Returned when the user lacks the required permissions.

### 404 Not Found
Returned when the requested resource (login record) does not exist.

---

## Usage Examples

### Example 1: Get Recent Login Records for a User

```bash
# Get login records from the last 7 days for a specific user
START_DATE=$(date -d '7 days ago' +%s)000
curl -X GET "https://keycloak.example.com/realms/myrealm/geoaware/login-records?userId=user-123&startDate=${START_DATE}&max=50" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}"
```

### Example 2: Get Your Own Login History

```bash
# Get your own login records (default: 10 records)
curl -X GET "https://keycloak.example.com/realms/myrealm/geoaware/login-records/me" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}"
```

### Example 3: Paginated Results

```bash
# Get second page of results (20 records per page)
curl -X GET "https://keycloak.example.com/realms/myrealm/geoaware/login-records?first=20&max=20" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}"
```

### Example 4: Filter by Date Range

```bash
# Get login records between two dates
START_DATE=1702656000000  # 2023-12-15
END_DATE=1702742400000    # 2023-12-16
curl -X GET "https://keycloak.example.com/realms/myrealm/geoaware/login-records?startDate=${START_DATE}&endDate=${END_DATE}" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}"
```

---

## Implementation Details

The API is implemented using:
- **JAX-RS** for REST endpoints
- **Keycloak Admin Permissions** for authorization
- **JPA** for data persistence
- **Pagination** for efficient data retrieval
- **DTO pattern** for clean API responses

All endpoints return data from the `geoaware_login_record` database table, which is populated automatically during user login events by the GeoAware event listener.

---

## Notes

1. **Response Format**: All list endpoints return a plain JSON array (following Keycloak's convention). No pagination metadata is included in the response or headers.
2. **Default Pagination**: List endpoints return 10 records by default. Use `max` parameter to adjust (e.g., `max=50`).
3. **Timestamps**: All timestamps are in epoch milliseconds (Java `Instant.toEpochMilli()`).
4. **Nullable Fields**: Many fields in location and device information may be null if the data was not available during login.
5. **Pagination**: For large result sets, use the `first` and `max` parameters to paginate through results. Clients track pagination state using these query parameters.
6. **Performance**: The API uses database indexes on `USER_ID`, `IP_ADDRESS`, `TIMESTAMP`, and geographical coordinates for optimal query performance.
7. **Security**: Admin endpoints enforce the `view-users` permission. The `/me` endpoint allows users to view only their own records.
