package inventarislab.model;

import inventarislab.exception.ValidasiException;
import inventarislab.util.Validasi;

/**
 * Ruangan laboratorium tempat aset berada (entitas CRUD kedua).
 */
public class Ruangan implements Identifiable {

    private String kode;
    private String nama;
    private String gedung;
    private int kapasitas;
    private boolean berAc;
    private String penanggungJawab;

    public Ruangan(String kode, String nama, String gedung, int kapasitas, boolean berAc,
                   String penanggungJawab) throws ValidasiException {
        setKode(kode);
        setNama(nama);
        setGedung(gedung);
        setKapasitas(kapasitas);
        setBerAc(berAc);
        setPenanggungJawab(penanggungJawab);
    }

    @Override
    public String getId() {
        return kode;
    }

    public String getKode() {
        return kode;
    }

    /** Format kode: 2-4 huruf, tanda '-', 2-3 angka. Contoh: LAB-01. */
    public final void setKode(String kode) throws ValidasiException {
        String bersih = Validasi.teksWajib(kode, "Kode ruangan", 10).toUpperCase();
        if (!bersih.matches("[A-Z]{2,4}-\\d{2,3}")) {
            throw new ValidasiException("Kode ruangan", "Format kode ruangan salah. Contoh yang benar: LAB-01");
        }
        this.kode = bersih;
    }

    public String getNama() {
        return nama;
    }

    public final void setNama(String nama) throws ValidasiException {
        this.nama = Validasi.teksWajib(nama, "Nama ruangan", 50);
    }

    public String getGedung() {
        return gedung;
    }

    public final void setGedung(String gedung) throws ValidasiException {
        this.gedung = Validasi.teksWajib(gedung, "Gedung", 30);
    }

    public int getKapasitas() {
        return kapasitas;
    }

    public final void setKapasitas(int kapasitas) throws ValidasiException {
        this.kapasitas = Validasi.bilanganDalamRentang(kapasitas, 1, 200, "Kapasitas");
    }

    public boolean isBerAc() {
        return berAc;
    }

    public final void setBerAc(boolean berAc) {
        this.berAc = berAc;
    }

    public String getPenanggungJawab() {
        return penanggungJawab;
    }

    public final void setPenanggungJawab(String penanggungJawab) throws ValidasiException {
        this.penanggungJawab = Validasi.teksWajib(penanggungJawab, "Penanggung jawab", 50);
    }

    /** Ditampilkan di JComboBox pilihan ruangan. */
    @Override
    public String toString() {
        return kode + " - " + nama;
    }
}
