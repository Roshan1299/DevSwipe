# DevSwipe Architecture

## 🏗️ Overview

DevSwipe is a Tinder-style Android application that connects developers through project collaboration. Built with Kotlin and Jetpack Compose, the app uses a **card-based UI pattern** with swipe gestures for project discovery, Spring Boot with PostgreSQL for backend services, and follows Android's **MVVM (Model-View-ViewModel) architecture** with clean architecture principles and Repository Pattern for complex interactions.

## 🎯 Application Purpose

DevSwipe solves the developer collaboration problem by providing:
- **Project Discovery**: Swipe through curated project ideas matching your interests
- **Skill Matching**: Connect developers based on complementary skills
- **Easy Project Creation**: Submit project ideas to find collaborators
- **Profile Management**: Showcase skills, experience, and completed projects

## 📐 Architectural Pattern

### MVVM with Clean Architecture and Repository Pattern

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│        (Activities, Fragments,          │
│         Composables, ViewModels)        │
├─────────────────────────────────────────┤
│          Domain Layer                   │
│         (Use Cases, Business Logic,     │
│          Entities, Repository Interfaces)│
├─────────────────────────────────────────┤
│            Data Layer                   │
│      (Repository Implementations,       │
│       Remote Data Sources, Local Data,  │
│       Network Services, Database)       │
└─────────────────────────────────────────┘
```

**Key Characteristics:**
- **Single Activity Architecture**: MainActivity hosts all fragments via Navigation Component
- **Feature-based Modularization**: Each major feature organized in separate modules
- **Repository Pattern**: Abstracts data sources and provides clean API to domain layer
- **Retrofit Integration**: REST API calls to Spring Boot backend
- **Dependency Injection**: Hilt for dependency injection management
- **Clean Architecture**: Separation of concerns for maintainability and testability

## 📱 Presentation Layer

### Main Activity Structure

```kotlin
// MainActivity.kt - Single entry point with bottom navigation
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    @Inject
    lateinit var authManager: AuthManager
    
    // Handles navigation, bottom nav visibility, authentication state
    // Controls fragment transitions and user session management
    // Uses Hilt for dependency injection
}
```

### Fragment Organization

```
📁 presentation/ui/
├── 📁 auth/                    # Authentication Flow
│   ├── LoginFragment.kt
│   ├── RegisterFragment.kt
│   └── ResetPasswordFragment.kt
├── 📁 onboarding/              # User Setup
│   ├── OnboardingSkillsFragment.kt
│   └── OnboardingInterestsFragment.kt
├── 📁 home/                    # Main Discovery
│   ├── HomeFragment.kt         # Card stack for project swiping
│   └── FilterBottomSheet.kt    # Project filtering options
├── 📁 projects/                # Project Management
│   └── CreateProjectIdeaFragment.kt
├── 📁 profile/                 # User Profile
│   ├── ProfileFragment.kt
│   └── EditProfileFragment.kt
├── 📁 collaboration/           # Collaboration features
│   └── SeekingCollaboratorsFragment.kt
└── 📁 splash/
    └── SplashActivity.kt
```

### Navigation Flow

```kotlin
// Navigation controlled by MainActivity bottom navigation
private fun setupBottomNavigation() {
    binding.ideasButton.setOnClickListener {
        navigateToFragment(R.id.homeFragment)  // Main discovery
    }
    binding.addButtons.setOnClickListener {
        navigateToFragment(R.id.createProjectIdeaFragment)  // Project creation
    }
    binding.profileButton.setOnClickListener {
        navigateToFragment(R.id.profileFragment)  // User profile
    }
}

