package inventarislab.view;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import inventarislab.exception.ValidasiException;
import inventarislab.model.Aset;
import inventarislab.model.Furnitur;
import inventarislab.model.JenisAset;
import inventarislab.model.Komputer;
import inventarislab.model.Kondisi;
import inventarislab.model.PerangkatEmbedded;
import inventarislab.model.PerangkatJaringan;
import inventarislab.model.Ruangan;
import inventarislab.util.Format;
import inventarislab.util.Validasi;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * FRAME 5 - Form tambah/ubah aset (Stage modal).
 * Bagian "Data Khusus" berganti sesuai jenis aset (StackPane, hanya satu yang terlihat),
 * mencerminkan atribut tambahan masing-masing kelas turunan Aset.
 */
public class AsetFormDialog extends Stage {

    // ---- data umum
    private final ComboBox<JenisAset> cmbJenis = new ComboBox<>();
    private final TextField txtId = new TextField();
    private final TextField txtNama = new TextField();
    private final TextField txtMerk = new TextField();
    private final ComboBox<Ruangan> cmbRuangan = new ComboBox<>();
    private final DatePicker dpTanggalPerolehan = Ui.datePicker(LocalDate.now());
    private final TextField txtHarga = new TextField();
    private final ToggleGroup grupKondisi = new ToggleGroup();
    private final RadioButton rbBaik = new RadioButton("Baik");
    private final RadioButton rbRusakRingan = new RadioButton("Rusak Ringan");
    private final RadioButton rbRusakBerat = new RadioButton("Rusak Berat");

    // ---- data khusus
    private final Map<JenisAset, GridPane> panelKhusus = new EnumMap<>(JenisAset.class);
    private final TextField txtProsesor = new TextField();
    private final Spinner<Integer> spnRam = Ui.spinner(1, 256, 8);
    private final DatePicker dpServis = Ui.datePicker(LocalDate.now());
    private final Spinner<Integer> spnPort = Ui.spinner(1, 96, 24);
    private final DatePicker dpCekJaringan = Ui.datePicker(LocalDate.now());
    private final ComboBox<String> cmbBahan = new ComboBox<>();
    private final TextField txtPlatform = new TextField();
    private final Spinner<Integer> spnIntervalCek = Ui.spinner(1, 24, 6);
    private final DatePicker dpCekEmbedded = Ui.datePicker(LocalDate.now());

    private final Label lblEstimasi = Ui.label("Klik \"Hitung Nilai Buku\" untuk melihat nilai aset hari ini.", "estimasi");
    private final Button btnHitung = Ui.tombolIkon("Hitung Nilai Buku", Ikon.TREN_TURUN);
    private final Button btnSimpan = Ui.tombol("Simpan", "btn-primer");
    private final Button btnBatal = Ui.tombol("Batal");

    private final boolean modeUbah;
    private final Function<JenisAset, String> pembuatId;
    private Aset hasil;

