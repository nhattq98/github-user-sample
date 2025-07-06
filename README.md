# GitHub User Browser

This Android project allows administrators to browse GitHub users and view detailed information about them. It integrates with the [GitHub API](https://api.github.com/users?per_page=20&since=100) to fetch users in batches and supports pagination, offline viewing, and detail navigation.

---

## 🧾 User Story

> As an administrator, it is possible to browse all users who are the members of GitHub site, then we can see more detailed information about them.

---

## ✅ Acceptance Criteria

- The administrator can look through fetched users’ information.
- The administrator can scroll down to see more users’ information with 20 items per fetch.
- Users’ information must be shown immediately when the administrator launches the application for the second time (cache support).
- Clicking on a user will navigate to the detail page of that user.

---

## 🚀 Features

- Paginated user browsing using GitHub API.
- User detail screen showing extended information.
- Caching of user data for offline and fast relaunch experience.
- Responsive and user-friendly UI built with Jetpack Compose.

---

## 🛠️ Technologies & Architecture

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM + Clean Architecture
- **Paging:** Paging 3 (for lazy loading users)
- **Networking:** Retrofit + Kotlinx Serialization
- **Caching:** Room Database
- **State Management:** Kotlin Flow + ViewModel
- **Data Persistence:** DataStore
- **Testing:** JUnit, Mockito, Turbine (for Flow)

---

## 🧱 Main Components Overview

### 📁 `data/remote/GithubApiService.kt`

```kotlin
interface GithubApiService {
    @GET("users")
    suspend fun getUsers(
        @Query("per_page") perPage: Int,
        @Query("since") since: Int
    ): List<GithubUser>
}
