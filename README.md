# Final Project Pemograman Berorientasi Objek

<p align="center">
  <img src="https://github.com/user-attachments/assets/0e845349-9c3b-4bea-8229-086b597df044" />
</p>

<p align="center">
  Kelompok 3 : <br>
  Michael Christian (2406348944) <br>
  Mohamad Rizky Alamsyah (2406420892) <br>
  Muhammad Rasya Syahputra (2406357803) <br>
  Novan Agung Wicaksono (2406401294) <br>

</p>

# Fate/Emerald Abyss

> **"Di mana kedalaman narasi Visual Novel bertemu dengan strategi taktis JRPG Klasik."**

## Ringkasan Game
**Fate/Emerald Abyss** adalah game hybrid yang menggabungkan penceritaan mendalam (Storytelling) dengan mekanik pertempuran strategi berbasis giliran (*turn-based*). Terinspirasi dari gaya narasi *Fate/Grand Order* dan sistem pertarungan *Final Fantasy*, game ini bertujuan menyajikan pengalaman yang mulus antara progresi cerita dan aksi gameplay. Pada F/EA (Fate/Emerald Abyss), kita berperan sebagai master yang akan mengeliminasi master lain dengan melakukan duel antar servant. 

Game akan dimulai dengan story berupa prolog dan player akan diberikan antarmuka berupa background map yang berisi sebuah node, player akan memulai battle ketika memilih dan menginisiasi node tersebut. Setelah player menyelesaikan node tersebut, player akan membuka node baru yang juga berisi story baru dan battle yang lebih menantang.

Game akan dibuat menggunakan **Arsitektur Client-Server**:
- **Frontend (Client):** Dibuat dengan menggunakan **LibGDX** untuk rendering 2D performa tinggi dan kompatibilitas cross-platform.
- **Backend (Server):** Memakai **Spring Boot** untuk menangani penyimpanan data (persistence), keamanan save file, dan konfigurasi game.

---

## Dunia & Latar (Lore)
*The Emerald Abyss sebuah kekosongan mistis yang ditemukan di bawah reruntuhan dunia lama. Para penyihir dan pejuang berkumpul bukan untuk kejayaan, melainkan untuk mencegah kegelapan menelan realitas. Akulturasi budaya dan ciri khas Yogyakarta serta latar yang mirip dengan Yogyakarta lama menjadikan Fate/Emerald Abyss sarat akan cerita*

* **Genre:** High Fantasy / Cyber-Fantasy
* **Gaya Visual:** Sprite 2D bergaya Anime & Dialog Visual Novel
* **Gameplay Inti:**
    1.  **Eksplorasi/Cerita:** Antarmuka Visual Novel dengan percabangan dialog.
    2.  **Pertarungan (Combat):** Tampilan samping (*side-view*), sistem *turn-based* di mana pemain dan musuh saling berhadapan.

---

## Arsitektur Teknis & Design Patterns

Untuk memastikan skalabilitas dan kemudahan maintenance, proyek ini secara ketat mengikuti prinsip Object-Oriented Design dan Design Patterns berikut:

### 1. Pola Arsitektur
* **Client-Server Model:** Komunikasi REST API antara LibGDX dan Spring Boot.
* **Game Loop:** Implementasi kustom menggunakan `ApplicationListener` dari LibGDX untuk mengelola siklus update dan render secara efisien.