    /**
     * @param asetLama  null untuk mode tambah, isi untuk mode ubah
     * @param pembuatId fungsi pembuat ID otomatis per jenis (dari service)
     */
    public AsetFormDialog(Window induk, List<Ruangan> daftarRuangan, Aset asetLama,
                          Function<JenisAset, String> pembuatId) {
        this.modeUbah = asetLama != null;
        this.pembuatId = pembuatId;
        setTitle(modeUbah ? "Ubah Aset " + asetLama.getId() : "Tambah Aset");
        initOwner(induk);
        initModality(Modality.APPLICATION_MODAL);

        cmbJenis.getItems().addAll(JenisAset.values());
        cmbJenis.getSelectionModel().selectFirst();
        cmbRuangan.getItems().addAll(daftarRuangan);
        if (!daftarRuangan.isEmpty()) {
            cmbRuangan.getSelectionModel().selectFirst();
        }
        cmbBahan.getItems().addAll("Kayu", "Besi", "Besi & HPL", "Plastik & Besi");
        cmbBahan.setEditable(true);
        cmbBahan.getSelectionModel().selectFirst();
        txtId.setDisable(true);
        rbBaik.setToggleGroup(grupKondisi);
        rbRusakRingan.setToggleGroup(grupKondisi);
        rbRusakBerat.setToggleGroup(grupKondisi);
        rbBaik.setSelected(true);
        txtNama.setPromptText("mis. Raspberry Pi 4 Model B 8GB");
        txtHarga.setPromptText("mis. 1850000 atau 1.850.000");
        txtProsesor.setPromptText("mis. Intel Core i5-12400");
        txtPlatform.setPromptText("mis. ARM Cortex-M4 180 MHz");
        lblEstimasi.setWrapText(true);
        lblEstimasi.setMaxWidth(Double.MAX_VALUE);
        lblEstimasi.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

        Label judul = Ui.label(modeUbah ? "Ubah Data Aset" : "Tambah Aset Baru", "judul-bagian");
        Label sub = Ui.label(modeUbah ? "Perbarui data aset " + asetLama.getId()
                : "Lengkapi data aset. Kolom bertanda * wajib diisi.", "teks-redup");
        VBox kepala = new VBox(2, judul, sub);
        kepala.getStyleClass().add("kepala-dialog");

        VBox isi = new VBox(14, buatBagianUmum(), buatBagianKhusus());
        isi.getStyleClass().add("isi-dialog");

        VBox root = new VBox(kepala, isi, buatTombol());
        root.getStyleClass().add("form-dialog");
        root.setPrefWidth(720);
        Scene scene = new Scene(root);
        Ui.pasangCss(scene);
        setScene(scene);

        // event tampilan
        cmbJenis.setOnAction(e -> jenisBerubah());
        btnHitung.setOnAction(e -> hitungEstimasi());
        btnSimpan.setOnAction(e -> simpan());
        btnBatal.setOnAction(e -> close());
        btnSimpan.setDefaultButton(true);
        btnBatal.setCancelButton(true);

        if (modeUbah) {
            isiDari(asetLama);
        } else {
            jenisBerubah();
        }
        sizeToScene();
    }

    // ------------------------------------------------------------ tata letak

    private VBox buatBagianUmum() {
        HBox kondisi = new HBox(16, rbBaik, rbRusakRingan, rbRusakBerat);
        kondisi.setAlignment(Pos.CENTER_LEFT);
        GridPane g = Ui.gridForm2();
        Ui.barisGanda(g, 0, "Jenis aset", cmbJenis, "ID (otomatis)", txtId);
        Ui.barisPenuh(g, 1, "Nama aset *", txtNama);
        Ui.barisGanda(g, 2, "Merk", txtMerk, "Ruangan *", cmbRuangan);
        Ui.barisGanda(g, 3, "Tgl perolehan *", dpTanggalPerolehan, "Harga (Rp) *", txtHarga);
        Ui.barisPenuh(g, 4, "Kondisi", kondisi);
        VBox k = new VBox(14, Ui.label("Data Umum", "judul-bagian"), g);
        k.getStyleClass().add("kartu");
        return k;
    }

