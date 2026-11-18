package inventarislab.dao;

import java.nio.file.Path;
import java.time.LocalDate;

import inventarislab.exception.DataAksesException;
import inventarislab.exception.ValidasiException;
import inventarislab.model.Aset;
import inventarislab.model.Furnitur;
import inventarislab.model.JenisAset;
import inventarislab.model.Komputer;
import inventarislab.model.Kondisi;
import inventarislab.model.PerangkatEmbedded;
import inventarislab.model.PerangkatJaringan;

/**
 * Menyimpan semua jenis aset di satu file (aset.csv).
 * Kolom 0 = jenis; kolom 8 dst. berisi atribut khusus tiap kelas turunan.
 */
public class AsetDao extends AbstractFileDao<Aset> {

    public AsetDao(Path file) throws DataAksesException {
        super(file);
    }

    @Override
    protected String getHeader() {
        return "jenis;id;nama;merk;tanggal_perolehan;harga;kondisi;ruangan;atribut1;atribut2;atribut3";
    }

    @Override
    protected String keBaris(Aset a) {
        String umum = String.join(PEMISAH, a.getJenis().name(), a.getId(), a.getNama(), a.getMerk(),
                a.getTanggalPerolehan().toString(), String.valueOf(a.getHargaPerolehan()),
                a.getKondisi().name(), a.getKodeRuangan());
        String khusus;
        if (a instanceof Komputer) {
            Komputer k = (Komputer) a;
            khusus = String.join(PEMISAH, k.getProsesor(), String.valueOf(k.getRamGb()),
                    k.getTanggalPerawatanTerakhir().toString());
        } else if (a instanceof PerangkatJaringan) {
            PerangkatJaringan j = (PerangkatJaringan) a;
            khusus = String.join(PEMISAH, String.valueOf(j.getJumlahPort()),
                    j.getTanggalPerawatanTerakhir().toString(), "");
        } else if (a instanceof Furnitur) {
            khusus = String.join(PEMISAH, ((Furnitur) a).getBahan(), "", "");
        } else {
            PerangkatEmbedded e = (PerangkatEmbedded) a;
            khusus = String.join(PEMISAH, e.getPlatform(), String.valueOf(e.getIntervalPengecekanBulan()),
                    e.getTanggalPengecekanTerakhir().toString());
        }
        return umum + PEMISAH + khusus;
    }

    @Override
    protected Aset dariBaris(String[] k) throws ValidasiException {
        JenisAset jenis = JenisAset.valueOf(k[0]);
        String id = k[1];
        String nama = k[2];
        String merk = k[3];
        LocalDate tgl = LocalDate.parse(k[4]);
        double harga = Double.parseDouble(k[5]);
        Kondisi kondisi = Kondisi.valueOf(k[6]);
        String ruangan = k[7];
        switch (jenis) {
            case KOMPUTER:
                return new Komputer(id, nama, merk, tgl, harga, kondisi, ruangan,
                        k[8], Integer.parseInt(k[9]), LocalDate.parse(k[10]));
            case JARINGAN:
                return new PerangkatJaringan(id, nama, merk, tgl, harga, kondisi, ruangan,
                        Integer.parseInt(k[8]), LocalDate.parse(k[9]));
            case FURNITUR:
                return new Furnitur(id, nama, merk, tgl, harga, kondisi, ruangan, k[8]);
            case EMBEDDED:
                return new PerangkatEmbedded(id, nama, merk, tgl, harga, kondisi, ruangan,
                        k[8], Integer.parseInt(k[9]), LocalDate.parse(k[10]));
            default:
                throw new ValidasiException("Jenis aset tidak dikenal: " + k[0]);
        }
    }
}
