package inventarislab.service;

import inventarislab.dao.Dao;
import inventarislab.exception.AutentikasiException;
import inventarislab.model.Pengguna;

/**
 * Logika login/logout dan pengecekan hak akses pengguna yang sedang aktif.
 */
public class AuthService {

    private static final int BATAS_PERCOBAAN = 3;

    private final Dao<Pengguna> penggunaDao;
    private Pengguna penggunaAktif;
    private int percobaanGagal;

    public AuthService(Dao<Pengguna> penggunaDao) {
        this.penggunaDao = penggunaDao;
    }

    public Pengguna login(String username, char[] password) throws AutentikasiException {
        if (percobaanGagal >= BATAS_PERCOBAAN) {
            throw new AutentikasiException("Terlalu banyak percobaan gagal. Tutup lalu buka kembali aplikasi.");
        }
        if (username == null || username.trim().isEmpty() || password == null || password.length == 0) {
            throw new AutentikasiException("Username dan password wajib diisi.");
        }
        Pengguna p = penggunaDao.findById(username.trim().toLowerCase()).orElse(null);
        if (p == null || !p.cekPassword(new String(password))) {
            percobaanGagal++;
            int sisa = BATAS_PERCOBAAN - percobaanGagal;
            throw new AutentikasiException("Username atau password salah. Sisa percobaan: " + sisa + ".");
        }
        percobaanGagal = 0;
        penggunaAktif = p;
        return p;
    }

    public void logout() {
        penggunaAktif = null;
    }

    public Pengguna getPenggunaAktif() {
        return penggunaAktif;
    }

    /** Dipanggil sebelum operasi sensitif (hapus). */
    public void wajibAdmin(String aksi) throws AutentikasiException {
        if (penggunaAktif == null || !penggunaAktif.isAdmin()) {
            throw new AutentikasiException("Hanya Admin yang boleh " + aksi + ".");
        }
    }
}
