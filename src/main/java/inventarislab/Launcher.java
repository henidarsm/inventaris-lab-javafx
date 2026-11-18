package inventarislab;

/**
 * JALANKAN KELAS INI dari Eclipse: klik kanan > Run As > Java Application.
 *
 * Kelas ini sengaja tidak meng-extend javafx.application.Application. Dengan begitu
 * aplikasi JavaFX bisa dijalankan langsung dari classpath Maven tanpa pengaturan
 * --module-path (menghindari error "JavaFX runtime components are missing").
 */
public class Launcher {

    public static void main(String[] args) {
        Main.main(args);
    }
}
