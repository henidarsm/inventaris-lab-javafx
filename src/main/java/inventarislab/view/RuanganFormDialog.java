package inventarislab.view;

import inventarislab.exception.ValidasiException;
import inventarislab.model.Ruangan;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * FRAME 7 - Form tambah/ubah ruangan (Stage modal).
 */
public class RuanganFormDialog extends Stage {

    private final TextField txtKode = new TextField();
    private final TextField txtNama = new TextField();
    private final ComboBox<String> cmbGedung = new ComboBox<>();
    private final Spinner<Integer> spnKapasitas = Ui.spinner(1, 200, 30);
    private final CheckBox chkAc = new CheckBox("Ruangan ber-AC");
    private final TextField txtPenanggungJawab = new TextField();
    private final Button btnSimpan = Ui.tombol("Simpan", "btn-primer");
    private final Button btnBatal = Ui.tombol("Batal");

    private Ruangan hasil;

    public RuanganFormDialog(Window induk, Ruangan lama) {
        setTitle(lama == null ? "Tambah Ruangan" : "Ubah Ruangan " + lama.getKode());
        initOwner(induk);
        initModality(Modality.APPLICATION_MODAL);

        cmbGedung.getItems().addAll("Gedung TK Lt. 1", "Gedung TK Lt. 2", "Gedung TK Lt. 3");
        cmbGedung.setEditable(true);
        cmbGedung.getSelectionModel().selectFirst();
        txtKode.setPromptText("mis. LAB-05");
        txtNama.setPromptText("mis. Lab Robotika");
        txtPenanggungJawab.setPromptText("Nama dosen/laboran");

        GridPane g = Ui.gridForm();
        Ui.baris(g, 0, "Kode ruangan *", txtKode);
        Ui.baris(g, 1, "Nama ruangan *", txtNama);
        Ui.baris(g, 2, "Gedung *", cmbGedung);
        Ui.baris(g, 3, "Kapasitas (orang)", spnKapasitas);
        Ui.baris(g, 4, "Fasilitas", chkAc);
        Ui.baris(g, 5, "Penanggung jawab *", txtPenanggungJawab);
        VBox kartu = new VBox(12, Ui.label("Data Ruangan", "judul-bagian"), g);
        kartu.getStyleClass().add("kartu");

        VBox kepala = new VBox(2, Ui.label(lama == null ? "Tambah Ruangan Baru" : "Ubah Data Ruangan", "judul-bagian"),
                Ui.label(lama == null ? "Kolom bertanda * wajib diisi." : "Kode ruangan tidak dapat diubah.", "teks-redup"));
        kepala.getStyleClass().add("kepala-dialog");
        VBox isi = new VBox(kartu);
        isi.getStyleClass().add("isi-dialog");

        HBox tombol = new HBox(10, Ui.pengisi(), btnBatal, btnSimpan);
        tombol.setAlignment(Pos.CENTER_RIGHT);
        tombol.getStyleClass().add("kaki-dialog");
        btnSimpan.setPrefWidth(110);
        btnBatal.setPrefWidth(90);

        VBox root = new VBox(kepala, isi, tombol);
        root.getStyleClass().add("form-dialog");
        root.setPrefWidth(540);
        Scene scene = new Scene(root);
        Ui.pasangCss(scene);
        setScene(scene);

        if (lama != null) {
            txtKode.setText(lama.getKode());
            txtKode.setDisable(true); // kode = primary key
            txtNama.setText(lama.getNama());
            cmbGedung.setValue(lama.getGedung());
            spnKapasitas.getValueFactory().setValue(lama.getKapasitas());
            chkAc.setSelected(lama.isBerAc());
            txtPenanggungJawab.setText(lama.getPenanggungJawab());
        }

        btnSimpan.setOnAction(e -> simpan());
        btnBatal.setOnAction(e -> close());
        btnSimpan.setDefaultButton(true);
        btnBatal.setCancelButton(true);
        sizeToScene();
    }

    private void simpan() {
        Ui.bersihkanError(txtKode, txtNama, cmbGedung, spnKapasitas, txtPenanggungJawab);
        try {
            hasil = new Ruangan(txtKode.getText(), txtNama.getText(), cmbGedung.getEditor().getText(),
                    Ui.nilaiSpinner(spnKapasitas, "Kapasitas"), chkAc.isSelected(), txtPenanggungJawab.getText());
            close();
        } catch (ValidasiException ex) {
            Control salah = null;
            if ("Kode ruangan".equals(ex.getNamaField())) {
                salah = txtKode;
            } else if ("Nama ruangan".equals(ex.getNamaField())) {
                salah = txtNama;
            } else if ("Gedung".equals(ex.getNamaField())) {
                salah = cmbGedung;
            } else if ("Kapasitas".equals(ex.getNamaField())) {
                salah = spnKapasitas;
            } else if ("Penanggung jawab".equals(ex.getNamaField())) {
                salah = txtPenanggungJawab;
            }
            Ui.tandaiError(salah);
            Ui.error(this, ex.getMessage());
            if (salah != null) {
                salah.requestFocus();
            }
        }
    }

    public Ruangan getHasil() {
        return hasil;
    }
}
