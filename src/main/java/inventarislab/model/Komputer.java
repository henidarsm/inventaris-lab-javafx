package inventarislab.model;

import java.time.LocalDate;

import inventarislab.exception.ValidasiException;
import inventarislab.util.Validasi;

/**
 * Komputer/PC lab. Penyusutan GARIS LURUS, umur ekonomis 4 tahun, tanpa residu.
 * Wajib servis berkala tiap 6 bulan (implementasi interface Perawatan).
 */
public class Komputer extends Aset implements Perawatan {

    private static final int UMUR_EKONOMIS = 4;

    private String prosesor;
    private int ramGb;
    private LocalDate tanggalPerawatanTerakhir;

    public Komputer(String id, String nama, String merk, LocalDate tanggalPerolehan, double hargaPerolehan,
                    Kondisi kondisi, String kodeRuangan, String prosesor, int ramGb,
                    LocalDate tanggalPerawatanTerakhir) throws ValidasiException {
        super(id, nama, merk, tanggalPerolehan, hargaPerolehan, kondisi, kodeRuangan);
        setProsesor(prosesor);
        setRamGb(ramGb);
        setTanggalPerawatanTerakhir(tanggalPerawatanTerakhir);
    }

    @Override
    public JenisAset getJenis() {
        return JenisAset.KOMPUTER;
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
        return (getHargaPerolehan() - getNilaiResidu()) / UMUR_EKONOMIS;
    }

    @Override
    public String getMetodePenyusutan() {
        return "Garis lurus, " + UMUR_EKONOMIS + " tahun";
    }

    @Override
    public String getSpesifikasi() {
        return prosesor + ", RAM " + ramGb + " GB";
    }

    // ---- Perawatan
    @Override
    public String getJenisPerawatan() {
        return "Servis & update OS";
    }

    @Override
    public int getIntervalPerawatanBulan() {
        return 6;
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

    public String getProsesor() {
        return prosesor;
    }

    public final void setProsesor(String prosesor) throws ValidasiException {
        this.prosesor = Validasi.teksWajib(prosesor, "Prosesor", 40);
    }

    public int getRamGb() {
        return ramGb;
    }

    public final void setRamGb(int ramGb) throws ValidasiException {
        this.ramGb = Validasi.bilanganDalamRentang(ramGb, 1, 256, "RAM (GB)");
    }
}
