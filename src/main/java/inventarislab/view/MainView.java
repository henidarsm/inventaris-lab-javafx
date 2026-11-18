package inventarislab.view;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FRAME 2 - Jendela utama: MenuBar, sidebar navigasi, area konten, status bar.
 * Area konten (StackPane) berganti antara DashboardView, AsetView, dan RuanganView.
 */
public class MainView extends Stage {

    public static final String KARTU_DASHBOARD = "dashboard";
    public static final String KARTU_ASET = "aset";
    public static final String KARTU_RUANGAN = "ruangan";

    private final StackPane konten = new StackPane();
    private final Map<String, Node> halaman = new LinkedHashMap<>();
    private final Map<String, ToggleButton> tombolNavigasi = new LinkedHashMap<>();
    private final ToggleGroup grupNavigasi = new ToggleGroup();

    private final MenuItem mnLogout = new MenuItem("Logout");
    private final MenuItem mnKeluar = new MenuItem("Keluar");
    private final MenuItem mnDashboard = new MenuItem("Dashboard");
    private final MenuItem mnAset = new MenuItem("Data Aset");
    private final MenuItem mnRuangan = new MenuItem("Data Ruangan");
    private final MenuItem mnTentang = new MenuItem("Tentang Aplikasi");

    private final Label lblInisial = new Label();
    private final Label lblNamaPengguna = Ui.label("", "nama-pengguna");
    private final Label lblPeran = Ui.label("", "teks-redup");
    private final Button btnLogout = new Button();
    private final Label lblStatus = new Label();
    private final Label lblFolderData = new Label();

    private final DashboardView dashboardView = new DashboardView();
    private final AsetView asetView = new AsetView();
    private final RuanganView ruanganView = new RuanganView();

    public MainView() {
        setTitle("Sistem Inventaris Laboratorium");
        ScrollPane gulirDashboard = new ScrollPane(dashboardView);
        gulirDashboard.setFitToWidth(true);
        gulirDashboard.setFitToHeight(true);
        halaman.put(KARTU_DASHBOARD, gulirDashboard);
        halaman.put(KARTU_ASET, asetView);
        halaman.put(KARTU_RUANGAN, ruanganView);

        BorderPane isi = new BorderPane();
        isi.setLeft(buatSidebar());
        isi.setCenter(konten);
        isi.setBottom(buatStatusBar());

        BorderPane root = new BorderPane(isi);
        root.setTop(buatMenuBar());

        Scene scene = new Scene(root, 1240, 700);
        Ui.pasangCss(scene);
        setScene(scene);
        setMinWidth(1040);
        setMinHeight(560);
    }

    private MenuBar buatMenuBar() {
        mnLogout.setAccelerator(KeyCombination.keyCombination("Shortcut+L"));
        mnKeluar.setAccelerator(KeyCombination.keyCombination("Shortcut+Q"));
        mnDashboard.setAccelerator(KeyCombination.keyCombination("Shortcut+1"));
        mnAset.setAccelerator(KeyCombination.keyCombination("Shortcut+2"));
        mnRuangan.setAccelerator(KeyCombination.keyCombination("Shortcut+3"));

        Menu mAplikasi = new Menu("_Aplikasi", null, mnLogout, new SeparatorMenuItem(), mnKeluar);
        Menu mData = new Menu("_Data", null, mnDashboard, mnAset, mnRuangan);
        Menu mBantuan = new Menu("_Bantuan", null, mnTentang);
        return new MenuBar(mAplikasi, mData, mBantuan);
    }

