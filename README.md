# Boat Tracking Android Application

Android application for the Boat Tracking System, built with Kotlin and Jetpack Compose.

## Requirements

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 26 (Android 8.0) minimum
- Android SDK 35 (Android 16) target
- JDK 17
- Google Maps API Key

## Project 

```
app/src/main/java/com/boattracking/
├── ui/                 # Jetpack Compose UI screens
├── viewmodel/          # ViewModels for MVVM architecture
├── repository/         # Data repositories
├── database/           # Room database entities, DAOs, and converters
├── network/            # Retrofit API service and models
├── service/            # Foreground services (GPS tracking, Bluetooth)
├── bluetooth/          # Bluetooth integration for Arduino sensors
├── sync/               # WorkManager sync jobs
├── security/           # Certificate pinning and secure storage
├── connection/         # Connection manager (local/remote)
└── util/               # Utility classes
```

## Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd captains-log/android
   ```

2. **Configure local.properties**
   ```bash
   cp local.properties.example local.properties
   ```
   
   Edit `local.properties` and add:
   - Your Android SDK path (usually set automatically by Android Studio)
   - Your Google Maps API Key

3. **Get Google Maps API Key**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select existing
   - Enable "Maps SDK for Android"
   - Create credentials (API Key)
   - Add the key to `local.properties`

4. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `android` directory
   - Wait for Gradle sync to complete

5. **Build the project**
   ```bash
   ./gradlew build
   ```

## Architecture

### MVVM Pattern
- **Model**: Room database entities and network models
- **View**: Jetpack Compose UI components
- **ViewModel**: Business logic and state management

### Key Components

#### Database (Room)
- **TripEntity**: Stores trip information
- **GpsPointEntity**: Stores GPS coordinates for trips
- **PhotoEntity**: Stores photo metadata and upload status

#### Network (Retrofit + OkHttp)
- **ApiService**: REST API endpoints
- **ConnectionManager**: Dual connection mode (local/remote)
- **Certificate Pinning**: TLS certificate validation

#### Security
- **SecurePreferences**: Encrypted storage for API keys and URLs
- **CertificatePinner**: SHA-256 certificate pinning for both local and remote connections

#### Services
- **GpsTrackingService**: Foreground service for continuous GPS tracking
- **BluetoothService**: Arduino sensor communication

## Features

### Dual Connection Mode
The app supports two connection endpoints:
- **Local Connection** (optional): Direct connection on local network
- **Remote Connection** (required): Connection via Cloudflare tunnel

Connection priority:
1. Try local connection first (2-second timeout)
2. Automatically fall back to remote if local unavailable

### Security Features
- TLS certificate pinning (separate pins for local/remote)
- Encrypted storage using EncryptedSharedPreferences
- API key authentication
- HTTPS-only communication

### Offline Functionality
- Local storage using Room database
- Automatic sync when online
- WiFi-only photo uploads
- 7-day photo retention after upload

### GPS Tracking
- Foreground service with persistent notification
- Configurable tracking interval (default 5 seconds)
- Stop point detection (5+ minutes in 45-foot radius)
- Offline trip recording

### Nautical Map Providers

The app supports multiple nautical data providers, configurable in Settings. Providers are organized by tier (Free/Paid) and type (Base Map, Overlay, Data).

#### Free Providers

| Provider | Type | Status | Notes |
|----------|------|--------|-------|
| **OpenSeaMap** | Overlay | Working | Nautical marks, buoys, lights. Community-maintained, global coverage. See [openseamap.org/legend](https://openseamap.org/legend) for symbol meanings. |
| **NOAA Charts** | Base Map | Working | Official US nautical charts. US coastal waters only. Slow initial load, tiles cached after first view. Preloads around GPS location on startup. |
| **GEBCO Bathymetry** | Base Map | Working | Global ocean depth visualization via WMS. Very slow initial load (WMS server), tiles cached after first view. Preloads around GPS location on startup. Best at zoom 3-12. |
| **NOAA CO-OPS** | Data | Working | Real-time and predicted tide data from US stations. Shows tide station markers with predictions on map. Independently togglable. |
| **NOAA Weather Alerts** | Data | Working | Active marine weather alerts from the National Weather Service. Shows colored zone polygons (red=warning, orange=watch, yellow=advisory), alert markers, dismissible banner card, and push notifications via WorkManager (15-min polling). |
| **Open-Meteo Marine** | Data | Working | Marine weather forecasts (wave height, swell, wind, temperature). Shows crosshair at map center and info card in bottom-left corner. Global coverage. |
| **Open-Meteo Ocean** | Data | Limited | Ocean current velocity, direction, and sea surface temperature. API accepts parameters but returns null for most locations. Data may appear in certain ocean regions. Card displays when data is available. |
| **AISstream** | Data | Not Tested | Real-time AIS vessel tracking via WebSocket. Requires free API key from aisstream.io. Service reported as currently not vending data. |

#### Paid Providers

| Provider | Type | Status | Notes |
|----------|------|--------|-------|
| **WorldTides** | Data | Untested | Global tide predictions. Paid per request ($10/5,000 predictions). |
| **Stormglass** | Data | Untested | Premium marine weather from multiple sources. Free tier: 10 req/day, paid from $19/month. |
| **Windy** | Overlay | Untested | Animated wind/wave/weather overlays. ~$720/year. |
| **Navionics/Garmin** | Base Map | Untested | Premium nautical charts. Contact for pricing. |
| **MarineTraffic** | Data | Untested | Global vessel tracking with satellite AIS. Credit-based pricing. |

#### Removed Providers

| Provider | Reason |
|----------|--------|
| **USCG NAVCEN / Wrecks & Obstructions** | Original USCG GeoJSON URL was dead (404). Switched to NOAA AWOIS ArcGIS service, but it only contained 359 records concentrated off the Carolina coast with zero West Coast coverage. Data too sparse to be useful. |
| **OpenSeaMap Depth** | Depth sounding tile overlay. Tile server at depth.openseamap.org and t1.openseamap.org/depth both return empty transparent PNGs (334 bytes) for every tile globally. Service is effectively dead. |

## Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Install on Device
```bash
./gradlew installDebug
```

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## Configuration

### First-Time Setup
On first launch, the app will prompt for:
1. **API Key**: Authentication key for backend API
2. **Remote URL**: Required server URL (e.g., https://captainslog.jware.dev)
3. **Remote Certificate Pin**: SHA-256 fingerprint of remote certificate
4. **Local URL**: Optional local server URL (e.g., https://local.captainslog.jware.dev:8585)
5. **Local Certificate Pin**: SHA-256 fingerprint of local certificate

### Getting Certificate Fingerprints
```bash
# For remote certificate
echo | openssl s_client -connect captainslog.jware.dev:443 2>/dev/null | \
  openssl x509 -pubkey -noout | \
  openssl pkey -pubin -outform der | \
  openssl dgst -sha256 -binary | \
  openssl enc -base64

