# GameVault

> **Proyek Akhir Pengembangan Aplikasi Mobile (PAM) 2026**
> **Kategori / Tema:** Hiburan (Entertainment & Gaming Information Hub)
> **Author:** Rendra

**GameVault** adalah aplikasi Android berbasis **100% Jetpack Compose (Material 3)** dengan arsitektur **MVVM** bagi para gamer untuk menjelajahi katalog game terbaru dan terpopuler dari **RAWG Video Games Database Public API** (detail game, rating, Metacritic, dan galeri screenshot responsif), dipadukan dengan **Backend REST API (Node.js/Express + PostgreSQL)** untuk autentikasi JWT (`ADMIN` vs `USER`), **Curated Collections**, **Community Game Reviews**, **Wishlist/Backlog**, serta **Local File Storage** untuk ekspor laporan koleksi.

---

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

---

## Tabel Progres Mingguan (Week 10 – Week 14)

**Total Commit Tercapai:** `13 / 30` *(Target minimal 30 commit bertahap)*

| Minggu | Periode | Fokus Modul & Deliverable | Target Commit | Progres | Status |
| :--- | :--- | :--- | :---: | :---: | :---: |
| **Week 10** | 16–22 Nov 2026 | Setup proyek, Tema Dark Gaming Material 3, Navigation Compose, Retrofit RAWG Public API, `ExploreGamesScreen`, & Unit Test | 10 | 12 (+1 init) | Selesai |
| **Week 11** | 23–29 Nov 2026 | Backend PostgreSQL (5 tabel), JWT Auth (`Access` + `Refresh Token`), Login/Register RBAC, Multi-Filter RAWG, & `GameDetailScreen` Galeri Screenshot Responsif | 14 | 0 | Belum Mulai |
| **Week 12** | 30 Nov–6 Des 2026 | Modul CRUDS **Curated Collections** (RBAC Admin CRUDS & User Read/Search) | 10 | 0 | Belum Mulai |
| **Week 13** | 7–13 Des 2026 | Modul CRUDS **Game Reviews & Rating Komunitas** (validasi kepemilikan & play status) | 10 | 0 | Belum Mulai |
| **Week 14** | 14–20 Des 2026 | Modul CRUDS **Wishlist / Backlog**, Local File Storage (`.txt`/`.pdf` export & reader), QA & finalisasi SRS | 15 | 0 | Belum Mulai |

---

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
