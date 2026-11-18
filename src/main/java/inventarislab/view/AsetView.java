package inventarislab.view;

import java.util.Arrays;
import java.util.List;

import inventarislab.model.Aset;
import inventarislab.model.JenisAset;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * FRAME 4 - Data Aset: pencarian + filter, tabel aset, tombol CRUD.
 */
public class AsetView extends VBox {

    private static final String SEMUA = "Semua jenis";

    private final TextField txtCari = new TextField();
    private final ComboBox<Object> cmbFilterJenis = new ComboBox<>();
    private final CheckBox chkPerluPerawatan = new CheckBox("Hanya yang lewat jadwal perawatan");
    private final Button btnReset = Ui.tombol("Reset filter");

    private final TableView<Aset> tabel = new TableView<>();

    private final Button btnTambah = Ui.tombolIkon("Tambah Aset", Ikon.TAMBAH, "btn-primer");
    private final Button btnUbah = Ui.tombolIkon("Ubah", Ikon.UBAH);
    private final Button btnHapus = Ui.tombolIkon("Hapus", Ikon.HAPUS, "btn-bahaya");
    private final Button btnDetail = Ui.tombolIkon("Detail", Ikon.INFO);
    private final Label lblJumlah = Ui.label("", "subjudul");

    public AsetView() {
        super(16);
        setPadding(new Insets(22, 26, 22, 26));

        HBox kepala = new HBox(12, new VBox(2, Ui.label("Data Aset", "judul-halaman"), lblJumlah),
                Ui.pengisi(), btnTambah);
        kepala.setAlignment(Pos.CENTER_LEFT);

        // toolbar: cari (ikon di dalam kolom), filter jenis, checkbox, reset
        txtCari.setPromptText("Cari ID, nama, merk, atau ruangan");
        HBox kolomCari = new HBox(6, Ikon.buat(Ikon.CARI, 16, "ikon-redup"), txtCari);
        kolomCari.getStyleClass().add("kolom-cari");
        kolomCari.setPrefWidth(320);
        HBox.setHgrow(txtCari, Priority.ALWAYS);
        cmbFilterJenis.getItems().add(SEMUA);
        cmbFilterJenis.getItems().addAll(Arrays.asList((Object[]) JenisAset.values()));
        cmbFilterJenis.getSelectionModel().selectFirst();
        cmbFilterJenis.setPrefWidth(190);
        HBox toolbar = new HBox(12, kolomCari, cmbFilterJenis, chkPerluPerawatan, Ui.pengisi(), btnReset);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        buatKolom();
        tabel.setPlaceholder(new Label("Tidak ada aset yang cocok dengan pencarian"));
        VBox.setVgrow(tabel, Priority.ALWAYS);

        HBox aksi = new HBox(8, btnUbah, btnHapus, btnDetail, Ui.pengisi(),
                Ui.label("Klik dua kali pada baris untuk mengubah", "teks-redup"));
        aksi.setAlignment(Pos.CENTER_LEFT);
        // tombol aksi baris aktif hanya jika ada baris yang dipilih
        btnUbah.disableProperty().bind(tabel.getSelectionModel().selectedItemProperty().isNull());
        btnHapus.disableProperty().bind(tabel.getSelectionModel().selectedItemProperty().isNull());
        btnDetail.disableProperty().bind(tabel.getSelectionModel().selectedItemProperty().isNull());

        VBox kartu = new VBox(14, toolbar, tabel, aksi);
        kartu.getStyleClass().add("kartu");
        VBox.setVgrow(kartu, Priority.ALWAYS);

        getChildren().addAll(kepala, kartu);
    }

    private void buatKolom() {
        TableColumn<Aset, String> kId = Ui.kolom("ID", Aset::getId);
        TableColumn<Aset, String> kNama = Ui.kolom("Nama Aset", Aset::getNama);
        TableColumn<Aset, String> kJenis = Ui.kolom("Jenis", a -> a.getJenis().toString());
        TableColumn<Aset, String> kRuang = Ui.kolom("Ruangan", Aset::getKodeRuangan);
        TableColumn<Aset, Double> kHarga = Ui.kolomRupiah("Harga Perolehan", Aset::getHargaPerolehan);
        TableColumn<Aset, Double> kBuku = Ui.kolomRupiah("Nilai Buku", Aset::hitungNilaiBuku); // polimorfik
        TableColumn<Aset, String> kKondisi = Ui.kolomBadge("Kondisi", a -> a.getKondisi().toString(),
                v -> v.equals("Baik") ? "badge-hijau" : v.equals("Rusak Ringan") ? "badge-oranye" : "badge-merah");

        double[] lebar = {100, 220, 172, 78, 128, 118, 118};
        List<TableColumn<Aset, ?>> semua = Arrays.asList(kId, kNama, kJenis, kRuang, kHarga, kBuku, kKondisi);
        for (int i = 0; i < semua.size(); i++) {
            semua.get(i).setPrefWidth(lebar[i]);
        }
        tabel.getColumns().addAll(semua);
        Ui.lebarFleksibel(tabel, kNama, 190); // nama aset mengisi sisa lebar
    }

    public void setData(List<Aset> daftar) {
        tabel.setItems(FXCollections.observableArrayList(daftar));
        lblJumlah.setText(daftar.size() + " aset ditampilkan");
    }

    public Aset getAsetTerpilih() {
        return tabel.getSelectionModel().getSelectedItem();
    }

    public String getKataKunci() {
        return txtCari.getText();
    }

    /** null berarti semua jenis. */
    public JenisAset getFilterJenis() {
        Object pilih = cmbFilterJenis.getValue();
        return pilih instanceof JenisAset ? (JenisAset) pilih : null;
    }

    public boolean isHanyaPerluPerawatan() {
        return chkPerluPerawatan.isSelected();
    }

    public void resetFilter() {
        txtCari.clear();
        cmbFilterJenis.getSelectionModel().selectFirst();
        chkPerluPerawatan.setSelected(false);
    }

    // ---------------------------------------------------- akses untuk controller

    public TextField getTxtCari() {
        return txtCari;
    }

    public ComboBox<Object> getCmbFilterJenis() {
        return cmbFilterJenis;
    }

    public CheckBox getChkPerluPerawatan() {
        return chkPerluPerawatan;
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

    public Button getBtnDetail() {
        return btnDetail;
    }

    public TableView<Aset> getTabel() {
        return tabel;
    }
}
