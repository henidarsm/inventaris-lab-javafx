package inventarislab;

import java.nio.file.Paths;

import inventarislab.controller.LoginController;
import inventarislab.view.LoginView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Kelas aplikasi JavaFX. Untuk menjalankan dari Eclipse, gunakan kelas {@link Launcher}.
 * Data disimpan di folder "data" di dalam folder proyek.
 */
public class Main extends Application {

    @Override
    public void start(Stage stageAwal) {
        try {
            AppContext ctx = new AppContext(Paths.get("data"));
            new LoginController(ctx, new LoginView()).tampilkan();
        } catch (Exception ex) {
            Alert a = new Alert(AlertType.ERROR, "Aplikasi gagal dimulai:\n" + ex.getMessage());
            a.setTitle("Kesalahan Fatal");
            a.showAndWait();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
