# ProjectSwipe Architecture

## 🏗️ Overview

ProjectSwipe follows the **MVVM (Model-View-ViewModel)** architecture pattern with **Repository Pattern** for data management. The app is built using modern Android development practices with Kotlin, ensuring maintainability, testability, and scalability.

## 📱 App Architecture Layers

### 1. Presentation Layer (UI)
- **Activities**: Main entry points and navigation containers
- **Fragments**: Individual screens (Side Projects, Add Project, Profile)
- **ViewModels**: Business logic and state management
- **Adapters**: RecyclerView adapters for lists and cards

### 2. Domain Layer (Business Logic)
- **Use Cases**: Specific business operations
- **Models**: Data classes representing business entities
- **Repositories**: Abstract data access interfaces

### 3. Data Layer
- **Repository Implementations**: Concrete data access implementations
- **Data Sources**: Firebase Firestore, Authentication
- **DTOs**: Data Transfer Objects for network/database operations

## 🗂️ Project Structure

```
app/src/main/java/com/yourpackage/projectswipe/
├── ui/
│   ├── activities/
│   │   ├── MainActivity.kt          # Main container with bottom navigation
│   │   ├── AuthActivity.kt          # Login/Register/Forgot Password
│   │   └── OnboardingActivity.kt    # Skills/interests setup
│   ├── fragments/
│   │   ├── ProjectsFragment.kt      # Swipeable project cards
│   │   ├── AddProjectFragment.kt    # Create new projects
│   │   ├── ProfileFragment.kt       # User profile and settings
│   │   └── EditProfileFragment.kt   # Edit user information
│   ├── adapters/
│   │   ├── ProjectCardAdapter.kt    # Swipe cards adapter
│   │   └── UserProjectsAdapter.kt   # User's own projects list
│   └── viewmodels/
│       ├── AuthViewModel.kt         # Authentication logic
│       ├── ProjectsViewModel.kt     # Project discovery logic
│       ├── AddProjectViewModel.kt   # Project creation logic
│       └── ProfileViewModel.kt      # Profile management logic
├── domain/
│   ├── models/
│   │   ├── User.kt                  # User data model
│   │   ├── Project.kt               # Project data model
│   │   └── UserProfile.kt           # Extended user profile
│   ├── repositories/
│   │   ├── AuthRepository.kt        # Authentication interface
│   │   ├── ProjectRepository.kt     # Project operations interface
│   │   └── UserRepository.kt        # User operations interface
│   └── usecases/
│       ├── LoginUseCase.kt          # Handle user login
│       ├── CreateProjectUseCase.kt  # Create new project
│       └── UpdateProfileUseCase.kt  # Update user profile
├── data/
│   ├── repositories/
│   │   ├── AuthRepositoryImpl.kt    # Firebase Auth implementation
│   │   ├── ProjectRepositoryImpl.kt # Firebase Firestore projects
│   │   └── UserRepositoryImpl.kt    # Firebase Firestore users
│   ├── datasources/
│   │   ├── FirebaseAuthDataSource.kt
│   │   └── FirestoreDataSource.kt
│   └── dtos/
│       ├── UserDto.kt               # User data transfer object
│       └── ProjectDto.kt            # Project data transfer object
├── utils/
│   ├── Constants.kt                 # App constants
│   ├── Extensions.kt                # Kotlin extensions
│   ├── PreferenceManager.kt         # SharedPreferences wrapper
│   └── NetworkUtils.kt              # Network connectivity checks
└── di/
    └── AppModule.kt                 # Dependency injection (if using Hilt)
```

## 🔄 Data Flow

### Project Discovery Flow
```
User Swipes → ProjectsFragment → ProjectsViewModel → ProjectRepository 
→ FirestoreDataSource → Firebase Firestore → Response → ViewModel → UI Update
```

### Authentication Flow
```
User Login → AuthActivity → AuthViewModel → AuthRepository 
→ FirebaseAuthDataSource → Firebase Auth → Success → MainActivity
```

### Project Creation Flow
```
User Input → AddProjectFragment → AddProjectViewModel → ProjectRepository 
→ FirestoreDataSource → Firestore → Success → Navigate Back
```

## 🎯 Key Design Patterns

### 1. MVVM Pattern
- **Model**: Data classes and business logic
- **View**: Activities and Fragments (UI)
- **ViewModel**: Mediates between View and Model, handles UI logic

