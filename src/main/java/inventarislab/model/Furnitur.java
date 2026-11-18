package inventarislab.model;

import java.time.LocalDate;

import inventarislab.exception.ValidasiException;
import inventarislab.util.Validasi;

/**
 * Meja, kursi, lemari. Penyusutan GARIS LURUS 8 tahun dengan nilai residu 10%.
 * Tidak mengimplementasikan Perawatan (tidak ada jadwal servis berkala).
 */
public class Furnitur extends Aset {

    private static final int UMUR_EKONOMIS = 8;

    private String bahan;

    public Furnitur(String id, String nama, String merk, LocalDate tanggalPerolehan, double hargaPerolehan,
                    Kondisi kondisi, String kodeRuangan, String bahan) throws ValidasiException {
        super(id, nama, merk, tanggalPerolehan, hargaPerolehan, kondisi, kodeRuangan);
        setBahan(bahan);
    }

    @Override
    public JenisAset getJenis() {
        return JenisAset.FURNITUR;
    }

    @Override
    public int getUmurEkonomisTahun() {
        return UMUR_EKONOMIS;
    }

    @Override
    protected double getPersenResidu() {
        return 0.10;
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
        return "Garis lurus, " + UMUR_EKONOMIS + " tahun, residu 10%";
    }

    @Override
    public String getSpesifikasi() {
        return "Bahan " + bahan;
    }

    public String getBahan() {
        return bahan;
    }

    public final void setBahan(String bahan) throws ValidasiException {
        this.bahan = Validasi.teksWajib(bahan, "Bahan", 30);
    }
}
