package inventarislab.model;

import java.time.LocalDate;

import inventarislab.exception.ValidasiException;
import inventarislab.util.Validasi;

/**
 * Switch/router/access point. Penyusutan SALDO MENURUN GANDA (double declining),
 * umur ekonomis 5 tahun: susut cepat di awal karena teknologinya cepat usang.
 * Pengecekan firmware & konfigurasi tiap 12 bulan.
 */
public class PerangkatJaringan extends Aset implements Perawatan {

    private static final int UMUR_EKONOMIS = 5;

    private int jumlahPort;
    private LocalDate tanggalPerawatanTerakhir;

    public PerangkatJaringan(String id, String nama, String merk, LocalDate tanggalPerolehan,
                             double hargaPerolehan, Kondisi kondisi, String kodeRuangan,
                             int jumlahPort, LocalDate tanggalPerawatanTerakhir) throws ValidasiException {
        super(id, nama, merk, tanggalPerolehan, hargaPerolehan, kondisi, kodeRuangan);
        setJumlahPort(jumlahPort);
        setTanggalPerawatanTerakhir(tanggalPerawatanTerakhir);
    }

    @Override
    public JenisAset getJenis() {
        return JenisAset.JARINGAN;
    }

    @Override
    public int getUmurEkonomisTahun() {
        return UMUR_EKONOMIS;
    }

    @Override
    public double hitungPenyusutanTahunan(int tahunKe) {
        if (tahunKe < 1 || tahunKe > UMUR_EKONOMIS) {
            return 0;
        }
        double tarif = 2.0 / UMUR_EKONOMIS;
        double nilaiAwalTahun = getHargaPerolehan() * Math.pow(1 - tarif, tahunKe - 1);
        if (tahunKe == UMUR_EKONOMIS) {
            // tahun terakhir: habiskan sisa nilai buku sampai nilai residu
            return nilaiAwalTahun - getNilaiResidu();
        }
        return nilaiAwalTahun * tarif;
    }

    @Override
    public String getMetodePenyusutan() {
        return "Saldo menurun ganda, " + UMUR_EKONOMIS + " tahun";
    }

    @Override
    public String getSpesifikasi() {
        return jumlahPort + " port";
    }

    // ---- Perawatan
    @Override
    public String getJenisPerawatan() {
        return "Cek firmware & konfigurasi";
    }

    @Override
    public int getIntervalPerawatanBulan() {
        return 12;
    }

    @Override
    public LocalDate getTanggalPerawatanTerakhir() {
        return tanggalPerawatanTerakhir;
    }

    public final void setTanggalPerawatanTerakhir(LocalDate tanggal) throws ValidasiException {
        Validasi.wajibAda(tanggal, "Tanggal perawatan terakhir");
        if (tanggal.isBefore(getTanggalPerolehan())) {
            throw new ValidasiException("Tanggal perawatan terakhir",
                    "Tanggal perawatan tidak boleh sebelum tanggal perolehan.");
        }
        this.tanggalPerawatanTerakhir = tanggal;
    }

    public int getJumlahPort() {
        return jumlahPort;
    }

    public final void setJumlahPort(int jumlahPort) throws ValidasiException {
        this.jumlahPort = Validasi.bilanganDalamRentang(jumlahPort, 1, 96, "Jumlah port");
    }
}
