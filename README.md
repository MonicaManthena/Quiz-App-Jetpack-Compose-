# Android Quiz App — Cricut Assessment

A small Android quiz application built with Jetpack Compose, Material3, and MVVM.

## Architecture

```
app/src/main/java/com/cricut/quizapp/
├── data/
│   ├── model/
│   │   ├── Question.kt        ← Sealed class for all question types
│   │   └── QuizState.kt       ← UI state + Answer sealed class
│   └── repository/
│       └── QuizRepository.kt  ← Interface + mock impl with simulated delay
├── ui/
│   ├── assessment/
│   │   ├── AssessmentScreen.kt      ← Root composable (required name)
│   │   ├── AssessmentViewModel.kt   ← StateFlow-based ViewModel
│   │   └── components/
│   │       ├── TrueFalseQuestion.kt
│   │       ├── MultipleChoiceQuestion.kt
│   │       ├── MultipleSelectionQuestion.kt
│   │       ├── OpenEndedQuestion.kt
│   │       └── QuizCompleteScreen.kt
│   └── theme/
│       ├── Color.kt / Theme.kt / Type.kt
└── MainActivity.kt
```

## Key Design Decisions

### State Management
- `QuizState` is a single immutable data class held in `MutableStateFlow` inside the ViewModel.
- Answers are stored as `Map<questionId, Answer>` — keyed by ID, not index — so back-navigation always restores the exact prior answer regardless of list reordering.
- `collectAsStateWithLifecycle` ensures the UI only collects when the lifecycle is at least STARTED, preventing wasted work in the background.

### Configuration Change Survival
- The ViewModel outlives Activity recreation. All state (current question index, all answers, loading state) lives in the ViewModel — nothing in the composable layer.

### Repository Pattern
- `QuizRepository` is an interface. `QuizRepositoryImpl` adds a 600ms `delay()` to simulate a real network call and make the loading state visible during development.
- The ViewModel's `init` block launches the fetch on `viewModelScope`, which is automatically cancelled when the ViewModel is cleared.

### Navigation
- Single-screen approach: questions swap in/out with `AnimatedContent` slide transitions.
- The "Next" / "Submit" button is disabled until the current question has a non-null answer (`hasAnsweredCurrent`), providing light input validation.

## Question Types Implemented
1. **True/False** — two pill buttons, mutually exclusive
2. **Multiple Choice** — four vertical option buttons, mutually exclusive
3. **Multiple Selection** — four checkbox cards, any combination allowed
4. **Open Ended** — `OutlinedTextField` with live character counter

## Running the App
1. Open in Android Studio Hedgehog (2023.1.1) or newer.
2. Sync Gradle.
3. Run on an emulator or device running API 26+.

## Running Unit Tests
```bash
./gradlew test
```
Tests cover: loading state, navigation (forward/back/bounds), answer persistence, quiz completion, and restart logic.