### 2. Repository Pattern
- Abstracts data sources from business logic
- Single source of truth for data operations
- Easy to switch from Firebase to PostgreSQL later

### 3. Observer Pattern
- ViewModels expose `LiveData`/`StateFlow` for UI observation
- Reactive UI updates based on data changes

### 4. Dependency Injection
- Constructor injection for better testability
- Repository interfaces injected into ViewModels

## 🧩 Fragment Communication

### Bottom Navigation
```kotlin
// MainActivity handles fragment switching
private fun setupBottomNavigation() {
    bottomNavigation.setOnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_projects -> showFragment(ProjectsFragment())
            R.id.nav_add -> showFragment(AddProjectFragment())
            R.id.nav_profile -> showFragment(ProfileFragment())
        }
        true
    }
}
```

### Data Sharing Between Fragments
- **SharedViewModel**: For data that needs to persist across fragments
- **Bundle Arguments**: For passing data to new fragments
- **Shared Repository**: Single source of truth accessed by multiple ViewModels

## 💾 State Management

### App Persistence Strategy
```kotlin
// PreferenceManager handles app state persistence
class PreferenceManager(context: Context) {
    fun saveLastFragment(fragmentTag: String)
    fun getLastFragment(): String
    fun saveDifficultyFilter(difficulty: String)
    fun getDifficultyFilter(): String
}
```

### User Session Management
- Firebase Auth handles session persistence automatically
- Custom `AuthRepository` checks authentication state on app launch
- Automatic redirect to login if session expired

## 🔥 Firebase Integration

### Authentication
```kotlin
class FirebaseAuthDataSource {
    fun signInWithEmail(email: String, password: String): Flow<Result<User>>
    fun signInWithGoogle(): Flow<Result<User>>
    fun signOut()
    fun getCurrentUser(): FirebaseUser?
}
```

### Firestore Integration
```kotlin
class FirestoreDataSource {
    fun getProjects(): Flow<List<Project>>
    fun createProject(project: Project): Flow<Result<String>>
    fun updateUserProfile(user: User): Flow<Result<Unit>>
}
```

## 🎨 UI Architecture

### Material Design Implementation
- **Theme**: Custom Material Design 3 theme
- **Components**: Cards, FAB, Bottom Navigation, Chips
- **Animations**: Swipe gestures, fade transitions

### Swipe Gesture Implementation
```kotlin
// Custom swipe detection in ProjectsFragment
private fun setupSwipeGestures() {
    val gestureDetector = GestureDetector(context, SwipeGestureListener())
    cardView.setOnTouchListener { _, event ->
        gestureDetector.onTouchEvent(event)
    }
}
```

## 🧪 Testing Strategy

### Unit Tests
- **ViewModels**: Test business logic with mock repositories
- **Repositories**: Test data operations with mock data sources
- **Use Cases**: Test specific business operations

### Integration Tests
- **Firebase**: Test actual Firebase operations
- **UI**: Test fragment interactions and navigation

## 🚀 Future Architecture Considerations

### PostgreSQL Migration
```kotlin
// Future API service interface
interface ProjectApiService {
    @GET("projects")
    suspend fun getProjects(): Response<List<ProjectDto>>
    
    @POST("projects")
    suspend fun createProject(@Body project: ProjectDto): Response<ProjectDto>
}
```

### Modularization Strategy
- **Feature Modules**: Separate modules for major features
- **Core Module**: Shared utilities and base classes
- **Data Module**: Repository implementations and data sources

## 📊 Performance Considerations

### Memory Management
- Use `viewLifecycleOwner` for LiveData observation in fragments
- Properly dispose of Firebase listeners in `onDestroy()`
- Implement pagination for large project lists

### Network Optimization
- Cache project data locally using Room (future enhancement)
- Implement offline-first strategy
- Optimize Firestore queries with proper indexing

## 🔧 Configuration Management

### Build Variants
```kotlin
// Different configurations for development and production
buildTypes {
    debug {
        applicationIdSuffix ".debug"
        buildConfigField "String", "API_BASE_URL", "\"https://dev-api.projectswipe.com\""
    }
    release {
        buildConfigField "String", "API_BASE_URL", "\"https://api.projectswipe.com\""
    }
}
```

This architecture ensures ProjectSwipe is maintainable, testable, and ready for future enhancements like the PostgreSQL migration and collaboration features.