    private VBox buatBagianKhusus() {
        GridPane kom = Ui.gridForm2();
        Ui.barisPenuh(kom, 0, "Prosesor *", txtProsesor);
        Ui.barisGanda(kom, 1, "RAM (GB)", spnRam, "Servis terakhir", dpServis);

        GridPane jar = Ui.gridForm2();
        Ui.barisGanda(jar, 0, "Jumlah port", spnPort, "Cek firmware", dpCekJaringan);

        GridPane fur = Ui.gridForm2();
        Ui.barisPenuh(fur, 0, "Bahan *", cmbBahan);

        GridPane emb = Ui.gridForm2();
        Ui.barisPenuh(emb, 0, "Platform/prosesor *", txtPlatform);
        Ui.barisGanda(emb, 1, "Interval cek (bulan)", spnIntervalCek, "Cek terakhir", dpCekEmbedded);

        panelKhusus.put(JenisAset.KOMPUTER, kom);
        panelKhusus.put(JenisAset.JARINGAN, jar);
        panelKhusus.put(JenisAset.FURNITUR, fur);
        panelKhusus.put(JenisAset.EMBEDDED, emb);
        StackPane tumpuk = new StackPane(kom, jar, fur, emb);
        tumpuk.setAlignment(Pos.TOP_LEFT);
        tumpuk.setMinHeight(84);

        VBox k = new VBox(12, Ui.label("Data Khusus Jenis Aset", "judul-bagian"), tumpuk, lblEstimasi);
        k.getStyleClass().add("kartu");
        return k;
    }

    private HBox buatTombol() {
        HBox h = new HBox(10, btnHitung, Ui.pengisi(), btnBatal, btnSimpan);
        h.setAlignment(Pos.CENTER_LEFT);
        h.getStyleClass().add("kaki-dialog");
        btnSimpan.setPrefWidth(110);
        btnBatal.setPrefWidth(90);
        return h;
    }

    // --------------------------------------------------------- perilaku form

    private void jenisBerubah() {
        JenisAset jenis = cmbJenis.getValue();
        panelKhusus.forEach((j, panel) -> panel.setVisible(j == jenis));
        if (!modeUbah) {
            txtId.setText(pembuatId.apply(jenis));
        }
    }

    private void isiDari(Aset a) {
        cmbJenis.setValue(a.getJenis());
        cmbJenis.setDisable(true); // jenis (kelas) tidak bisa diganti saat ubah
        panelKhusus.forEach((j, panel) -> panel.setVisible(j == a.getJenis()));
        txtId.setText(a.getId());
        txtNama.setText(a.getNama());
        txtMerk.setText(a.getMerk());
        for (Ruangan r : cmbRuangan.getItems()) {
            if (r.getKode().equals(a.getKodeRuangan())) {
                cmbRuangan.setValue(r);
            }
        }
        dpTanggalPerolehan.setValue(a.getTanggalPerolehan());
        txtHarga.setText(String.valueOf(Math.round(a.getHargaPerolehan())));
        rbBaik.setSelected(a.getKondisi() == Kondisi.BAIK);
        rbRusakRingan.setSelected(a.getKondisi() == Kondisi.RUSAK_RINGAN);
        rbRusakBerat.setSelected(a.getKondisi() == Kondisi.RUSAK_BERAT);

        if (a instanceof Komputer) {
            Komputer k = (Komputer) a;
            txtProsesor.setText(k.getProsesor());
            spnRam.getValueFactory().setValue(k.getRamGb());
            dpServis.setValue(k.getTanggalPerawatanTerakhir());
        } else if (a instanceof PerangkatJaringan) {
            PerangkatJaringan j = (PerangkatJaringan) a;
            spnPort.getValueFactory().setValue(j.getJumlahPort());
            dpCekJaringan.setValue(j.getTanggalPerawatanTerakhir());
        } else if (a instanceof Furnitur) {
            cmbBahan.setValue(((Furnitur) a).getBahan());
        } else if (a instanceof PerangkatEmbedded) {
            PerangkatEmbedded e = (PerangkatEmbedded) a;
            txtPlatform.setText(e.getPlatform());
            spnIntervalCek.getValueFactory().setValue(e.getIntervalPengecekanBulan());
            dpCekEmbedded.setValue(e.getTanggalPengecekanTerakhir());
        }
    }

