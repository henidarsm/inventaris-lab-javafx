package inventarislab.model;

import java.time.LocalDate;

import inventarislab.exception.ValidasiException;
import inventarislab.util.Validasi;

/**
 * Board/kit embedded lab: Raspberry Pi, STM32 Nucleo, board FPGA, Arduino, ESP32.
 * Penyusutan JUMLAH ANGKA TAHUN (sum of years' digits) 4 tahun, residu 5%:
 * susut cepat di awal karena generasi board cepat berganti.
 * Wajib uji fungsi & update firmware dengan interval yang bisa diatur (implementasi Perawatan).
 */
public class PerangkatEmbedded extends Aset implements Perawatan {

    private static final int UMUR_EKONOMIS = 4;

    private String platform;
    private int intervalPengecekanBulan;
    private LocalDate tanggalPengecekanTerakhir;

    public PerangkatEmbedded(String id, String nama, String merk, LocalDate tanggalPerolehan,
                             double hargaPerolehan, Kondisi kondisi, String kodeRuangan, String platform,
                             int intervalPengecekanBulan, LocalDate tanggalPengecekanTerakhir) throws ValidasiException {
        super(id, nama, merk, tanggalPerolehan, hargaPerolehan, kondisi, kodeRuangan);
        setPlatform(platform);
        setIntervalPengecekanBulan(intervalPengecekanBulan);
        setTanggalPengecekanTerakhir(tanggalPengecekanTerakhir);
    }

    @Override
    public JenisAset getJenis() {
        return JenisAset.EMBEDDED;
    }

    @Override
    public int getUmurEkonomisTahun() {
        return UMUR_EKONOMIS;
    }

    @Override
    protected double getPersenResidu() {
        return 0.05;
    }

    @Override
    public double hitungPenyusutanTahunan(int tahunKe) {
        if (tahunKe < 1 || tahunKe > UMUR_EKONOMIS) {
            return 0;
        }
        int jumlahAngkaTahun = UMUR_EKONOMIS * (UMUR_EKONOMIS + 1) / 2; // 10
        int sisaUmur = UMUR_EKONOMIS - tahunKe + 1;
        return (getHargaPerolehan() - getNilaiResidu()) * sisaUmur / jumlahAngkaTahun;
    }

    @Override
    public String getMetodePenyusutan() {
        return "Jumlah angka tahun, " + UMUR_EKONOMIS + " tahun, residu 5%";
    }

    @Override
    public String getSpesifikasi() {
        return platform + ", cek tiap " + intervalPengecekanBulan + " bln";
    }

    // ---- Perawatan
    @Override
    public String getJenisPerawatan() {
        return "Uji fungsi & update firmware";
    }

    @Override
    public int getIntervalPerawatanBulan() {
        return intervalPengecekanBulan;
    }

    @Override
    public LocalDate getTanggalPerawatanTerakhir() {
        return tanggalPengecekanTerakhir;
    }

    public String getPlatform() {
        return platform;
    }

    public final void setPlatform(String platform) throws ValidasiException {
        this.platform = Validasi.teksWajib(platform, "Platform", 40);
    }

    public int getIntervalPengecekanBulan() {
        return intervalPengecekanBulan;
    }

    public final void setIntervalPengecekanBulan(int bulan) throws ValidasiException {
        this.intervalPengecekanBulan = Validasi.bilanganDalamRentang(bulan, 1, 24, "Interval pengecekan (bulan)");
    }

    public LocalDate getTanggalPengecekanTerakhir() {
        return tanggalPengecekanTerakhir;
    }

    public final void setTanggalPengecekanTerakhir(LocalDate tanggal) throws ValidasiException {
        Validasi.wajibAda(tanggal, "Tanggal pengecekan terakhir");
        if (tanggal.isBefore(getTanggalPerolehan())) {
            throw new ValidasiException("Tanggal pengecekan terakhir",
                    "Tanggal pengecekan tidak boleh sebelum tanggal perolehan.");
        }
        this.tanggalPengecekanTerakhir = tanggal;
    }
}
