package inventarislab.model;

import inventarislab.exception.ValidasiException;
import inventarislab.util.PasswordUtil;
import inventarislab.util.Validasi;

/**
 * Akun yang bisa login ke aplikasi.
 */
public class Pengguna implements Identifiable {

    private String username;
    private String passwordHash;
    private String namaLengkap;
    private Peran peran;

    /** Konstruktor untuk data yang dibaca dari file (password sudah berupa hash). */
    public Pengguna(String username, String passwordHash, String namaLengkap, Peran peran) throws ValidasiException {
        setUsername(username);
        this.passwordHash = Validasi.teksWajib(passwordHash, "Password", 64);
        setNamaLengkap(namaLengkap);
        setPeran(peran);
    }

    /** Factory method: membuat pengguna baru dari password polos (password di-hash dulu). */
    public static Pengguna baru(String username, String passwordPolos, String namaLengkap, Peran peran)
            throws ValidasiException {
        if (passwordPolos == null || passwordPolos.length() < 6) {
            throw new ValidasiException("Password", "Password minimal 6 karakter.");
        }
        return new Pengguna(username, PasswordUtil.hash(passwordPolos), namaLengkap, peran);
    }

    @Override
    public String getId() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public final void setUsername(String username) throws ValidasiException {
        String bersih = Validasi.teksWajib(username, "Username", 20).toLowerCase();
        if (!bersih.matches("[a-z0-9_]{3,20}")) {
            throw new ValidasiException("Username", "Username hanya huruf kecil, angka, dan '_' (3-20 karakter).");
        }
        this.username = bersih;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean cekPassword(String passwordPolos) {
        return PasswordUtil.cocok(passwordPolos, passwordHash);
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public final void setNamaLengkap(String namaLengkap) throws ValidasiException {
        this.namaLengkap = Validasi.teksWajib(namaLengkap, "Nama lengkap", 50);
    }

    public Peran getPeran() {
        return peran;
    }

    public final void setPeran(Peran peran) throws ValidasiException {
        this.peran = Validasi.wajibAda(peran, "Peran");
    }

    public boolean isAdmin() {
        return peran == Peran.ADMIN;
    }
}
