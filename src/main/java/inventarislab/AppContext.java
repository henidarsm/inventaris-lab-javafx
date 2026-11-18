package inventarislab;

import java.nio.file.Path;

import inventarislab.dao.AsetDao;
import inventarislab.dao.PenggunaDao;
import inventarislab.dao.RuanganDao;
import inventarislab.exception.DataAksesException;
import inventarislab.exception.ValidasiException;
import inventarislab.service.AuthService;
import inventarislab.service.InventarisService;
import inventarislab.service.RuanganService;
import inventarislab.util.DataAwal;

/**
 * Merakit seluruh lapisan (DAO -> Service) satu kali saat aplikasi dimulai.
 * Controller mengambil service dari sini, sehingga view tidak pernah menyentuh DAO langsung.
 */
public class AppContext {

    private final Path folderData;
    private final AuthService authService;
    private final InventarisService inventarisService;
    private final RuanganService ruanganService;

    public AppContext(Path folderData) throws DataAksesException, ValidasiException {
        this.folderData = folderData;
        PenggunaDao penggunaDao = new PenggunaDao(folderData.resolve("pengguna.csv"));
        RuanganDao ruanganDao = new RuanganDao(folderData.resolve("ruangan.csv"));
        AsetDao asetDao = new AsetDao(folderData.resolve("aset.csv"));
        DataAwal.isiJikaKosong(penggunaDao, ruanganDao, asetDao);

        this.authService = new AuthService(penggunaDao);
        this.inventarisService = new InventarisService(asetDao, ruanganDao);
        this.ruanganService = new RuanganService(ruanganDao, inventarisService);
    }

    public Path getFolderData() {
        return folderData;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public InventarisService getInventarisService() {
        return inventarisService;
    }

    public RuanganService getRuanganService() {
        return ruanganService;
    }
}
