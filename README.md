# DevSwipe 🚀

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blueviolet?logo=kotlin)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Android-13%2B-brightgreen?logo=android)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-blue?logo=opensourceinitiative)](https://opensource.org/licenses/MIT)
[![CI](https://github.com/Roshan1299/DevSwipe/workflows/Android%20CI/badge.svg)](https://github.com/Roshan1299/DevSwipe/actions)
[![Release](https://img.shields.io/github/v/release/Roshan1299/DevSwipe?include_prereleases&label=beta&logo=github)](https://github.com/Roshan1299/DevSwipe/releases)

> **Discover side projects and find collaborators through an engaging Tinder-style interface**

DevSwipe is a mobile Android app designed for university students to discover exciting side project ideas and connect with peers for collaboration. Say goodbye to endless scrolling through forums – just swipe, match, and build together!

## 🎯 Problem We're Solving

University students struggle to:
- Find side project ideas aligned with their interests
- Connect with peers who share similar skills and goals
- Navigate through formal, generic platforms that lack engagement
- Balance academic work with meaningful project collaboration

## ✨ Features

### 🔥 Current Features (MVP)
- **🔐 Authentication** - Email & Google Sign-In with Firebase Auth
- **🔑 Password Recovery** - Secure password reset via email
- **⚙️ Profile Setup** - Skills & interests selection for new users
- **📱 Project Discovery** - Swipeable cards with smooth animations
- **🎚️ Smart Filtering** - Filter projects by difficulty (Beginner/Intermediate/Advanced)
- **➕ Project Creation** - Create and share your project ideas
- **👤 Profile Management** - View, edit profile and manage your posts
- **💾 App Persistence** - Remember your last session and preferences

### 🚀 Planned Features
- **🤝 Collaborator Matching** - Swipe to find and connect with potential teammates
- **💬 In-App Messaging** - Chat system for collaboration discussions
- **🔔 Push Notifications** - Stay updated with app announcements
- **🎭 Advanced Filtering** - Filter by skills, interests, and project categories
- **📊 Project Categories** - AI/ML, Web Dev, Mobile Apps, Design, and more
- **🤖 Smart Recommendations** - AI-powered project suggestions based on your profile
- **🏫 University Integration** - Connect with your university email domain
- **📈 Project Tracking** - Monitor project progress and team formation
- **⚡ Enhanced Performance** - PostgreSQL migration for better scalability

## 🎯 Target Audience

University students aged 18-25 interested in:
- **Computer Science & Engineering** students building technical projects
- **Design students** looking for creative collaborations
- **Business students** seeking entrepreneurial opportunities
- **Anyone** passionate about learning and building outside coursework

## 🛠️ Tech Stack

### Frontend
- **Kotlin** - Native Android development
- **Material Design Components** - Modern, intuitive UI
- **Kotlin Coroutines** - Smooth asynchronous operations

### Backend & Database
- **Current**: Firebase (Firestore, Auth, FCM)
- **Future**: PostgreSQL with REST API for enhanced scalability

### Tools & Dependencies
- Android Studio, Git
- Firebase SDK, Google Play Services
- Retrofit (for future API integration)

## 📱 How It Works

1. **Sign Up** - Register with email or Google account
2. **Setup Profile** - Select your skills and interests
3. **Discover Projects** - Swipe through project ideas
   - ← Swipe left to skip
   - → Swipe right if interested
4. **Create Projects** - Share your own project ideas
5. **Connect & Build** - Find collaborators and start building!

## 📦 Installation

Download the latest APK from [Releases](https://github.com/Roshan1299/DevSwipe/releases) or build from source - see [Developer Setup](docs/SETUP.md) for detailed instructions.

## 🛣️ Roadmap

### Phase 1: MVP (Current) ✅
Core swiping functionality and project discovery

### Phase 2: Collaboration 🔄
- Collaborator matching and messaging
- Advanced filtering and recommendations

### Phase 3: Scale & Enhance 📈
- PostgreSQL migration
- University integration
- Advanced project management features

## 🤝 Contributing

We welcome contributions! Please read our [Contributing Guidelines](CONTRIBUTING.md) for details on how to get started.

## 📚 Documentation

- [Developer Setup Guide](docs/SETUP.md) - How to build and run the project
- [API Documentation](docs/API.md) - Firebase integration and data models  
- [UI/UX Guidelines](docs/DESIGN.md) - Design principles and component usage

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Ready to find your next project adventure?** 🎯 Download DevSwipe and start swiping!