### 2. Implementasi Design Patterns
| Pattern | Penggunaan dalam Proyek |
| :--- | :--- |
| **Singleton** | Mengelola instance global tunggal seperti `NetworkManager` (API calls), `ResourceManager` (Aset Gambar/Suara), dan `DatabaseManager`. |
| **Command** | Memisahkan Input dari Aksi. Digunakan untuk Menu Battle (Attack, Skill, Item). Memungkinkan antrian aksi (queue) dan memisahkan logika tombol UI dari logika game. |
| **Strategy** | Mengimplementasikan kecerdasan buatan (AI) Musuh. Musuh yang berbeda menggunakan strategi berbeda (contoh: `AggressiveStrategy`, `DefensiveStrategy`) secara dinamis. |
| **State** | Mengelola *Finite State Machine* yang kompleks untuk: <br> 1. **Layar Game:** Main Menu -> Visual Novel -> Battle. <br> 2. **Animasi Unit:** Idle -> Attack -> Hurt -> Dying. |
| **Observer** | Memisahkan Data dari UI (*Decoupling*). `HealthBarUI` akan "mengamati" `PlayerStats`; saat HP berubah, UI update otomatis tanpa perlu pengecekan berulang (*polling*). |
| **Factory Method** | Pembuatan terpusat untuk objek kompleks seperti Elemen UI (`ButtonFactory`, `TextboxFactory`) dan Entitas Game (`EnemyFactory`). |
| **Object Pool** | Optimasi manajemen memori. Menggunakan ulang objek berat seperti Efek Partikel dan Teks Damage (Floating Text) untuk mencegah *lag* akibat Garbage Collection. |

---

## Tools yang Dipakai

### Frontend (Game Client)
* **Bahasa:** Java 17+
* **Framework:** LibGDX
* **UI:** Scene2D
* **IDE:** IntelliJ IDEA Community Edition

### Backend (API & Database)
* **Framework:** Spring Boot 3.x
* **Database:** PostgreSQL / MySQL
* **Keamanan:** Spring Security (Basic Auth / JWT)
* **Format Data:** JSON

---

## Struktur File

```text
src/
├── main/java/com/fate/abyss/
│   ├── client/                  # Kode Client LibGDX
│   │   ├── core/                # Game Loop & Screens
│   │   ├── entities/            # Class Player, Enemy
│   │   ├── patterns/            # Implementasi Design Patterns
│   │   │   ├── command/         # AttackCmd, SkillCmd
│   │   │   ├── strategy/        # Logika AI
│   │   │   ├── observer/        # Event Listeners
│   │   │   └── factory/         # Pembuat Entitas
│   │   ├── ui/                  # Scene2D Actors & HUD
│   │   └── utils/               # AssetManager, Parsers
│   │
│   └── server/                  # Kode Spring Boot (Modul Terpisah)
│       ├── controller/          # REST Endpoints
│       ├── service/             # Logika Bisnis
│       ├── model/               # Entitas Database
│       └── repository/          # JPA Repositories
```
---
🚀 Roadmap Pengembangan

Fase 1: Pondasi & Sistem Inti
- [x] Inisialisasi proyek LibGDX & proyek Spring Boot.
- [ ] Implementasi Singleton ResourceManager untuk memuat aset.
- [ ] Membuat Layar Game Dasar (Menu, Game) menggunakan State Pattern.
- [ ] Setup API Health Check dasar di Spring Boot.

Fase 2: Sistem Pertarungan (The Core)
- [ ] Membuat entitas Player & Enemy.
- [ ] Implementasi Factory Method untuk memunculkan (spawn) unit.
- [ ] Implementasi Command Pattern untuk aksi tombol (Attack/Defend).
- [ ] Implementasi Observer Pattern untuk update UI HP/Mana.
- [ ] Membangun logika perputaran giliran (Turn-Based Logic).

Fase 3: AI & Poles Visual
- [ ] Implementasi Strategy Pattern untuk perilaku AI Musuh.
- [ ] Implementasi Object Pool untuk VFX dan Angka Damage.
- [ ] Menambahkan State Pattern untuk Animasi Karakter (pergantian Sprite).

Fase 4: Integrasi Backend & Visual Novel
- [ ] Membuat Sistem Dialog Visual Novel (Parser & UI).
- [ ] Menghubungkan NetworkManager untuk save/load stat pemain dari Spring Boot.
- [ ] Finalisasi tema UI (Theming).
