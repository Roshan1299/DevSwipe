# DevSwipe Architecture

## 🏗️ Overview

DevSwipe is a Tinder-style Android application that connects developers through project collaboration. Built with Kotlin, the app uses a **card-based UI pattern** with swipe gestures for project discovery, Firebase for backend services, and follows Android's **View-based architecture** with custom utility classes for complex interactions.

## 🎯 Application Purpose

DevSwipe solves the developer collaboration problem by providing:
- **Project Discovery**: Swipe through curated project ideas matching your interests
- **Skill Matching**: Connect developers based on complementary skills
- **Easy Project Creation**: Submit project ideas to find collaborators
- **Profile Management**: Showcase skills, experience, and completed projects

## 📐 Architectural Pattern

### View-Based Architecture with Utils Pattern

```
┌─────────────────────────────────────────┐
│               Presentation              │
│        (Activities, Fragments)          │
├─────────────────────────────────────────┤
│            Business Logic               │
│         (Utils, Custom Classes)         │
├─────────────────────────────────────────┤
│               Data Layer                │
│         (Firebase, Data Models)         │
└─────────────────────────────────────────┘
```

**Key Characteristics:**
- **Single Activity Architecture**: MainActivity hosts all fragments via Navigation Component
- **Fragment-based Features**: Each major feature is a separate fragment
- **Utility-driven Business Logic**: Complex logic encapsulated in utility classes
- **Firebase Direct Integration**: Direct Firebase calls from fragments/utils
- **Custom View Components**: Complex UI interactions handled by custom utility classes

## 📱 Presentation Layer

### Main Activity Structure

```kotlin
// MainActivity.kt - Single entry point with bottom navigation
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    
    // Handles navigation, bottom nav visibility, authentication state
    // Controls fragment transitions and user session management
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
├── 📁 collaboration/           # Future collaboration features
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
        navigateToFragment(R.id.createPostFragment)  // Project creation
    }
    binding.profileButton.setOnClickListener {
        navigateToFragment(R.id.profileFragment)  // User profile
    }
}

// Dynamic bottom nav visibility based on current fragment
navController.addOnDestinationChangedListener { _, destination, _ ->
    when (destination.id) {
        R.id.loginFragment, R.id.createPostFragment -> {
            binding.customBottomNav.visibility = View.GONE
        }
        else -> {
            binding.customBottomNav.visibility = View.VISIBLE
        }
    }
}
```

## 🧠 Business Logic Layer (Utils)

### Card Stack Management

```kotlin
// CardStackManager.kt - Handles the card stack UI and interactions
class CardStackManager(
    private val context: Context,
    private val container: FrameLayout,
    var allIdeas: List<ProjectIdea>,
    private val onCardSwiped: (ProjectIdea, Int) -> Unit
) {
    private val maxVisible = 3  // Number of visible cards
    var currentTopIndex = startingIndex
    
    // Manages card positioning, scaling, and animation
    // Handles infinite scroll by cycling through projects
    // Coordinates with SwipeHandler for gesture recognition
}
```

### Swipe Gesture Handling

```kotlin
// SwipeHandler.kt - Custom touch handling for card swiping
class SwipeHandler(
    private val card: View,
    private val swipeThreshold: Float = 250f,
    private val flingThreshold: Float = 1000f,
    private val onSwipeComplete: (direction: Int) -> Unit
) {
    // Implements custom touch detection
    // Handles card animation during drag
    // Provides visual feedback (labels, haptic feedback)
    // Determines swipe completion based on distance/velocity
}
```

### Card Content Binding