    /** Membaca isi form menjadi objek kelas turunan yang sesuai. Validasi dilakukan oleh setter model. */
    private Aset bangunAset() throws ValidasiException {
        JenisAset jenis = cmbJenis.getValue();
        String id = txtId.getText();
        String nama = txtNama.getText();
        String merk = txtMerk.getText();
        Ruangan ruangan = Validasi.wajibAda(cmbRuangan.getValue(), "Ruangan");
        LocalDate tgl = Ui.tanggalDari(dpTanggalPerolehan, "Tanggal perolehan");
        double harga = Validasi.parseRupiah(txtHarga.getText(), "Harga perolehan");
        Kondisi kondisi = rbBaik.isSelected() ? Kondisi.BAIK
                : rbRusakRingan.isSelected() ? Kondisi.RUSAK_RINGAN : Kondisi.RUSAK_BERAT;

        switch (jenis) {
            case KOMPUTER:
                return new Komputer(id, nama, merk, tgl, harga, kondisi, ruangan.getKode(), txtProsesor.getText(),
                        Ui.nilaiSpinner(spnRam, "RAM (GB)"), Ui.tanggalDari(dpServis, "Tanggal perawatan terakhir"));
            case JARINGAN:
                return new PerangkatJaringan(id, nama, merk, tgl, harga, kondisi, ruangan.getKode(),
                        Ui.nilaiSpinner(spnPort, "Jumlah port"), Ui.tanggalDari(dpCekJaringan, "Tanggal perawatan terakhir"));
            case FURNITUR:
                return new Furnitur(id, nama, merk, tgl, harga, kondisi, ruangan.getKode(), cmbBahan.getEditor().getText());
            default:
                return new PerangkatEmbedded(id, nama, merk, tgl, harga, kondisi, ruangan.getKode(), txtPlatform.getText(),
                        Ui.nilaiSpinner(spnIntervalCek, "Interval pengecekan (bulan)"),
                        Ui.tanggalDari(dpCekEmbedded, "Tanggal pengecekan terakhir"));
        }
    }

    private void hitungEstimasi() {
        try {
            Aset a = bangunAset();
            lblEstimasi.setText("Nilai buku hari ini: " + Format.rupiah(a.hitungNilaiBuku())
                    + "  (" + a.getMetodePenyusutan() + ")");
        } catch (ValidasiException ex) {
            lblEstimasi.setText("Nilai buku: - (" + ex.getMessage() + ")");
        }
    }

    private void simpan() {
        Ui.bersihkanError(txtNama, txtMerk, txtHarga, txtProsesor, txtPlatform, cmbBahan, cmbRuangan,
                dpTanggalPerolehan, dpServis, dpCekJaringan, dpCekEmbedded, spnRam, spnPort, spnIntervalCek);
        try {
            hasil = bangunAset();
            close();
        } catch (ValidasiException ex) {
            Control salah = fieldUntuk(ex.getNamaField());
            Ui.tandaiError(salah);
            Ui.error(this, ex.getMessage());
            if (salah != null) {
                salah.requestFocus();
            }
        }
    }

    /** Arahkan ke field yang salah agar pengguna langsung bisa memperbaiki. */
    private Control fieldUntuk(String namaField) {
        if (namaField == null) {
            return null;
        }
        switch (namaField) {
            case "Nama aset": return txtNama;
            case "Merk": return txtMerk;
            case "Harga perolehan": return txtHarga;
            case "Tanggal perolehan": return dpTanggalPerolehan;
            case "Ruangan": return cmbRuangan;
            case "Prosesor": return txtProsesor;
            case "Platform": return txtPlatform;
            case "Bahan": return cmbBahan;
            case "RAM (GB)": return spnRam;
            case "Jumlah port": return spnPort;
            case "Interval pengecekan (bulan)": return spnIntervalCek;
            case "Tanggal perawatan terakhir":
                return cmbJenis.getValue() == JenisAset.KOMPUTER ? dpServis : dpCekJaringan;
            case "Tanggal pengecekan terakhir": return dpCekEmbedded;
            default: return null;
        }
    }

    /** Objek hasil form, atau null bila dibatalkan. */
    public Aset getHasil() {
        return hasil;
    }
}
