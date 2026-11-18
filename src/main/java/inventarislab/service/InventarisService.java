package inventarislab.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import inventarislab.dao.Dao;
import inventarislab.exception.DataAksesException;
import inventarislab.exception.ValidasiException;
import inventarislab.model.Aset;
import inventarislab.model.JenisAset;
import inventarislab.model.Perawatan;
import inventarislab.model.Ruangan;

/**
 * Logika bisnis aset: CRUD dengan aturan, pencarian, dan statistik dashboard.
 * Bekerja terhadap tipe Aset (kelas induk) sehingga semua perhitungan bersifat polimorfik.
 */
public class InventarisService {

    private final Dao<Aset> asetDao;
    private final Dao<Ruangan> ruanganDao;

    public InventarisService(Dao<Aset> asetDao, Dao<Ruangan> ruanganDao) {
        this.asetDao = asetDao;
        this.ruanganDao = ruanganDao;
    }

    // ------------------------------------------------------------------ CRUD

    public List<Aset> semuaAset() {
        return asetDao.findAll();
    }

    public void tambahAset(Aset aset) throws ValidasiException, DataAksesException {
        if (asetDao.findById(aset.getId()).isPresent()) {
            throw new ValidasiException("ID aset", "ID " + aset.getId() + " sudah dipakai aset lain.");
        }
        pastikanRuanganAda(aset.getKodeRuangan());
        asetDao.insert(aset);
    }

    public void ubahAset(Aset aset) throws ValidasiException, DataAksesException {
        pastikanRuanganAda(aset.getKodeRuangan());
        asetDao.update(aset);
    }

    public void hapusAset(String id) throws DataAksesException {
        asetDao.delete(id);
    }

    private void pastikanRuanganAda(String kode) throws ValidasiException {
        if (!ruanganDao.findById(kode).isPresent()) {
            throw new ValidasiException("Ruangan", "Ruangan " + kode + " tidak terdaftar.");
        }
    }

    /** ID otomatis: prefix jenis + nomor urut, mis. KOM-007. */
    public String buatIdBaru(JenisAset jenis) {
        int maks = 0;
        for (Aset a : asetDao.findAll()) {
            if (a.getJenis() == jenis) {
                try {
                    maks = Math.max(maks, Integer.parseInt(a.getId().substring(4)));
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    // ID tidak mengikuti pola standar, abaikan
                }
            }
        }
        return String.format("%s-%03d", jenis.getPrefixId(), maks + 1);
    }

    // ------------------------------------------------------------- pencarian

    /** Overloading #1: cari berdasarkan kata kunci saja (semua jenis). */
    public List<Aset> cari(String kataKunci) {
        return cari(kataKunci, null, false);
    }

    /** Overloading #2: kata kunci + filter jenis (null = semua) + hanya yang perlu perawatan. */
    public List<Aset> cari(String kataKunci, JenisAset jenis, boolean hanyaPerluPerawatan) {
        String kunci = kataKunci == null ? "" : kataKunci.trim().toLowerCase();
        LocalDate hariIni = LocalDate.now();
        return asetDao.findAll().stream()
                .filter(a -> jenis == null || a.getJenis() == jenis)
                .filter(a -> kunci.isEmpty()
                        || a.getId().toLowerCase().contains(kunci)
                        || a.getNama().toLowerCase().contains(kunci)
                        || a.getMerk().toLowerCase().contains(kunci)
                        || a.getKodeRuangan().toLowerCase().contains(kunci))
                .filter(a -> !hanyaPerluPerawatan
                        || (a instanceof Perawatan && ((Perawatan) a).perluPerawatan(hariIni)))
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------- statistik

    public Map<JenisAset, Integer> jumlahPerJenis() {
        Map<JenisAset, Integer> hasil = new EnumMap<>(JenisAset.class);
        for (JenisAset j : JenisAset.values()) {
            hasil.put(j, 0);
        }
        for (Aset a : asetDao.findAll()) {
            hasil.merge(a.getJenis(), 1, Integer::sum);
        }
        return hasil;
    }

    public Map<JenisAset, Double> nilaiBukuPerJenis() {
        Map<JenisAset, Double> hasil = new EnumMap<>(JenisAset.class);
        for (JenisAset j : JenisAset.values()) {
            hasil.put(j, 0.0);
        }
        for (Aset a : asetDao.findAll()) {
            hasil.merge(a.getJenis(), a.hitungNilaiBuku(), Double::sum); // polimorfik
        }
        return hasil;
    }

    public double totalNilaiPerolehan() {
        return asetDao.findAll().stream().mapToDouble(Aset::getHargaPerolehan).sum();
    }

    public double totalNilaiBuku() {
        double total = 0;
        for (Aset a : asetDao.findAll()) {
            total += a.hitungNilaiBuku(); // memanggil aturan penyusutan milik kelas turunan
        }
        return total;
    }

    /** Aset yang jadwal perawatannya sudah lewat, urut dari yang paling lama terlambat. */
    public List<Perawatan> asetPerluPerawatan() {
        LocalDate hariIni = LocalDate.now();
        List<Perawatan> hasil = new ArrayList<>();
        for (Aset a : asetDao.findAll()) {
            if (a instanceof Perawatan && ((Perawatan) a).perluPerawatan(hariIni)) {
                hasil.add((Perawatan) a);
            }
        }
        hasil.sort(Comparator.comparing(Perawatan::getJadwalPerawatanBerikutnya));
        return hasil;
    }

    public long jumlahAsetDiRuangan(String kodeRuangan) {
        return asetDao.findAll().stream().filter(a -> a.getKodeRuangan().equals(kodeRuangan)).count();
    }
}
