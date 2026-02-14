# Trip Crew Sharing - Technical Specification

## Overview
Offline P2P trip crew sharing via QR codes. Captain generates QR for active trip, crew scans to join. Full crew experience with crew list visibility. No server required.

## Key Decisions
- **Join method**: QR code (consistent with existing boat sharing)
- **Sea time credit**: Full trip duration (crew gets credit for entire trip)
- **Connectivity**: Fully offline P2P via QR data exchange
- **Scope**: Full crew experience (crew list visible to captain + crew)
- **Crew list sync**: Captain-refresh QR pattern (captain regenerates QR after each join)
- **Trip completion**: Crew manually ends OR re-scans captain's QR after trip ends

## Data Model Changes

### New Entity: CrewMemberEntity
```kotlin
@Entity(tableName = "crew_members", primaryKeys = ["tripId", "deviceId"])
data class CrewMemberEntity(
    val tripId: String,
    val deviceId: String,
    val displayName: String,
    val joinedAt: Date,
    val role: String = "crew"
)
```

### TripEntity additions
- `captainTripId: String?` — on crew devices, links to captain's original trip ID
- `captainName: String?` — display name of the captain

### SecurePreferences addition
- `displayName: String?` — user's display name for crew sharing

### Room Migration 10 → 11
1. CREATE TABLE crew_members
2. ALTER TABLE trips ADD COLUMN captainTripId
3. ALTER TABLE trips ADD COLUMN captainName

## QR Payload Format
```json
{
  "v": 1,
  "type": "crew_join",
  "origin": "device:<uuid>",
  "ts": <unix_ms>,
  "data": {
    "tripId": "<uuid>",
    "boatId": "<uuid>",
    "boatName": "Sea Breeze",
    "captainName": "Justin",
    "startTime": <unix_ms>,
    "endTime": <unix_ms or null>,
    "waterType": "coastal",
    "crew": [{"deviceId": "<uuid>", "name": "Alex", "joinedAt": <unix_ms>}]
  }
}
```

## Join Flow
1. Captain starts trip → taps "Share with Crew" on TripDetailScreen
2. ShareTripCrewScreen generates QR from active trip + crew list
3. Crew scans QR → creates local BoatEntity (if needed) + TripEntity (role=crew) + CrewMemberEntity (self)
4. Captain adds crew member to local CrewMemberEntity table
5. Captain's QR auto-regenerates with updated crew list

## New Files
- database/entities/CrewMemberEntity.kt
- database/dao/CrewMemberDao.kt
- database/migrations/Migration_10_11.kt
- sharing/models/TripCrewShareData.kt
- sharing/TripCrewShareGenerator.kt
- sharing/TripCrewImporter.kt
- ui/sharing/ShareTripCrewScreen.kt
- ui/components/CameraPreview.kt (extracted from ScanBoatScreen)

## Modified Files
- database/entities/TripEntity.kt (add captainTripId, captainName)
- database/AppDatabase.kt (version 11, add CrewMemberEntity, migration)
- database/dao/TripDao.kt (add getTripByCaptainTripId)
- security/SecurePreferences.kt (add displayName)
- ui/sharing/ScanBoatScreen.kt (extract CameraPreview, add type routing for crew_join)
- ui/trips/TripDetailScreen.kt (add crew list card, share button)
- ui/MainNavigation.kt (add routes)
