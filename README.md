# Sistem Inventaris Laboratorium (JavaFX)

Tugas Proyek Mata Kuliah Pemrograman Berorientasi Objek, Teknik Komputer, Universitas Diponegoro.
Dibuat dengan Java 17+ dan JavaFX 21, arsitektur MVC + DAO, penyimpanan file CSV.

## Cara menjalankan di Eclipse

Kebutuhan:
- **Eclipse IDE for Java Developers** (versi terbaru sudah membawa Java 21 dan dukungan Maven).
- **Koneksi internet saat pertama kali import**, karena Eclipse mengunduh JavaFX otomatis lewat Maven.

Langkah:
1. Buka Eclipse, lalu **File > Import... > Maven > Existing Maven Projects**, lalu **Next**.
2. Di **Root Directory**, klik **Browse** dan pilih folder ini (folder yang berisi `pom.xml`). Pastikan `pom.xml` tercentang, lalu klik **Finish**.
3. Tunggu sampai Eclipse selesai mengunduh library (lihat progres di kanan bawah).
4. Buka `src/main/java > inventarislab > Launcher.java`.
5. Klik kanan **`Launcher.java`**, lalu **Run As > Java Application**.
6. Login memakai salah satu akun berikut:

| Username  | Password     | Hak akses                          |
|-----------|--------------|------------------------------------|
| `admin`   | `admin123`   | Semua fitur, termasuk hapus        |
| `laboran` | `laboran123` | Tambah/ubah saja, tidak bisa hapus |

> Penting: yang dijalankan adalah **`Launcher.java`**, bukan `Main.java`. Kalau `Main.java` yang dijalankan langsung, akan muncul error "JavaFX runtime components are missing".

Saat pertama kali dijalankan, folder `data/` terisi otomatis dengan data contoh: 4 ruangan (Lab Multimedia, Lab Embedded, Lab Jaringan, Lab Software) dan 12 aset. Untuk kembali ke data awal, hapus folder `data/`.

`UjiKonsol.java` (Run As > Java Application) menguji model + DAO lewat konsol tanpa GUI. File ini dipakai untuk checkpoint Minggu 2.

### Jika ada masalah
- **Banyak tanda merah setelah import:** klik kanan proyek, lalu **Maven > Update Project... > OK**.
- **"JavaFX runtime components are missing":** pastikan yang dijalankan `Launcher.java`.
- **Library tidak terunduh:** cek koneksi internet, lalu ulangi **Maven > Update Project** dengan centang *Force Update*.
- **Alternatif lewat terminal (jika Maven terpasang):** `mvn javafx:run`.

## Struktur package

```
src/main/java/inventarislab
├── Launcher, Main, AppContext, UjiKonsol
├── model       Aset (abstract) ← Komputer, PerangkatJaringan, Furnitur, PerangkatEmbedded
│               Ruangan, Pengguna, Identifiable (interface), Perawatan (interface),
│               JenisAset, Kondisi, Peran (enum)
├── exception   ValidasiException, DataAksesException, AutentikasiException
├── dao         Dao<T> (interface), AbstractFileDao<T> (abstract), AsetDao, RuanganDao, PenggunaDao
├── service     AuthService, InventarisService, RuanganService
├── controller  LoginController, MainController, DashboardController, AsetController, RuanganController
├── view        LoginView, MainView, DashboardView, AsetView, AsetFormDialog,
│               RuanganView, RuanganFormDialog, Ui, Ikon
└── util        Validasi, Format, PasswordUtil, DataAwal
src/main/resources/inventarislab/view
└── style.css (tema tampilan), undip.png (logo), lab.jpg (foto halaman login)
docs/           class diagram & package diagram (PNG + sumber PlantUML .puml)
```

## Pembagian kerja & commit Git (3 anggota)

Setiap anggota wajib commit sendiri bagian yang menjadi tanggung jawabnya:

| Anggota | Peran | File yang di-commit |
|---|---|---|
| Anggota 1 | Model & Data | `model/*`, `exception/*`, `dao/*`, `util/Validasi`, `util/DataAwal`, `UjiKonsol` |
| Anggota 2 | Tampilan (View) | `view/*`, `src/main/resources/*` (CSS, logo, foto) |
| Anggota 3 | Controller & Integrasi | `controller/*`, `service/*`, `Main`, `Launcher`, `AppContext`, `util/Format`, `util/PasswordUtil`, `pom.xml` |

Contoh alur commit:
```
git init
git add .gitignore README.md docs && git commit -m "Inisialisasi proyek dan dokumen desain"
# Anggota 1:
git add src/main/java/inventarislab/model src/main/java/inventarislab/exception src/main/java/inventarislab/dao
git commit -m "Model, exception, dan DAO file"
# Anggota 3:
git add pom.xml src/main/java/inventarislab/service src/main/java/inventarislab/controller
git commit -m "Service dan controller"
# Anggota 2:
git add src/main/java/inventarislab/view src/main/resources
git commit -m "Tampilan JavaFX dan tema CSS"
```

## Kredit
- Ikon: bentuk dari Material Icons (Google), lisensi Apache 2.0.
- Foto laboratorium dan logo Universitas Diponegoro disediakan oleh kelompok.
