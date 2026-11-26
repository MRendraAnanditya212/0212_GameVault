# GameVault

> **Proyek Akhir Pengembangan Aplikasi Mobile (PAM) 2026**
> **Kategori / Tema:** Hiburan (Entertainment & Gaming Information Hub)
> **Author:** Rendra

**GameVault** adalah aplikasi Android berbasis **100% Jetpack Compose (Material 3)** dengan arsitektur **MVVM** bagi para gamer untuk menjelajahi katalog game terbaru dan terpopuler dari **RAWG Video Games Database Public API** (detail game, rating, Metacritic, dan galeri screenshot responsif), dipadukan dengan **Backend REST API (Node.js/Express + PostgreSQL)** untuk autentikasi JWT (`ADMIN` vs `USER`), **Curated Collections**, **Community Game Reviews**, **Wishlist/Backlog**, serta **Local File Storage** untuk ekspor laporan koleksi.

---

## API GameVault
# https://github.com/MRendraAnanditya212/0212_API_GameVault

## Arsitektur Hybrid

```text
+-------------------------------------------------------------------+
|               Android Client — GameVault (Kotlin)                 |
|  Jetpack Compose (Material 3) • Navigation Compose • MVVM         |
|  StateFlow • Coil AsyncImage • Retrofit2 / OkHttp3                |
+-------------------------------+-----------------------------------+
                                |
          +---------------------+---------------------+
          |                                           |
          v                                           v
+--------------------------+             +-------------------------------+
|   RAWG Public API (v1)   |             |  GameVault REST API (Week 11+) |
|   https://api.rawg.io    |             |  Node.js / Express + JWT RBAC  |
|   • GET /games           |             |  PostgreSQL (5 Tabel Relasi):  |
|   • GET /games/:id       |             |  users, curated_collections,   |
|   • GET /games/:id/      |             |  collection_games,             |
|     screenshots          |             |  game_reviews, user_wishlists  |
+--------------------------+             +-------------------------------+
```

---

## Fitur yang Sudah Tersedia (Week 10)

- **Dark Gaming Material 3 Theme:** Palet Obsidian Dark (`#0B0E14`), Electric Cyan (`#00E5FF`), Cyber Violet (`#8B5CF6`), tipografi terstruktur, dan badge warna Metacritic (High/Mid/Low).
- **Compose Navigation Graph:** Routing terpusat via `Screen` sealed class (`Explore`, `Collections`, `Wishlist`, `Profile`, `GameDetail/{gameId}`, `Login`, `Register`, `ExportHistory`) lengkap dengan bottom navigation bar.
- **Fondasi Koneksi RAWG Public API:** `RetrofitClient` dengan API Key interceptor (`BuildConfig.RAWG_API_KEY`), logging interceptor, `RawgApiService`, DTO & Domain mappers (`GameModel.kt`), dan `RawgGameRepository` dengan penanganan error jaringan/HTTP.
- **Explore Games (MVVM):** `ExploreViewModel` + `ExploreGamesScreen` dengan pencarian ter-debounce, filter kategori (`Popular`, `Top Rated`, `Latest`), grid responsif (`LazyVerticalGrid` dengan `GridCells.Adaptive`), Coil cover image loading, pagination (`Muat Lebih Banyak`), serta state Loading/Empty/Error.
- **Unit Testing:** Pengujian mapper `GameDto.toDomain()` dan perilaku reactive `ExploreViewModel` (`ExploreViewModelTest.kt`).

---

## Konfigurasi & Menjalankan Proyek

1. Daftar dan dapatkan API Key gratis di [RAWG Video Games Database API](https://rawg.io/apidocs).
2. Buka file `local.properties` di root proyek (file ini otomatis di-ignore oleh Git) lalu tambahkan:
   ```properties
   RAWG_API_KEY=your_rawg_api_key_here
   ```
3. Sync Gradle di Android Studio atau jalankan verifikasi dari terminal:
   ```bash
   ./gradlew testDebugUnitTest assembleDebug
   ```
## 4. Progress Mingguan (Weekly Progress)
Berikut adalah linimasa pengembangan proyek GameVault yang direkonstruksi secara riil berdasarkan riwayat git commit:

### 🗓️ Minggu 10 (20 November 2026)
* **Inisialisasi Lingkungan & Tema UI GameVault** ( `1a752ef` , `1cd9d9b` , `06feb7e` , `b82e6ea` ): Inisialisasi proyek, penyesuaian skema warna Dark Gaming Material 3, tipografi, tema UI GameVault, dan dokumentasi `README.md`.
* **Sistem Navigasi Compose** ( `9bdaea6` , `2843ba3` ): Pembuatan `GameVaultNavGraph`, sealed class `Screen`, dan `BottomNavItem` untuk navigasi terpusat.
* **Integrasi RAWG Public API & Model Data** ( `aaa6d08` , `64b19db` , `32de961` ): Pembuatan DTO RAWG API, model domain `Game`, `UiState`, `RawgApiService`, `RetrofitClient`, serta repositori `RawgGameRepository`.
* **Katalog Game Explore & Komponen UI** ( `0a13c8b` , `e8fc8ca` , `22a3df7` ): Pembuatan `ExploreUiState`, `ExploreViewModel`, komponen UI reusable ( `GameCard` , `CommonComponents` ), dan antarmuka `ExploreGamesScreen`.
* **Pengujian Unit (Unit Testing)** ( `58d56f8` , `2cab75b` ): Pembuatan unit test `ExploreViewModelTest` untuk memverifikasi logika ViewModel dan mapper DTO.



### 🗓️ Minggu 11 (26 November 2026)
* **Manajemen Sesi & Local Storage DataStore** ( `787fc95` , `f02771e` ): Manajemen sesi pengguna dan penyimpanan token lokal menggunakan `TokenManager` dan `UserSession` berbasis DataStore Preferences.
* **Keamanan Jaringan & Token Interceptor** ( `a1ef8be` , `e887371` , `0ad7dd7` ): Integrasi `AuthInterceptor` untuk menyisipkan JWT Bearer Token, `TokenAuthenticator` untuk penanganan otomatis refresh token (401 Unauthorized), dan konfigurasi backend API pada `RetrofitClient`.
* **Sistem Autentikasi Backend & Repositori** ( `ee0ed05` , `c9b3976` , `849571f` ): Pembuatan DTO & model `AuthModel`, `AuthRepository`, `AuthViewModel`, serta antarmuka `LoginScreen` dan `RegisterScreen`.
* **Detail Game Responsif & Multi-Filter Explore** ( `849571f` , `f02771e` , `cfb480f` ): Pembuatan `GameDetailViewModel`, layar `GameDetailScreen` dengan galeri screenshot responsif ( `HorizontalPager` + `AsyncImage` ), serta fitur multi-filter dan pencarian ter-debounce pada katalog game.


## Struktur Direktori Utama (`app/`)

```text
app/src/main/java/com/example/gamevault/
├── MainActivity.kt
├── core/
│   ├── common/UiState.kt
│   └── network/
│       ├── RawgApiService.kt
│       └── RetrofitClient.kt
├── data/
│   ├── model/
│   │   ├── RawgResponseDto.kt
│   │   └── GameModel.kt
│   └── repository/GameRepository.kt
└── ui/
    ├── components/
    │   ├── CommonComponents.kt
    │   └── GameCard.kt
    ├── explore/
    │   ├── ExploreGamesScreen.kt
    │   ├── ExploreUiState.kt
    │   └── ExploreViewModel.kt
    ├── navigation/
    │   ├── GameVaultNavGraph.kt
    │   └── Screen.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```
