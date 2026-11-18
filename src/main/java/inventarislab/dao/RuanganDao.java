package inventarislab.dao;

import java.nio.file.Path;

import inventarislab.exception.DataAksesException;
import inventarislab.exception.ValidasiException;
import inventarislab.model.Ruangan;

public class RuanganDao extends AbstractFileDao<Ruangan> {

    public RuanganDao(Path file) throws DataAksesException {
        super(file);
    }

    @Override
    protected String getHeader() {
        return "kode;nama;gedung;kapasitas;ber_ac;penanggung_jawab";
    }

    @Override
    protected String keBaris(Ruangan r) {
        return String.join(PEMISAH, r.getKode(), r.getNama(), r.getGedung(),
                String.valueOf(r.getKapasitas()), String.valueOf(r.isBerAc()), r.getPenanggungJawab());
    }

    @Override
    protected Ruangan dariBaris(String[] k) throws ValidasiException {
        return new Ruangan(k[0], k[1], k[2], Integer.parseInt(k[3]), Boolean.parseBoolean(k[4]), k[5]);
    }
}