```kotlin
// ProjectCardBinder.kt - Handles card content and flip animations
object ProjectCardBinder {
    fun bind(card: View, context: Context, idea: ProjectIdea) {
        // Binds project data to front/back card layouts
        // Implements card flip animation using 3D rotation
        // Manages user profile loading with UserUtils
        // Sets up button interactions (like/dislike/info)
    }
    
    // Implements sophisticated card flip with 3D rotation
    private fun flipCard(root: View, front: View, back: View, showBack: Boolean) {
        val scale = root.context.resources.displayMetrics.density
        root.cameraDistance = 8000 * scale  // 3D perspective
        
        // Two-phase animation: rotate out, switch content, rotate in
        val outAnim = ObjectAnimator.ofFloat(root, "rotationY", 0f, 90f)
        val inAnim = ObjectAnimator.ofFloat(root, "rotationY", -90f, 0f)
    }
}
```

### User Data Management

```kotlin
// UserUtils.kt - Handles user data operations
object UserUtils {
    fun getUserInfo(userId: String, callback: (UserInfo) -> Unit) {
        // Direct Firebase queries for user information
        // Caches user data for performance
        // Handles profile image loading coordination
    }
}
```

## 💾 Data Layer

### Data Models

```kotlin
// ProjectIdea.kt - Main data model
data class ProjectIdea(
    val id: String = "",
    val title: String = "",
    val previewDescription: String = "",      // For swipe cards
    val fullDescription: String = "",         // For detailed view
    val createdBy: String = "",               // User ID
    val tags: List<String> = emptyList(),     // Skills/technologies
    val createdByName: String = "",
    val difficulty: String = "",              // Beginner/Intermediate/Advanced
    val githubLink: String = "",
    val timeline: String = ""
) {
    // Used throughout the app for project representation
    // Supports both preview (swipe) and detailed (flip) views
}
```

### Firebase Integration

```kotlin
// Direct Firebase integration pattern used throughout fragments
class CreateProjectIdeaFragment {
    private fun saveProjectIdea() {
        val projectIdea = ProjectIdea(
            title = binding.projectTitleEditText.text.toString(),
            previewDescription = binding.previewDescriptionEditText.text.toString(),
            // ... other fields
        )
        
        FirebaseFirestore.getInstance()
            .collection("project_ideas")
            .add(projectIdea)
            .addOnSuccessListener { /* Handle success */ }
            .addOnFailureListener { /* Handle error */ }
    }
}
```

### Firestore Schema

```javascript
// Collections Structure
project_ideas: {
  [documentId]: {
    title: "React Native Food App",
    previewDescription: "Short description for swipe cards...",
    fullDescription: "Detailed project description...",
    createdBy: "userId123",
    tags: ["React Native", "Node.js", "Design"],
    difficulty: "Intermediate",
    githubLink: "https://github.com/user/project",
    timeline: "3 months",
    createdByName: "John Doe"
  }
}

users: {
  [userId]: {
    email: "user@example.com",
    displayName: "John Doe",
    skills: ["JavaScript", "React", "Node.js"],
    interests: ["Mobile", "Web", "AI"],
    profileImageUrl: "https://...",
    // ... other profile fields
  }
}
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
│   │   └── 📁 collaboration/         # Future collaboration features
│   └── 📁 adapters/                  # RecyclerView adapters
│       ├── ProfilePostAdapter.kt     # User's projects in profile
│       └── ProjectCardAdapter.kt     # Future: if switching from utils
├── 📁 data/
│   └── 📁 models/
│       └── ProjectIdea.kt            # Main data model
└── 📁 utils/                         # Business logic utilities
    ├── CardStackManager.kt           # Card stack management
    ├── ProjectCardBinder.kt          # Card content binding
    ├── SwipeHandler.kt               # Gesture handling
    └── UserUtils.kt                  # User data operations
```

## 🔄 Data Flow Examples

### Project Discovery Flow

```
1. User opens HomeFragment
2. HomeFragment loads projects from Firestore
3. CardStackManager creates card stack with ProjectIdea data
4. SwipeHandler attaches to each card for gesture detection
5. ProjectCardBinder populates card content and handles flips
6. User swipes → SwipeHandler detects → CardStackManager updates stack
7. Process repeats with new cards
```

### Project Creation Flow

