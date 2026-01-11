# Abhiyant - Mechanical Inspection Data Collection App

An Android application for collecting mechanical component inspection data directly on mobile devices, with cloud synchronization capabilities. This app replaces manual data entry and eliminates human errors during data transfer.

## Features

- **Digital Data Entry**: Direct entry of inspection measurements using various tools:
  - Vernier Calipers (Length, Width, Height, Diameter)
  - Micrometers (Thickness, Outer Diameter, Inner Diameter)
  - Digital Height Masters
  - Additional flexible measurements
  
- **Local Storage**: Room Database for offline data storage
- **Cloud Synchronization**: Firebase Firestore integration for cloud backup and transfer
- **Search & Filter**: Search by component name, part number, batch, or serial number. Filter by inspection status
- **Status Management**: Track inspection status (Pending, Passed, Failed, Needs Rework)
- **MVVM Architecture**: Clean architecture with separation of concerns
- **Jetpack Compose UI**: Modern, responsive UI built with Jetpack Compose
- **Navigation**: Navigation Component with NavHost for seamless navigation

## Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture with clear separation of concerns:

- **Data Layer**: Room Database (entities, DAOs), Repository, Cloud Storage Service
- **Domain Layer**: Business logic in ViewModels
- **Presentation Layer**: Jetpack Compose UI screens
- **Navigation**: Navigation Component with NavHost

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM
- **Local Database**: Room Database
- **Cloud Storage**: Firebase Firestore
- **Navigation**: Navigation Component
- **Dependency Injection**: Hilt

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 24 (minimum) - 34 (target)
- Kotlin 1.9.0 or later

### Firebase Setup

1. **Create a Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or select an existing one
   - Enable Firestore Database (Create database in test mode for development)

2. **Add Android App to Firebase**:
   - Click "Add app" and select Android
   - Register app with package name: `com.example.abhiyant`
   - Download `google-services.json`
   - Place the file in `app/` directory (same level as `build.gradle.kts`)
   - Note: A placeholder file `app/google-services.json.placeholder` is provided as a reference. Replace it with your actual `google-services.json` file from Firebase Console.

3. **Enable Firestore**:
   - In Firebase Console, go to Firestore Database
   - Create a database (start in test mode for development)
   - Set up security rules as needed for production

### Build and Run

1. Clone or download this repository
2. Open the project in Android Studio
3. Place `google-services.json` in the `app/` directory
4. Sync Gradle files
5. Build and run on an emulator or physical device

### Configuration

The app is configured with:
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Compile SDK: 34

## Project Structure

```
app/src/main/java/com/example/abhiyant/
├── data/
│   ├── cloud/
│   │   └── CloudStorageService.kt      # Firebase Firestore integration
│   ├── database/
│   │   └── InspectionDatabase.kt       # Room Database setup
│   ├── dao/
│   │   └── InspectionDao.kt            # Database queries
│   ├── model/
│   │   └── InspectionEntity.kt         # Data models
│   └── repository/
│       └── InspectionRepository.kt     # Repository pattern implementation
├── navigation/
│   ├── NavGraph.kt                     # Navigation graph
│   └── Navigation.kt                   # Navigation routes
├── ui/
│   ├── screen/
│   │   ├── InspectionListScreen.kt     # List of inspections
│   │   ├── InspectionEntryScreen.kt    # Create/Edit inspection form
│   │   └── InspectionDetailScreen.kt   # View inspection details
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodel/
│       ├── InspectionListViewModel.kt   # List screen ViewModel
│       └── InspectionDetailViewModel.kt # Detail screen ViewModel
├── util/
│   └── AppContainer.kt                 # Dependency container
├── AbhiyantApplication.kt              # Application class
└── MainActivity.kt                     # Main activity
```

## Usage

### Creating an Inspection

1. Tap the "+" button on the home screen
2. Fill in the basic information (Component Name, Inspector Name are required)
3. Enter measurements from your tools:
   - Vernier measurements
   - Micrometer measurements
   - Digital height master measurements
4. Select the inspection status
5. Add any notes or remarks
6. Tap "Save" to store the inspection locally

### Viewing Inspections

- Tap on any inspection card to view details
- Use the search icon to search by component name, part number, batch, or serial number
- Use filter chips to filter by status (All, Pending, Passed, Failed, Needs Rework)

### Cloud Synchronization

1. Tap the cloud upload icon in the top app bar
2. The app will automatically upload all unsynced inspections to Firebase Firestore
3. Once synced, inspections are marked with sync status
4. Data can be accessed from any device or downloaded to main computers

### Editing and Deleting

- Tap on an inspection to view details
- Use the edit icon to modify an inspection
- Use the delete icon to remove an inspection (with confirmation)

## Data Model

### Inspection Entity

Each inspection contains:
- **Basic Info**: Component name, part number, inspector name, batch/serial numbers
- **Vernier Measurements**: Length, width, height, diameter
- **Micrometer Measurements**: Thickness, outer diameter, inner diameter
- **Digital Height Master**: Measurement value
- **Status**: Pending, Passed, Failed, Needs Rework
- **Metadata**: Inspection date, sync status, notes, remarks

## Security Notes

For production deployment:
1. Update Firestore security rules to restrict access
2. Implement user authentication if needed
3. Add data validation on both client and server side
4. Consider encrypting sensitive data
5. Set up proper backup and recovery procedures

## Future Enhancements

- User authentication and multi-user support
- Image capture and attachment
- Export to CSV/Excel formats
- Barcode/QR code scanning for component identification
- Offline sync queue with retry mechanism
- Data visualization and reporting
- Audit trail and history tracking

## License

This project is developed for MSME (Micro, Small, and Medium Enterprises) use.


