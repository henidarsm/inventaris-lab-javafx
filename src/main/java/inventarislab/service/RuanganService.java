package inventarislab.service;

import java.util.List;
import java.util.stream.Collectors;

import inventarislab.dao.Dao;
import inventarislab.exception.DataAksesException;
import inventarislab.exception.ValidasiException;
import inventarislab.model.Ruangan;

/**
 * Logika bisnis ruangan. Ruangan yang masih berisi aset tidak boleh dihapus.
 */
public class RuanganService {

    private final Dao<Ruangan> ruanganDao;
    private final InventarisService inventarisService;

    public RuanganService(Dao<Ruangan> ruanganDao, InventarisService inventarisService) {
        this.ruanganDao = ruanganDao;
        this.inventarisService = inventarisService;
    }

    public List<Ruangan> semuaRuangan() {
        return ruanganDao.findAll();
    }

    public List<Ruangan> cari(String kataKunci) {
        String kunci = kataKunci == null ? "" : kataKunci.trim().toLowerCase();
        return ruanganDao.findAll().stream()
                .filter(r -> kunci.isEmpty()
                        || r.getKode().toLowerCase().contains(kunci)
                        || r.getNama().toLowerCase().contains(kunci)
                        || r.getGedung().toLowerCase().contains(kunci)
                        || r.getPenanggungJawab().toLowerCase().contains(kunci))
                .collect(Collectors.toList());
    }

    public void tambah(Ruangan r) throws ValidasiException, DataAksesException {
        if (ruanganDao.findById(r.getKode()).isPresent()) {
            throw new ValidasiException("Kode ruangan", "Kode " + r.getKode() + " sudah dipakai.");
        }
        ruanganDao.insert(r);
    }

    public void ubah(Ruangan r) throws DataAksesException {
        ruanganDao.update(r);
    }

    public void hapus(String kode) throws ValidasiException, DataAksesException {
        long isi = inventarisService.jumlahAsetDiRuangan(kode);
        if (isi > 0) {
            throw new ValidasiException("Ruangan " + kode + " masih berisi " + isi
                    + " aset. Pindahkan atau hapus asetnya terlebih dahulu.");
        }
        ruanganDao.delete(kode);
    }

    public long jumlahAset(String kode) {
        return inventarisService.jumlahAsetDiRuangan(kode);
    }
}