```
1. User taps Add button → MainActivity navigates to CreateProjectIdeaFragment
2. User fills form and taps Save
3. Fragment creates ProjectIdea object
4. Direct Firestore call to save project
5. On success, navigate back to HomeFragment
6. HomeFragment reloads to show new project
```

### Card Interaction Flow

```
1. User sees project card (front side with preview)
2. Taps info button → ProjectCardBinder triggers flip animation
3. Card shows back side with full details
4. User can like/dislike from either side
5. SwipeHandler processes gesture and notifies CardStackManager
6. CardStackManager removes current card and advances to next
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
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    
    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    
    // UI Components
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
}
```

## 🔥 Firebase Integration Pattern

### Direct Fragment Integration

```kotlin
// Pattern used throughout the app - direct Firebase calls from fragments
class HomeFragment : Fragment() {
    private fun loadProjects() {
        FirebaseFirestore.getInstance()
            .collection("project_ideas")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val ideas = documents.mapNotNull { doc ->
                    doc.toObject<ProjectIdea>().copy(id = doc.id)
                }
                cardStackManager.updateIdeas(ideas)
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }
}
```

### Authentication State Management

```kotlin
// MainActivity handles auth state globally
override fun onCreate(savedInstanceState: Bundle?) {
    auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    
    // Auto-navigate to home if logged in
    if (currentUser != null && savedInstanceState == null) {
        navController.navigate(R.id.homeFragment)
    }
    // Otherwise, navigation graph handles login flow
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
UserUtils.getUserInfo(idea.createdBy) { userInfo ->
    userInfo.profileImageUrl?.let { url ->
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .circleCrop()                    // Process once, cache result
            .into(frontPfp)
    }
}
```

## 🚀 Future Architecture Considerations

### Potential Refactoring to MVVM

```kotlin
// Future ViewModel structure for better separation
class HomeViewModel : ViewModel() {
    private val _projects = MutableLiveData<List<ProjectIdea>>()
    val projects: LiveData<List<ProjectIdea>> = _projects
    
    fun loadProjects() {
        viewModelScope.launch {
            // Move Firebase logic from fragment to ViewModel
            // Add proper error handling and loading states
        }
    }
}
```

### Repository Pattern Introduction

```kotlin
// Future repository layer
interface ProjectRepository {
    suspend fun getProjects(): Flow<List<ProjectIdea>>
    suspend fun createProject(project: ProjectIdea): Result<String>
    suspend fun deleteProject(projectId: String): Result<Unit>
}

class ProjectRepositoryImpl : ProjectRepository {
    // Encapsulate Firebase operations
    // Add caching, offline support
    // Better error handling
}
```

### Modularization Path

```
projectswipe/
├── app/                    # Main application module
├── core/
│   ├── ui/                # Shared UI components (CardStackManager, etc.)
│   ├── data/              # Data models and Firebase utilities
│   └── utils/             # Shared utilities
├── feature/
│   ├── home/              # Home/discovery feature
│   ├── projects/          # Project creation/management
│   ├── profile/           # User profile
│   └── auth/              # Authentication
└── shared/
    └── resources/         # Shared resources
```

## 🎯 Key Design Decisions

### Why Utils-Based Architecture?

1. **Rapid Development**: Direct Firebase integration speeds up development
2. **Custom UI Needs**: Complex card interactions require custom solutions
3. **Small Team**: Simple architecture easier to maintain with limited resources
4. **Proof of Concept**: Focus on core functionality over architectural purity

### Card Stack vs RecyclerView

```kotlin
// Chosen CardStackManager over RecyclerView because:
// 1. Custom swipe gestures with physics
// 2. 3D card flip animations
// 3. Stacked card visual effect
// 4. Complex touch handling requirements
```

### Direct Firebase vs Repository Pattern

- **Current**: Direct Firebase calls for simplicity and speed
- **Future**: Repository pattern for testability and maintainability
- **Trade-off**: Development speed vs. architectural flexibility

This architecture reflects ProjectSwipe's current implementation focused on rapid prototyping and core functionality, with clear paths for future architectural improvements as the team and requirements grow.
