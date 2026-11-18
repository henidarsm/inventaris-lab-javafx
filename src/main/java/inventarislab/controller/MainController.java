package inventarislab.controller;

import inventarislab.AppContext;
import inventarislab.model.Pengguna;
import inventarislab.view.LoginView;
import inventarislab.view.MainView;
import inventarislab.view.Ui;
import javafx.application.Platform;

/**
 * Mengatur navigasi antartampilan (MenuBar + sidebar), logout, keluar,
 * dan membuat controller untuk tiap halaman.
 */
public class MainController {

    private final AppContext ctx;
    private final MainView view;
    private final DashboardController dashboardController;
    private final AsetController asetController;
    private final RuanganController ruanganController;

    public MainController(AppContext ctx, MainView view) {
        this.ctx = ctx;
        this.view = view;

        dashboardController = new DashboardController(ctx, view.getDashboardView());
        // setiap perubahan data aset/ruangan ikut memperbarui dashboard
        asetController = new AsetController(ctx, view, view.getAsetView(), this::dataBerubah);
        ruanganController = new RuanganController(ctx, view, view.getRuanganView(), this::dataBerubah);

        Pengguna p = ctx.getAuthService().getPenggunaAktif();
        view.setPengguna(p.getNamaLengkap(), p.getPeran().toString());
        view.setInfoFolderData("Data tersimpan di folder: " + ctx.getFolderData() + "/ (format CSV)");

        pasangNavigasi();
        view.setOnCloseRequest(e -> {
            e.consume(); // jangan langsung tutup, tanya dulu
            keluar();
        });
    }

    private void pasangNavigasi() {
        view.getTombolNavigasi(MainView.KARTU_DASHBOARD).setOnAction(e -> bukaDashboard());
        view.getTombolNavigasi(MainView.KARTU_ASET).setOnAction(e -> bukaAset());
        view.getTombolNavigasi(MainView.KARTU_RUANGAN).setOnAction(e -> bukaRuangan());
        view.getMnDashboard().setOnAction(e -> bukaDashboard());
        view.getMnAset().setOnAction(e -> bukaAset());
        view.getMnRuangan().setOnAction(e -> bukaRuangan());
        view.getMnLogout().setOnAction(e -> logout());
        view.getBtnLogout().setOnAction(e -> logout());
        view.getMnKeluar().setOnAction(e -> keluar());
        view.getMnTentang().setOnAction(e -> tentang());
    }

    public void tampilkan() {
        bukaDashboard();
        view.show();
    }

    public void bukaDashboard() {
        dashboardController.muatData();
        view.tampilkanKartu(MainView.KARTU_DASHBOARD);
    }

    public void bukaAset() {
        asetController.muatData();
        view.tampilkanKartu(MainView.KARTU_ASET);
    }

    public void bukaRuangan() {
        ruanganController.muatData();
        view.tampilkanKartu(MainView.KARTU_RUANGAN);
    }

    private void dataBerubah() {
        dashboardController.muatData();
    }

    private void logout() {
        if (Ui.konfirmasi(view, "Logout", "Keluar dari akun?", "Anda akan kembali ke halaman login.")) {
            ctx.getAuthService().logout();
            new LoginController(ctx, new LoginView()).tampilkan();
            view.close();
        }
    }

    private void keluar() {
        if (Ui.konfirmasi(view, "Keluar Aplikasi", "Yakin ingin menutup aplikasi?",
                "Semua data sudah tersimpan otomatis.")) {
            Platform.exit();
        }
    }

    private void tentang() {
        Ui.detail(view, "Tentang Aplikasi", "Sistem Inventaris Laboratorium v1.0", java.util.Arrays.asList(
                new String[] {"Mata kuliah", "Pemrograman Berorientasi Objek"},
                new String[] {"Teknologi", "Java + JavaFX"},
                new String[] {"Arsitektur", "MVC + Service + DAO"},
                new String[] {"Penyimpanan", "File CSV di folder data/"}));
    }
}