// Dynamic bottom nav visibility based on current fragment
navController.addOnDestinationChangedListener { _, destination, _ ->
    when (destination.id) {
        R.id.loginFragment,
        R.id.onboardingSkillsFragment,
        R.id.onboardingInterestsFragment,
        R.id.registerFragment,
        R.id.createProjectIdeaFragment,
        R.id.editProfileFragment -> {
            binding.customBottomNav.visibility = View.GONE
            binding.addButtons.visibility = View.GONE
        }
        else -> {
            binding.customBottomNav.visibility = View.VISIBLE
            binding.addButtons.visibility = View.VISIBLE
        }
    }
}
```

## 🧠 Business Logic Layer (Domain & Data)

### Card Stack Management

```kotlin
// CardStackManager.kt - Handles the card stack UI and interactions
class CardStackManager(
    private val context: Context,
    private val container: FrameLayout,
    var allIdeas: List<ProjectIdea>,
    private val apiService: ApiService,
    private val startingIndex: Int = 0,
    private val onCardSwiped: (ProjectIdea, Int) -> Unit
) {
    private val maxVisible = 3  // Number of visible cards
    var currentTopIndex = startingIndex
    
    // Manages card positioning, scaling, and animation
    // Handles infinite scroll by cycling through projects
    // Coordinates with SwipeHandler for gesture recognition
    // Uses Retrofit API service for data operations
}
```

### Data Repository

```kotlin
// ProjectRepository.kt - Repository interface for project operations
interface ProjectRepository {
    suspend fun getCurrentUserProjects(): Result<List<ProjectIdea>>
    suspend fun getAllProjects(): Result<List<ProjectIdea>>
    suspend fun createProject(createRequest: ProjectCreateRequest): Result<ProjectIdea>
    suspend fun updateProject(id: UUID, updateRequest: UpdateProjectRequest): Result<ProjectIdea>
    suspend fun deleteProject(id: UUID): Result<Unit>
    suspend fun getProject(id: UUID): Result<ProjectIdea>
    suspend fun filterProjects(difficulty: String?, tags: List<String>?): Result<List<ProjectIdea>>
}
```

### Repository Implementation

```kotlin
// ProjectRepositoryImpl.kt - Repository implementation using API service
class ProjectRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ProjectRepository {
    override suspend fun getCurrentUserProjects(): Result<List<ProjectIdea>> {
        return try {
            val response = apiService.getCurrentUserProjects()
            if (response.isSuccessful) {
                val projectResponses = response.body() ?: emptyList()
                val projectIdeas = projectResponses.map { it.toProjectIdea() }
                Result.success(projectIdeas)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // ... other implementations
}
```

## 💾 Data Layer

### Data Models

```kotlin
// ProjectIdea.kt - Main data model
data class ProjectIdea(
    val id: UUID,
    val title: String,
    val previewDescription: String,
    val fullDescription: String,
    val githubLink: String?,
    val tags: List<String>,
    val difficulty: String,
    val createdBy: UserDto
) {
    // Used throughout the app for project representation
    // Supports both preview (swipe) and detailed (flip) views
    // Aligned with Spring Boot backend entity
}
```

### Network Layer with Retrofit

```kotlin
// ApiService.kt - Interface for API endpoints
interface ApiService {
    // Authentication endpoints
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    // Project endpoints
    @POST("api/projects")
    suspend fun createProject(@Body request: ProjectCreateRequest): Response<ProjectResponse>
    
    @GET("api/projects")
    suspend fun getProjects(): Response<List<ProjectResponse>>
    
    // ... other endpoints
}
```

### Backend Entity Mapping

```kotlin
// Project.kt - Backend entity in Spring Boot
@Entity
@Table(name = "projects")
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    
    var title: String,
    var previewDescription: String,
    var fullDescription: String,
    var githubLink: String?,
    
    @ElementCollection
    var tags: List<String>,
    
    var difficulty: String,
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User
)
```

## 🏗️ Project Structure

```
com.first.projectswipe/
├── MainActivity.kt                    # Single activity entry point
├── 📁 presentation/
│   ├── 📁 ui/
│   │   ├── 📁 auth/                  # Authentication fragments
│   │   ├── 📁 onboarding/            # User setup flow
│   │   ├── 📁 home/                  # Main discovery interface
│   │   ├── 📁 projects/              # Project creation/management
│   │   ├── 📁 profile/               # User profile management
│   │   └── 📁 collaboration/         # Collaboration features
│   └── 📁 adapters/                  # RecyclerView adapters
│       ├── ProfilePostAdapter.kt     # User's projects in profile
│       └── ProjectCardAdapter.kt     # For list displays
├── 📁 data/
│   ├── 📁 models/                    # Data models
│   │   └── ProjectIdea.kt            # Main data model
│   ├── 📁 repository/                # Repository implementations
│   │   └── ProjectRepositoryImpl.kt  # Project repository implementation
│   └── 📁 remote/                    # Remote data sources
│       └── NetworkService.kt         # Network API service
├── 📁 domain/
│   ├── 📁 repository/                # Repository interfaces
│   │   └── ProjectRepository.kt      # Project repository interface
│   └── 📁 usecase/                   # Use cases
│       └── GetProjectsUseCase.kt     # Use case for getting projects
├── 📁 network/                       # Network layer
│   ├── ApiService.kt                 # API service interface
│   ├── NetworkModule.kt              # Hilt network module
│   ├── ProjectMapper.kt              # Data mapping between DTOs and models
│   └── RetrofitClient.kt             # Retrofit client configuration
└── 📁 utils/                         # Utility classes
    ├── CardStackManager.kt           # Card stack management
    ├── ProjectCardBinder.kt          # Card content binding
    └── SwipeHandler.kt               # Gesture handling
```

## 🔄 Data Flow Examples

### Project Discovery Flow

```
1. User opens HomeFragment
2. HomeFragment requests projects from ProjectRepository
3. ProjectRepository calls ApiService to fetch projects from Spring Boot backend
4. Backend retrieves projects from PostgreSQL database
5. Backend returns response to Android app
6. CardStackManager creates card stack with ProjectIdea data
7. SwipeHandler attaches to each card for gesture detection
8. ProjectCardBinder populates card content and handles flips
9. User swipes → SwipeHandler detects → CardStackManager updates stack
10. Process repeats with new cards
```

### Project Creation Flow

```
1. User taps Add button → MainActivity navigates to CreateProjectIdeaFragment
2. User fills form and taps Save
3. Fragment creates ProjectCreateRequest object
4. ProjectRepository calls ApiService to save project to backend
5. Backend saves project to PostgreSQL database
6. On success, backend returns created project
7. Fragment receives success response and navigates back to HomeFragment
8. HomeFragment reloads to show new project
```

### Card Interaction Flow

```
1. User sees project card (front side with preview)
2. Taps info button → ProjectCardBinder triggers flip animation
3. Card shows back side with full details
4. User can like/dislike from either side
5. SwipeHandler processes gesture and notifies CardStackManager
6. CardStackManager removes current card and advances to next
7. Swipe action may trigger backend API calls (like, save, etc.)
```

## 🎨 UI Architecture

### Card-Based Interface

```kotlin
// HomeFragment uses custom card stack instead of RecyclerView
private fun setupCardStack() {
    cardStackManager = CardStackManager(
        context = requireContext(),
        container = binding.cardStackContainer,
        allIdeas = projectIdeas,
        apiService = apiService,  // Retrofit API service
        onCardSwiped = { idea, direction ->
            handleCardSwipe(idea, direction)
        }
    )
    cardStackManager.showInitialCards()
}
```

### Custom Animations

```kotlin
// Sophisticated card animations in CardStackManager
private fun restack() {
    for (i in 0 until container.childCount) {
        val card = container.getChildAt(container.childCount - 1 - i)
        val offset = 24 * i      // Stacked card offset
        val scale = 1f - 0.03f * i  // Progressive scaling
        
        card.animate()
            .translationY(offset.toFloat())
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(200)
            .setInterpolator(OvershootInterpolator())
            .start()
    }
}
```

## 🛠️ Technology Stack

```gradle
// Key dependencies from build.gradle
dependencies {
    // Core Android
    implementation("androidx.navigation:navigation-fragment-ktx")
    implementation("androidx.navigation:navigation-ui-ktx")
    
    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    
    // UI Components
    implementation("com.google.android.material:material:1.10.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // JSON
    implementation("com.google.code.gson:gson:2.10.1")
}
```

## 🌐 Spring Boot Backend Integration Pattern

### Retrofit Client with JWT Authentication

```kotlin
// NetworkModule.kt - Hilt module for network configuration
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:8080/" // Emulator

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenProvider: TokenProvider): Interceptor {
        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            val token = tokenProvider.getToken()
            token?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }

            requestBuilder.addHeader("Content-Type", "application/json")

            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
```

### Authentication State Management with JWT

```kotlin
// AuthManager.kt - Authentication manager using JWT tokens
@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? = prefs.getString("jwt_token", null)

    fun isUserLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun logout() {
        prefs.edit().remove("jwt_token").apply()
    }
}
```

## 📊 Performance Considerations

### Card Stack Optimization

```kotlin
// CardStackManager optimizations
class CardStackManager {
    private val maxVisible = 3  // Limit visible cards to prevent memory issues
    