# For local certificate
echo | openssl s_client -connect local.captainslog.jware.dev:8585 2>/dev/null | \
  openssl x509 -pubkey -noout | \
  openssl pkey -pubin -outform der | \
  openssl dgst -sha256 -binary | \
  openssl enc -base64
```

## Permissions

The app requires the following permissions:
- **INTERNET**: API communication
- **ACCESS_FINE_LOCATION**: GPS tracking
- **ACCESS_COARSE_LOCATION**: GPS tracking
- **FOREGROUND_SERVICE**: Continuous GPS tracking
- **FOREGROUND_SERVICE_LOCATION**: Location-based foreground service
- **POST_NOTIFICATIONS**: Trip tracking and maintenance notifications
- **BLUETOOTH**: Arduino sensor communication
- **CAMERA**: Photo attachments
- **READ_EXTERNAL_STORAGE**: Photo access

## Dependencies

### Core
- Kotlin 1.9.20
- Jetpack Compose (Material 3)
- AndroidX Core KTX
- Lifecycle & ViewModel

### Database
- Room 2.6.1

### Networking
- Retrofit 2.9.0
- OkHttp 4.12.0
- Gson 2.10.1

### Security
- AndroidX Security Crypto 1.1.0-alpha06

### Background Work
- WorkManager 2.9.0

### Maps
- Google Maps 18.2.0
- Google Location Services 21.0.1
- Maps Compose 4.3.0

### Image Loading
- Coil 2.5.0

### Testing
- JUnit 4.13.2
- MockK 1.13.8
- Espresso 3.5.1

## Troubleshooting

### Gradle Sync Issues
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Certificate Pinning Errors
- Verify certificate fingerprints are correct
- Ensure URLs match certificate domains
- Check that certificates haven't expired

### GPS Not Working
- Ensure location permissions are granted
- Check that GPS is enabled on device
- Verify foreground service is running

### Sync Issues
- Check internet connectivity
- Verify API key is correct
- Check backend server is running
- Review connection manager logs

## License

[Add your license here]

## Support

For issues and questions, please contact [your contact info]
