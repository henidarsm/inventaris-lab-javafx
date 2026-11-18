package inventarislab.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * FRAME 1 - Halaman Login (Stage).
 * Hanya berisi komponen tampilan; logika login ada di LoginController.
 */
public class LoginView extends Stage {

    private final TextField txtUsername = new TextField();
    private final PasswordField txtPassword = new PasswordField();
    private final TextField txtPasswordTerlihat = new TextField();
    private final CheckBox chkTampilkan = new CheckBox("Tampilkan password");
    private final Button btnMasuk = Ui.tombol("Masuk", "btn-primer");
    private final Button btnKeluar = Ui.tombol("Keluar");
    private final Label lblPesan = Ui.label("", "teks-error");

    public LoginView() {
        setTitle("Login - Sistem Inventaris Laboratorium");

        HBox root = new HBox(buatPanelBrand(), buatPanelForm());
        Scene scene = new Scene(root, 1040, 580);
        Ui.pasangCss(scene);
        setScene(scene);
        setMinWidth(900);
        setMinHeight(540);
    }

    /** Panel kiri: foto laboratorium + gradasi gelap di bawah agar teks terbaca. */
    private StackPane buatPanelBrand() {
        // foto selalu memenuhi panel (seperti "cover") dan tetap di tengah
        Image gambar = new Image(Ui.class.getResourceAsStream("/inventarislab/view/lab.jpg"));
        ImageView foto = new ImageView(gambar);
        foto.setPreserveRatio(true);
        foto.setSmooth(true);
        foto.setManaged(false);
        Region lapisan = new Region();
        lapisan.getStyleClass().add("lapisan-foto");

        ImageView logo = Ui.logoUndip(44);
        StackPane lingkar = new StackPane(logo);
        lingkar.getStyleClass().add("lingkar-logo-kecil");

        Label judul = Ui.label("Sistem Inventaris Laboratorium", "judul-brand");
        judul.setWrapText(true);
        Label sub = Ui.label("Pendataan aset, nilai buku, serta jadwal perawatan perangkat laboratorium.", "sub-brand");
        sub.setWrapText(true);

        Region pengisi = new Region();
        VBox.setVgrow(pengisi, Priority.ALWAYS);
        VBox isi = new VBox(12, pengisi, lingkar, judul, sub,
                Ui.label("Teknik Komputer - Universitas Diponegoro", "kecil"));
        isi.getStyleClass().add("isi-foto");

        StackPane panel = new StackPane(foto, lapisan, isi);
        panel.layoutBoundsProperty().addListener((obs, lama, b) -> {
            double rasio = gambar.getWidth() / gambar.getHeight();
            double lebar = Math.max(b.getWidth(), b.getHeight() * rasio);
            double tinggi = lebar / rasio;
            foto.setFitWidth(lebar);
            // titik fokus di tengah foto
            double x = b.getWidth() / 2 - lebar * 0.5;
            foto.setLayoutX(Math.min(0, Math.max(b.getWidth() - lebar, x)));
            foto.setLayoutY((b.getHeight() - tinggi) / 2);
        });
        Rectangle potong = new Rectangle();
        potong.widthProperty().bind(panel.widthProperty());
        potong.heightProperty().bind(panel.heightProperty());
        panel.setClip(potong);
        panel.setPrefWidth(600);
        panel.setMinWidth(440);
        HBox.setHgrow(panel, Priority.ALWAYS);
        return panel;
    }

    private VBox buatPanelForm() {
        txtUsername.setPromptText("Masukkan username");
        txtPassword.setPromptText("Masukkan password");
        txtPasswordTerlihat.setPromptText("Masukkan password");

        // password terlihat/tersembunyi: dua field berbagi teks yang sama
        txtPasswordTerlihat.textProperty().bindBidirectional(txtPassword.textProperty());
        txtPasswordTerlihat.visibleProperty().bind(chkTampilkan.selectedProperty());
        txtPasswordTerlihat.managedProperty().bind(chkTampilkan.selectedProperty());
        txtPassword.visibleProperty().bind(chkTampilkan.selectedProperty().not());
        txtPassword.managedProperty().bind(chkTampilkan.selectedProperty().not());
        StackPane kolomPassword = new StackPane(txtPassword, txtPasswordTerlihat);

        btnMasuk.setDefaultButton(true); // Enter = Masuk
        btnMasuk.setMaxWidth(Double.MAX_VALUE);
        btnMasuk.setPrefHeight(38);
        btnKeluar.setMaxWidth(Double.MAX_VALUE);
        btnKeluar.setPrefHeight(38);
        HBox.setHgrow(btnMasuk, Priority.ALWAYS);
        HBox.setHgrow(btnKeluar, Priority.ALWAYS);
        HBox tombol = new HBox(10, btnKeluar, btnMasuk);
        lblPesan.setWrapText(true);

        VBox form = new VBox(6,
                Ui.label("Masuk ke akun Anda", "judul-halaman"),
                Ui.label("Gunakan akun Admin atau Laboran.", "subjudul"),
                spasi(18),
                Ui.label("Username", "label-form"), txtUsername,
                spasi(8),
                Ui.label("Password", "label-form"), kolomPassword,
                spasi(2), chkTampilkan,
                lblPesan,
                spasi(10),
                tombol);
        form.getStyleClass().add("login-form");
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPrefWidth(440);
        form.setMinWidth(400);
        return form;
    }

    private static Region spasi(double tinggi) {
        Region r = new Region();
        r.setMinHeight(tinggi);
        return r;
    }

    // ---------------------------------------------------- akses untuk controller

    public Button getBtnMasuk() {
        return btnMasuk;
    }

    public Button getBtnKeluar() {
        return btnKeluar;
    }

    public TextField getTxtUsername() {
        return txtUsername;
    }

    public PasswordField getTxtPassword() {
        return txtPassword;
    }

    public String getUsername() {
        return txtUsername.getText();
    }

    public char[] getPassword() {
        return txtPassword.getText().toCharArray();
    }

    public void tampilkanPesan(String pesan) {
        lblPesan.setText(pesan);
    }

    public void kosongkanPassword() {
        txtPassword.clear();
        txtPassword.requestFocus();
    }

    public void kunciForm() {
        txtUsername.setDisable(true);
        txtPassword.setDisable(true);
        txtPasswordTerlihat.setDisable(true);
        btnMasuk.setDisable(true);
    }
}
