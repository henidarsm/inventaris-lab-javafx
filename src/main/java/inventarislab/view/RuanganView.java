package inventarislab.view;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToLongFunction;

import inventarislab.model.Ruangan;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * FRAME 6 - Data Ruangan: pencarian, tabel ruangan, tombol CRUD.
 */
public class RuanganView extends VBox {

    private final TextField txtCari = new TextField();
    private final Button btnReset = Ui.tombol("Reset filter");
    private final TableView<Ruangan> tabel = new TableView<>();
    private final Button btnTambah = Ui.tombolIkon("Tambah Ruangan", Ikon.TAMBAH, "btn-primer");
    private final Button btnUbah = Ui.tombolIkon("Ubah", Ikon.UBAH);
    private final Button btnHapus = Ui.tombolIkon("Hapus", Ikon.HAPUS, "btn-bahaya");
    private final Label lblJumlah = Ui.label("", "subjudul");

    private ToLongFunction<String> penghitungAset = kode -> 0;

    public RuanganView() {
        super(16);
        setPadding(new Insets(22, 26, 22, 26));

        HBox kepala = new HBox(12, new VBox(2, Ui.label("Data Ruangan", "judul-halaman"), lblJumlah),
                Ui.pengisi(), btnTambah);
        kepala.setAlignment(Pos.CENTER_LEFT);

        txtCari.setPromptText("Cari kode, nama, gedung, atau penanggung jawab");
        HBox kolomCari = new HBox(6, Ikon.buat(Ikon.CARI, 16, "ikon-redup"), txtCari);
        kolomCari.getStyleClass().add("kolom-cari");
        kolomCari.setPrefWidth(380);
        HBox.setHgrow(txtCari, Priority.ALWAYS);
        HBox toolbar = new HBox(12, kolomCari, Ui.pengisi(), btnReset);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TableColumn<Ruangan, String> kKode = Ui.kolom("Kode", Ruangan::getKode);
        TableColumn<Ruangan, String> kNama = Ui.kolom("Nama Ruangan", Ruangan::getNama);
        TableColumn<Ruangan, String> kGedung = Ui.kolom("Gedung", Ruangan::getGedung);
        TableColumn<Ruangan, Integer> kKapasitas = Ui.kolom("Kapasitas", Ruangan::getKapasitas);
        TableColumn<Ruangan, String> kAc = Ui.kolomBadge("AC", r -> r.isBerAc() ? "Ada" : "Tidak ada",
                v -> v.equals("Ada") ? "badge-biru" : "badge-abu");
        TableColumn<Ruangan, String> kPj = Ui.kolom("Penanggung Jawab", Ruangan::getPenanggungJawab);
        TableColumn<Ruangan, Long> kJumlah = Ui.kolom("Jumlah Aset", r -> penghitungAset.applyAsLong(r.getKode()));

        double[] lebar = {90, 200, 150, 90, 90, 230, 105};
        List<TableColumn<Ruangan, ?>> semua = Arrays.asList(kKode, kNama, kGedung, kKapasitas, kAc, kPj, kJumlah);
        for (int i = 0; i < semua.size(); i++) {
            semua.get(i).setPrefWidth(lebar[i]);
        }
        tabel.getColumns().addAll(semua);
        Ui.lebarFleksibel(tabel, kNama, 170);
        tabel.setPlaceholder(new Label("Tidak ada ruangan yang cocok dengan pencarian"));
        VBox.setVgrow(tabel, Priority.ALWAYS);

        HBox aksi = new HBox(8, btnUbah, btnHapus, Ui.pengisi(),
                Ui.label("Klik dua kali pada baris untuk mengubah", "teks-redup"));
        aksi.setAlignment(Pos.CENTER_LEFT);
        btnUbah.disableProperty().bind(tabel.getSelectionModel().selectedItemProperty().isNull());
        btnHapus.disableProperty().bind(tabel.getSelectionModel().selectedItemProperty().isNull());

        VBox kartu = new VBox(14, toolbar, tabel, aksi);
        kartu.getStyleClass().add("kartu");
        VBox.setVgrow(kartu, Priority.ALWAYS);

        getChildren().addAll(kepala, kartu);
    }

    public void setData(List<Ruangan> daftar, ToLongFunction<String> penghitungAset) {
        this.penghitungAset = penghitungAset;
        tabel.setItems(FXCollections.observableArrayList(daftar));
        tabel.refresh();
        lblJumlah.setText(daftar.size() + " ruangan ditampilkan");
    }

    public Ruangan getRuanganTerpilih() {
        return tabel.getSelectionModel().getSelectedItem();
    }

    public String getKataKunci() {
        return txtCari.getText();
    }

    public void resetFilter() {
        txtCari.clear();
    }

    public TextField getTxtCari() {
        return txtCari;
    }

    public Button getBtnReset() {
        return btnReset;
    }

    public Button getBtnTambah() {
        return btnTambah;
    }

    public Button getBtnUbah() {
        return btnUbah;
    }

    public Button getBtnHapus() {
        return btnHapus;
    }

    public TableView<Ruangan> getTabel() {
        return tabel;
    }
}
