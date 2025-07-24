# Contributing to ProjectSwipe

First off, thank you for considering contributing to ProjectSwipe! 🎉

ProjectSwipe is an open-source Android application that helps university students discover side project ideas through a Tinder-style interface. We welcome contributions from developers of all skill levels.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Issue Guidelines](#issue-guidelines)
- [Community](#community)

## 🤝 Code of Conduct

This project and everyone participating in it is governed by our [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

## 🚀 How Can I Contribute?

### 🐛 Reporting Bugs
- Use the [Bug Report template](.github/ISSUE_TEMPLATE/bug_report.md)
- Check if the issue already exists
- Provide detailed reproduction steps
- Include device/environment information

### 💡 Suggesting Features
- Use the [Feature Request template](.github/ISSUE_TEMPLATE/feature_request.md)
- Explain the problem you're trying to solve
- Describe your proposed solution
- Consider the impact on existing users

### 🔧 Code Contributions
- Fix bugs
- Implement new features
- Improve documentation
- Enhance UI/UX
- Optimize performance
- Add tests

### 📚 Documentation
- Fix typos or unclear instructions
- Add examples and tutorials
- Improve setup guides
- Update README files

## 🏁 Getting Started

### Prerequisites
Before contributing, ensure you have:
- Android Studio (latest stable version)
- JDK 11 or higher
- Git installed
- Firebase account (for backend features)

### Setup Development Environment
1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/ProjectSwipe.git
   cd ProjectSwipe
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/ORIGINAL_OWNER/ProjectSwipe.git
   ```
4. **Follow the setup guide**: See [SETUP.md](SETUP.md) for detailed instructions
5. **Configure Firebase**: Follow [FIREBASE_SETUP.md](FIREBASE_SETUP.md)

### Verify Your Setup
1. Build the project: `./gradlew build`
2. Run tests: `./gradlew test`
3. Launch the app on emulator/device

## 🔄 Development Workflow

### 1. Create a Feature Branch
```bash
# Sync with upstream
git checkout main
git pull upstream main

# Create feature branch
git checkout -b feature/your-feature-name
```

### 2. Make Your Changes
- Write code following our [coding standards](#coding-standards)
- Add tests for new functionality
- Update documentation if needed
- Test your changes thoroughly

### 3. Commit Your Changes
Follow our [commit guidelines](#commit-guidelines):
```bash
git add .
git commit -m "feat: add swipe animation improvements"
```

### 4. Push and Create Pull Request
```bash
git push origin feature/your-feature-name
```
Then create a Pull Request on GitHub.

## 🎨 Coding Standards

### Kotlin Style Guide
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use **4 spaces** for indentation
- Maximum line length: **120 characters**
- Use meaningful variable and function names

### Android Best Practices
- Follow [Android Architecture Guidelines](https://developer.android.com/jetpack/guide)
- Use **MVVM** pattern with **Repository** pattern
- Implement **data binding** where appropriate
- Use **Kotlin Coroutines** for async operations

### Code Formatting
- Use Android Studio's built-in formatter
- Format code before committing: `Ctrl+Alt+L` (Windows/Linux) or `Cmd+Option+L` (Mac)
- Configure ktlint for consistent formatting

### File Structure
```
app/src/main/java/com/yourname/projectswipe/
├── data/           # Data layer (repositories, models)
├── domain/         # Business logic (use cases)
├── presentation/   # UI layer (activities, fragments, viewmodels)
├── di/            # Dependency injection
└── utils/         # Utility classes
```

### Naming Conventions
- **Classes**: PascalCase (`ProjectRepository`)
- **Functions**: camelCase (`getUserProjects()`)
- **Variables**: camelCase (`projectList`)
- **Constants**: SCREAMING_SNAKE_CASE (`MAX_PROJECT_COUNT`)
- **Resources**: snake_case (`activity_main`, `btn_submit`)

## 📝 Commit Guidelines

We follow [Conventional Commits](https://www.conventionalcommits.org/) specification:

### Format
```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### Types
- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, etc.)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Maintenance tasks

### Examples
```bash
feat: add project filtering by difficulty level
fix: resolve crash when swiping on empty project list
docs: update Firebase setup instructions
style: format code according to ktlint rules
refactor: extract authentication logic to repository
test: add unit tests for project matching algorithm
chore: update dependencies to latest versions
```

## 🔄 Pull Request Process

### Before Submitting
- [ ] Fork the repo and create your branch from `main`
- [ ] Ensure your code follows our coding standards
- [ ] Add tests for new functionality
- [ ] Update documentation if needed
- [ ] Run `./gradlew test` and ensure all tests pass
- [ ] Run `./gradlew lint` and fix any warnings
- [ ] Test on both emulator and physical device
- [ ] Rebase your branch on latest `main`

### PR Requirements
1. **Clear title** following conventional commit format
2. **Detailed description** explaining:
   - What changes were made
   - Why they were made
   - How to test them
3. **Link related issues** using keywords (fixes #123)
4. **Screenshots/GIFs** for UI changes
5. **Checklist completion**

### Review Process
1. **Automated checks** must pass (build, tests, lint)
2. **Code review** by maintainers
3. **Testing** on different devices/API levels
4. **Approval** from at least one maintainer
5. **Merge** using squash and merge

### After Merge
- Delete your feature branch
- Update your local main branch
- Close any related issues

## 🐛 Issue Guidelines

### Before Creating an Issue
- Search existing issues to avoid duplicates
- Check if it's already fixed in the latest version
- Gather all necessary information

### Bug Reports
Use the bug report template and include:
- **Clear title** describing the bug
- **Steps to reproduce** the issue
- **Expected vs actual behavior**
- **Device information** (model, Android version, app version)
- **Screenshots/logs** if applicable
- **Environment details** (debug/release build)

### Feature Requests
Use the feature request template and include:
- **Problem description** you're trying to solve
- **Proposed solution** with detailed explanation
- **User benefit** and impact assessment
- **Implementation suggestions** (if technical)
- **Priority level** and affected platforms

## 🏷️ Labels

We use the following labels to categorize issues and PRs:

### Type Labels
- `bug` - Something isn't working
- `enhancement` - New feature or request
- `documentation` - Improvements or additions to docs
- `question` - Further information is requested

### Priority Labels
- `priority: low` - Nice to have
- `priority: medium` - Should be addressed
- `priority: high` - Important issue
- `priority: critical` - Urgent fix needed

### Status Labels
- `good first issue` - Good for newcomers
- `help wanted` - Extra attention is needed
- `wontfix` - This will not be worked on
- `duplicate` - This issue or PR already exists

### Platform Labels
- `android` - Android app related
- `backend` - Backend/Firebase related
- `ci/cd` - Continuous integration/deployment

## 🤝 Community

### Getting Help
- **GitHub Discussions**: For questions and general discussion
- **Issues**: For bug reports and feature requests
- **Email**: [your-email@example.com] for sensitive matters

### Recognition
Contributors will be:
- Added to the CONTRIBUTORS.md file
- Mentioned in release notes for significant contributions
- Credited in the app's about section

### Mentorship
New contributors can:
- Start with issues labeled `good first issue`
- Ask questions in GitHub Discussions
- Request code review feedback
- Pair program with experienced contributors

## 📊 Development Statistics

Help us improve by contributing to:
- **Code coverage**: Currently at X%
- **Performance**: App startup time, memory usage
- **User experience**: UI/UX improvements
- **Accessibility**: Supporting users with disabilities

## 🎉 Thank You!

Your contributions make ProjectSwipe better for university students worldwide. Every bug fix, feature addition, and documentation improvement helps students discover and collaborate on amazing projects.

---

**Questions?** Feel free to reach out through GitHub Discussions or create an issue with the `question` label.

**Happy Contributing!** 🚀
