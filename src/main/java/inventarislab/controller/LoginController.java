package inventarislab.controller;

import inventarislab.AppContext;
import inventarislab.exception.AutentikasiException;
import inventarislab.service.AuthService;
import inventarislab.view.LoginView;
import inventarislab.view.MainView;
import inventarislab.view.Ui;
import javafx.application.Platform;

/**
 * Menghubungkan LoginView dengan AuthService.
 */
public class LoginController {

    private final AppContext ctx;
    private final LoginView view;
    private final AuthService auth;

    public LoginController(AppContext ctx, LoginView view) {
        this.ctx = ctx;
        this.view = view;
        this.auth = ctx.getAuthService();
        view.getBtnMasuk().setOnAction(e -> prosesLogin());
        view.getBtnKeluar().setOnAction(e -> keluar());
    }

    public void tampilkan() {
        view.show();
        view.getTxtUsername().requestFocus();
    }

    private void prosesLogin() {
        try {
            auth.login(view.getUsername(), view.getPassword());
            // tampilkan jendela utama dulu, baru tutup login (agar JavaFX tidak mengira semua jendela ditutup)
            new MainController(ctx, new MainView()).tampilkan();
            view.close();
        } catch (AutentikasiException ex) {
            view.tampilkanPesan(ex.getMessage());
            view.kosongkanPassword();
            if (ex.getMessage().startsWith("Terlalu banyak")) {
                view.kunciForm();
            }
        }
    }

    private void keluar() {
        if (Ui.konfirmasi(view, "Keluar", "Tutup aplikasi?", "Aplikasi akan ditutup.")) {
            Platform.exit();
        }
    }
}