    private VBox buatSidebar() {
        ImageView logo = Ui.logoUndip(38);
        VBox teksLogo = new VBox(1, Ui.label("Inventaris Lab", "sidebar-judul"),
                Ui.label("Teknik Komputer", "sidebar-sub"));
        HBox kepala = new HBox(10, logo, teksLogo);
        kepala.setAlignment(Pos.CENTER_LEFT);
        kepala.setPadding(new Insets(0, 6, 6, 6));

        VBox sidebar = new VBox(kepala);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(232);
        sidebar.getChildren().add(Ui.label("MENU", "sidebar-seksi"));
        sidebar.getChildren().add(tombolNav(KARTU_DASHBOARD, "Dashboard", Ikon.DASHBOARD));
        sidebar.getChildren().add(Ui.label("DATA MASTER", "sidebar-seksi"));
        sidebar.getChildren().add(tombolNav(KARTU_ASET, "Data Aset", Ikon.INVENTARIS));
        sidebar.getChildren().add(tombolNav(KARTU_RUANGAN, "Data Ruangan", Ikon.GEDUNG));

        Region pengisi = new Region();
        VBox.setVgrow(pengisi, Priority.ALWAYS);
        sidebar.getChildren().addAll(pengisi, buatKartuPengguna());

        // satu tombol navigasi selalu terpilih
        grupNavigasi.selectedToggleProperty().addListener((obs, lama, baru) -> {
            if (baru == null && lama != null) {
                lama.setSelected(true);
            }
        });
        return sidebar;
    }

    private HBox buatKartuPengguna() {
        StackPane avatar = new StackPane(lblInisial);
        avatar.getStyleClass().add("avatar");
        lblNamaPengguna.setWrapText(true);
        VBox teks = new VBox(1, lblNamaPengguna, lblPeran);
        HBox.setHgrow(teks, Priority.ALWAYS);
        btnLogout.setGraphic(Ikon.buat(Ikon.KELUAR, 16));
        btnLogout.getStyleClass().add("btn-ikon");
        btnLogout.setTooltip(new Tooltip("Logout"));
        HBox kartu = new HBox(10, avatar, teks, btnLogout);
        kartu.setAlignment(Pos.CENTER_LEFT);
        kartu.getStyleClass().add("kartu-pengguna");
        return kartu;
    }

    private ToggleButton tombolNav(String kartu, String teks, String ikon) {
        ToggleButton b = new ToggleButton(teks, Ikon.buat(ikon, 18));
        b.getStyleClass().add("nav");
        b.setToggleGroup(grupNavigasi);
        b.setMaxWidth(Double.MAX_VALUE);
        tombolNavigasi.put(kartu, b);
        return b;
    }

    private HBox buatStatusBar() {
        HBox status = new HBox(lblStatus, Ui.pengisi(), lblFolderData);
        status.getStyleClass().add("status-bar");
        return status;
    }

    /** Ganti konten tengah dan tandai tombol navigasi yang aktif. */
    public void tampilkanKartu(String kartu) {
        konten.getChildren().setAll(halaman.get(kartu));
        tombolNavigasi.get(kartu).setSelected(true);
    }

    /** Menampilkan pengguna yang sedang login di sidebar dan status bar. */
    public void setPengguna(String nama, String peran) {
        lblNamaPengguna.setText(nama);
        lblPeran.setText(peran);
        String[] kata = nama.trim().split("\\s+");
        String inisial = kata[0].substring(0, 1) + (kata.length > 1 ? kata[1].substring(0, 1) : "");
        lblInisial.setText(inisial.toUpperCase());
        lblStatus.setText("Login sebagai " + nama + " (" + peran + ")");
    }

    public void setInfoFolderData(String teks) {
        lblFolderData.setText(teks);
    }

    // ---------------------------------------------------- akses untuk controller

    public ToggleButton getTombolNavigasi(String kartu) {
        return tombolNavigasi.get(kartu);
    }

    public Button getBtnLogout() {
        return btnLogout;
    }

    public MenuItem getMnLogout() {
        return mnLogout;
    }

    public MenuItem getMnKeluar() {
        return mnKeluar;
    }

    public MenuItem getMnDashboard() {
        return mnDashboard;
    }

    public MenuItem getMnAset() {
        return mnAset;
    }

    public MenuItem getMnRuangan() {
        return mnRuangan;
    }

    public MenuItem getMnTentang() {
        return mnTentang;
    }

    public DashboardView getDashboardView() {
        return dashboardView;
    }

    public AsetView getAsetView() {
        return asetView;
    }

    public RuanganView getRuanganView() {
        return ruanganView;
    }
}
