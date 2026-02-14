# Implementation Plan: Trip Crew Sharing

## Phase 1: Data Layer (sequential - must complete before UI)

### Task 1.1: CrewMemberEntity + DAO
- New file: `database/entities/CrewMemberEntity.kt`
- New file: `database/dao/CrewMemberDao.kt`

### Task 1.2: TripEntity additions + TripDao query
- Modify: `database/entities/TripEntity.kt` — add `captainTripId: String?`, `captainName: String?`
- Modify: `database/dao/TripDao.kt` — add `getTripByCaptainTripId(captainTripId: String)` query

### Task 1.3: Room Migration 10→11 + AppDatabase update
- New file: `database/migrations/Migration_10_11.kt`
- Modify: `database/AppDatabase.kt` — version 11, add CrewMemberEntity, add migration, add crewMemberDao()

### Task 1.4: SecurePreferences — add displayName
- Modify: `security/SecurePreferences.kt`

## Phase 2: Sharing Infrastructure (parallel after Phase 1)

### Task 2.1: TripCrewShareData model
- New file: `sharing/models/TripCrewShareData.kt`

### Task 2.2: TripCrewShareGenerator (QR generation + parsing)
- New file: `sharing/TripCrewShareGenerator.kt`

### Task 2.3: TripCrewImporter (join logic)
- New file: `sharing/TripCrewImporter.kt`

## Phase 3: UI (parallel after Phase 2)

### Task 3.1: Extract CameraPreview to shared composable
- New file: `ui/components/CameraPreview.kt`
- Modify: `ui/sharing/ScanBoatScreen.kt` — use shared CameraPreview

### Task 3.2: ShareTripCrewScreen (captain shows QR)
- New file: `ui/sharing/ShareTripCrewScreen.kt`

### Task 3.3: ScanTripCrewScreen (crew scans QR to join)
- New file: `ui/sharing/ScanTripCrewScreen.kt`

### Task 3.4: Crew list card on TripDetailScreen + "Share with Crew" button
- Modify: `ui/trips/TripDetailScreen.kt`

### Task 3.5: Navigation — add crew sharing routes to TripNavigation
- Modify: `ui/trips/TripNavigation.kt`

## Phase 4: Build + Test
- Run `./gradlew assembleDebug`
- Fix any compilation errors
