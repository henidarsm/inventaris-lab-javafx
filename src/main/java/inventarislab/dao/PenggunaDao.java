package inventarislab.dao;

import java.nio.file.Path;

import inventarislab.exception.DataAksesException;
import inventarislab.exception.ValidasiException;
import inventarislab.model.Pengguna;
import inventarislab.model.Peran;

public class PenggunaDao extends AbstractFileDao<Pengguna> {

    public PenggunaDao(Path file) throws DataAksesException {
        super(file);
    }

    @Override
    protected String getHeader() {
        return "username;password_sha256;nama_lengkap;peran";
    }

    @Override
    protected String keBaris(Pengguna p) {
        return String.join(PEMISAH, p.getUsername(), p.getPasswordHash(), p.getNamaLengkap(), p.getPeran().name());
    }

    @Override
    protected Pengguna dariBaris(String[] k) throws ValidasiException {
        return new Pengguna(k[0], k[1], k[2], Peran.valueOf(k[3]));
    }
}
