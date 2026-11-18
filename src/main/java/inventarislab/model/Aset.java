package inventarislab.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import inventarislab.exception.ValidasiException;
import inventarislab.util.Validasi;

/**
 * Kelas induk (abstract) semua aset laboratorium.
 *
 * Abstraksi : method abstrak getJenis(), getUmurEkonomisTahun(),
 *             hitungPenyusutanTahunan(int) dan getSpesifikasi().
 * Pewarisan : diturunkan ke Komputer, PerangkatJaringan, Furnitur, PerangkatEmbedded.
 * Polimorfisme: hitungNilaiBuku() memanggil hitungPenyusutanTahunan() milik
 *             kelas turunan lewat referensi bertipe Aset.
 */
public abstract class Aset implements Identifiable {

    private String id;
    private String nama;
    private String merk;
    private LocalDate tanggalPerolehan;
    private double hargaPerolehan;
    private Kondisi kondisi;
    private String kodeRuangan;

    protected Aset(String id, String nama, String merk, LocalDate tanggalPerolehan,
                   double hargaPerolehan, Kondisi kondisi, String kodeRuangan) throws ValidasiException {
        setId(id);
        setNama(nama);
        setMerk(merk);
        setTanggalPerolehan(tanggalPerolehan);
        setHargaPerolehan(hargaPerolehan);
        setKondisi(kondisi);
        setKodeRuangan(kodeRuangan);
    }

    // ------------------------------------------------------------------ abstrak

    public abstract JenisAset getJenis();

    public abstract int getUmurEkonomisTahun();

    /** Besar penyusutan pada tahun pemakaian ke-n (1-based); aturannya beda per jenis. */
    public abstract double hitungPenyusutanTahunan(int tahunKe);

    /** Ringkasan spesifikasi khusus tiap jenis aset. */
    public abstract String getSpesifikasi();

    /** Nama metode penyusutan yang dipakai, untuk ditampilkan di GUI. */
    public abstract String getMetodePenyusutan();

    // ------------------------------------------------------------ konkret/umum

    /** Persentase nilai residu dari harga perolehan; default 0, bisa di-override. */
    protected double getPersenResidu() {
        return 0.0;
    }

    public double getNilaiResidu() {
        return hargaPerolehan * getPersenResidu();
    }

    /** Akumulasi penyusutan sampai tanggal tertentu (tahun berjalan dihitung proporsional per bulan). */
    public double hitungAkumulasiPenyusutan(LocalDate perTanggal) {
        if (perTanggal.isBefore(tanggalPerolehan)) {
            return 0;
        }
        long bulanPakai = ChronoUnit.MONTHS.between(tanggalPerolehan, perTanggal);
        int tahunPenuh = (int) (bulanPakai / 12);
        int sisaBulan = (int) (bulanPakai % 12);
        double total = 0;
        for (int t = 1; t <= tahunPenuh && t <= getUmurEkonomisTahun(); t++) {
            total += hitungPenyusutanTahunan(t);
        }
        if (tahunPenuh < getUmurEkonomisTahun()) {
            total += hitungPenyusutanTahunan(tahunPenuh + 1) * sisaBulan / 12.0;
        }
        double batas = hargaPerolehan - getNilaiResidu();
        return Math.min(total, batas);
    }

    /** Overloading #1: nilai buku per hari ini. */
    public double hitungNilaiBuku() {
        return hitungNilaiBuku(LocalDate.now());
    }

    /** Overloading #2: nilai buku per tanggal tertentu. */
    public double hitungNilaiBuku(LocalDate perTanggal) {
        return hargaPerolehan - hitungAkumulasiPenyusutan(perTanggal);
    }

    public String tampilkanInfo() {
        return String.format("[%s] %s - %s (%s)", id, getJenis(), nama, getSpesifikasi());
    }

    // ------------------------------------------------------- getter & setter

    @Override
    public String getId() {
        return id;
    }

    public final void setId(String id) throws ValidasiException {
        this.id = Validasi.teksWajib(id, "ID aset", 20).toUpperCase();
    }

    public String getNama() {
        return nama;
    }

    public final void setNama(String nama) throws ValidasiException {
        String bersih = Validasi.teksWajib(nama, "Nama aset", 60);
        if (bersih.length() < 3) {
            throw new ValidasiException("Nama aset", "Nama aset minimal 3 karakter.");
        }
        this.nama = bersih;
    }

    public String getMerk() {
        return merk;
    }

    public final void setMerk(String merk) throws ValidasiException {
        this.merk = Validasi.teksOpsional(merk, "Merk", 40);
    }

    public LocalDate getTanggalPerolehan() {
        return tanggalPerolehan;
    }

    public final void setTanggalPerolehan(LocalDate tanggalPerolehan) throws ValidasiException {
        Validasi.wajibAda(tanggalPerolehan, "Tanggal perolehan");
        if (tanggalPerolehan.isAfter(LocalDate.now())) {
            throw new ValidasiException("Tanggal perolehan", "Tanggal perolehan tidak boleh di masa depan.");
        }
        this.tanggalPerolehan = tanggalPerolehan;
    }

    public double getHargaPerolehan() {
        return hargaPerolehan;
    }

    public final void setHargaPerolehan(double hargaPerolehan) throws ValidasiException {
        Validasi.angkaTidakNegatif(hargaPerolehan, "Harga perolehan");
        if (hargaPerolehan == 0) {
            throw new ValidasiException("Harga perolehan", "Harga perolehan harus lebih dari 0.");
        }
        this.hargaPerolehan = hargaPerolehan;
    }

    public Kondisi getKondisi() {
        return kondisi;
    }

    public final void setKondisi(Kondisi kondisi) throws ValidasiException {
        this.kondisi = Validasi.wajibAda(kondisi, "Kondisi");
    }

    public String getKodeRuangan() {
        return kodeRuangan;
    }

    public final void setKodeRuangan(String kodeRuangan) throws ValidasiException {
        this.kodeRuangan = Validasi.teksWajib(kodeRuangan, "Ruangan", 10).toUpperCase();
    }

    @Override
    public String toString() {
        return tampilkanInfo();
    }
}
