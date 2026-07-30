# Student Finance - Aplikasi Keuangan Pelajar

Aplikasi Android native untuk mengelola keuangan siswa/pelajar secara offline-first.

## Tech Stack

- **Bahasa**: Kotlin
- **UI**: Jetpack Compose (Material Design 3)
- **Database Lokal**: Room (SQLite)
- **Arsitektur**: MVVM (ViewModel + Repository pattern)
- **Dependency Injection**: Hilt
- **Penyimpanan Preferensi**: DataStore
- **Grafik**: Vico
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34

## Fitur Utama

### A. Manajemen Transaksi
- Catat pemasukan & pengeluaran
- Kategori transaksi yang bisa dikustomisasi
- Input cepat dengan kalkulator built-in
- Lampirkan foto struk/bukti transaksi (disimpan lokal)
- Catatan/deskripsi per transaksi
- Transaksi berulang (recurring)

### B. Anggaran (Budgeting)
- Buat anggaran bulanan per kategori
- Alert saat mendekati atau melebihi batas anggaran
- Progress bar visual sisa anggaran

### C. Tabungan & Target Keuangan
- Fitur target menabung
- Tracking progress menabung dengan visualisasi
- Estimasi waktu tercapainya target

### D. Laporan & Analisis
- Ringkasan harian, mingguan, bulanan, tahunan
- Grafik pie chart dan line/bar chart
- Perbandingan pemasukan vs pengeluaran
- Export laporan ke PDF/CSV

### E. Pengingat (Reminder)
- Reminder untuk mencatat transaksi harian
- Reminder tagihan/pembayaran menggunakan WorkManager + AlarmManager
- 100% offline, tanpa internet

### F. Keamanan
- Kunci aplikasi dengan PIN/pattern atau biometrik
- Data terenkripsi di database lokal (SQLCipher)

### G. Backup & Restore Lokal
- Export seluruh data ke file JSON
- Import/restore dari file backup

### H. Personalisasi
- Dark mode / light mode
- Pilihan mata uang (default Rupiah)
- Widget home screen (planned)
- Multi-profil (opsional)

## Struktur Proyek

```
com.student.finance/
├── data/
│   ├── local/
│   │   ├── entity/          # Room Entities
│   │   ├── dao/             # Data Access Objects
│   │   ├── StudentFinanceDatabase.kt
│   │   └── DataStoreManager.kt
│   └── repository/          # Repository Pattern
├── di/
│   └── AppModule.kt           # Hilt DI Module
├── ui/
│   ├── theme/                 # Material 3 Theme
│   ├── navigation/            # Navigation Graph
│   ├── screens/               # Compose Screens
│   ├── components/            # Reusable UI Components
│   └── viewmodel/             # MVVM ViewModels
├── worker/
│   └── ReminderWorker.kt      # Background WorkManager
├── util/
│   ├── CurrencyFormatter.kt
│   ├── DateUtils.kt
│   └── BackupRestoreManager.kt
├── MainActivity.kt
└── StudentFinanceApp.kt
```

## Cara Build & Run

### Prerequisites
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17
- Android SDK dengan API 34

### Langkah-langkah

1. **Clone/Extract Proyek**
   ```bash
   unzip StudentFinance.zip
   cd StudentFinance
   ```

2. **Buka di Android Studio**
   - File > Open > Pilih folder `StudentFinance`
   - Tunggu Gradle sync selesai

3. **Build Project**
   - Build > Make Project (Ctrl+F9)
   - Atau jalankan dari terminal:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Aplikasi**
   - Hubungkan device Android atau buka emulator
   - Pastikan device/emulator memiliki API 24+
   - Klik tombol Run (Shift+F10) atau:
   ```bash
   ./gradlew installDebug
   ```

### Troubleshooting

- **Gradle Sync Failed**: Pastikan JDK 17 terpasang dan di-set di Android Studio
- **KSP Error**: Clean project (Build > Clean Project) lalu rebuild
- **Hilt Error**: Pastikan annotation processing aktif dan KSP plugin terpasang

## Arsitektur Database (Room)

### Entity

**Transaction**
- id (Long, PK)
- amount (Double)
- type (String: INCOME/EXPENSE)
- categoryId (Long, FK)
- date (Long, timestamp)
- description (String?)
- receiptPhotoPath (String?)
- isRecurring (Boolean)
- recurringInterval (String?)

**Category**
- id (Long, PK)
- name (String)
- iconName (String)
- colorHex (String)
- type (String: INCOME/EXPENSE)

**Budget**
- id (Long, PK)
- categoryId (Long, FK)
- limitAmount (Double)
- period (String)
- month (Int)
- year (Int)
- alertThreshold (Double)

**SavingGoal**
- id (Long, PK)
- name (String)
- targetAmount (Double)
- savedAmount (Double)
- deadline (Long?)
- iconName (String)
- colorHex (String)

**Reminder**
- id (Long, PK)
- title (String)
- message (String?)
- triggerTime (Long)
- type (String)
- isRecurring (Boolean)
- recurringInterval (String?)
- isEnabled (Boolean)

## Lisensi

MIT License - Aplikasi ini 100% open source dan tidak mengumpulkan data pengguna.

## Catatan Keamanan & Privasi

- Semua data disimpan secara lokal di perangkat
- Tidak ada pengiriman data ke server/cloud
- Tidak memerlukan akun/login online
- Cocok untuk pengguna di bawah umur (tidak ada tracking/data collection)