    // Recycle cards instead of creating new ones
    private fun handleCardSwipe(card: View, idea: ProjectIdea, direction: Int) {
        container.removeView(card)  // Remove from view
        currentTopIndex++           // Advance index
        
        if (currentTopIndex >= allIdeas.size) {
            currentTopIndex = 0     // Loop back to beginning
            showInitialCards()      // Refresh stack
            return
        }
        
        addCardAt(maxVisible - 1)   // Add new card at bottom
        restack()                   // Rearrange existing cards
    }
}
```

### Image Loading Strategy

```kotlin
// Efficient image loading with Glide in ProjectCardBinder
// Uses Retrofit API service to fetch user information
// Implements proper caching strategies
Glide.with(context)
    .load(userDto.profileImageUrl)
    .placeholder(R.drawable.ic_profile_placeholder)
    .error(R.drawable.ic_profile_placeholder)
    .circleCrop()                    // Process once, cache result
    .into(frontPfp)
```

## 🚀 Future Architecture Considerations

### Potential Compose Migration

```kotlin
// Future Composable structure with clean architecture
@Composable
fun ProjectSwipeApp() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen() }
        composable("profile") { ProfileScreen() }
        // ... other screens
    }
}
```

### Repository Pattern Enhancement

```kotlin
// Advanced repository with caching and offline support
interface ProjectRepository {
    suspend fun getProjects(): Flow<PagingData<ProjectIdea>>
    suspend fun createProject(createRequest: ProjectCreateRequest): Result<ProjectIdea>
    suspend fun updateProject(id: UUID, updateRequest: UpdateProjectRequest): Result<ProjectIdea>
    suspend fun deleteProject(id: UUID): Result<Unit>
    suspend fun syncProjects(): Result<Unit>
}
```

### Modularization Path

```
devswipe/
├── app/                            # Main application module
├── core/
│   ├── network/                   # Network layer with Retrofit
│   ├── data/                      # Data models and repositories
│   ├── domain/                    # Business logic and use cases
│   └── ui/                        # Shared UI components
├── feature/
│   ├── auth/                      # Authentication feature
│   ├── home/                      # Home/discovery feature
│   ├── projects/                  # Project creation/management
│   ├── profile/                   # User profile
│   └── collaboration/             # Collaboration features
└── shared/
    └── resources/                 # Shared resources
```

## 🎯 Key Design Decisions

### Why MVVM with Repository Pattern?

1. **Separation of Concerns**: Clear boundaries between UI, business logic, and data
2. **Testability**: Easy to unit test ViewModels and Use Cases separately
3. **Maintainability**: Changes in one layer don't affect others
4. **Scalability**: Architecture supports growth with new features

### Spring Boot Backend vs Firebase

- **Current**: Spring Boot with PostgreSQL for full control and customization
- **Advantages**: Custom business logic, complex queries, data relationships
- **Trade-off**: More infrastructure management vs. Firebase's simplicity

This architecture reflects DevSwipe's current implementation focused on scalability and maintainability with clear separation of concerns, supporting both the Android frontend and Spring Boot backend with PostgreSQL database.
