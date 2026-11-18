package inventarislab.view;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import inventarislab.model.Aset;
import inventarislab.model.JenisAset;
import inventarislab.model.Perawatan;
import inventarislab.util.Format;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FRAME 3 - Dashboard: 4 kartu statistik, komposisi aset (diagram donat),
 * nilai buku per jenis (bar), dan tabel aset yang jadwal perawatannya sudah lewat.
 */
public class DashboardView extends VBox {

    private final Label lblSapaan = Ui.label("", "subjudul");
    private final Label lblTanggal = Ui.label("", "badge", "badge-biru");
    private final Button btnMuatUlang = Ui.tombolIkon("Muat Ulang", Ikon.MUAT_ULANG);

    private final Label lblTotalAset = Ui.label("0", "nilai-stat");
    private final Label lblNilaiPerolehan = Ui.label("Rp 0", "nilai-stat");
    private final Label lblNilaiBuku = Ui.label("Rp 0", "nilai-stat");
    private final Label lblPerluPerawatan = Ui.label("0", "nilai-stat");
    private final Label lblKetPenyusutan = Ui.label("", "ket-stat");

    private final PieChart donat = new PieChart();
    private final Label lblTotalDonat = Ui.label("0", "angka-donat");
    private final VBox legenda = new VBox(10);
    private final VBox barNilai = new VBox(14);
    private final TableView<Perawatan> tabelPerawatan = new TableView<>();

    public DashboardView() {
        super(16);
        setPadding(new Insets(22, 26, 22, 26));

        HBox kepala = new HBox(12, new VBox(2, Ui.label("Dashboard", "judul-halaman"), lblSapaan),
                Ui.pengisi(), lblTanggal, btnMuatUlang);
        kepala.setAlignment(Pos.CENTER_LEFT);

        HBox kartu = new HBox(16,
                kartuStat("Jumlah Aset", lblTotalAset, Ui.label("unit terdaftar", "ket-stat"), Ikon.INVENTARIS, "biru"),
                kartuStat("Nilai Perolehan", lblNilaiPerolehan, Ui.label("total harga beli", "ket-stat"), Ikon.UANG, "ungu"),
                kartuStat("Nilai Buku", lblNilaiBuku, lblKetPenyusutan, Ikon.TREN_TURUN, "hijau"),
                kartuStat("Perlu Perawatan", lblPerluPerawatan, Ui.label("jadwal sudah lewat", "ket-stat"), Ikon.KUNCI_PAS, "oranye"));

        HBox baris = new HBox(16, buatKartuKomposisi(), buatKartuNilai());

        // tabel jadwal terlewat
        LocalDate hariIni = LocalDate.now();
        TableColumn<Perawatan, String> kId = Ui.kolom("ID", p -> ((Aset) p).getId());
        TableColumn<Perawatan, String> kNama = Ui.kolom("Nama Aset", p -> ((Aset) p).getNama());
        TableColumn<Perawatan, String> kRuang = Ui.kolom("Ruangan", p -> ((Aset) p).getKodeRuangan());
        TableColumn<Perawatan, String> kTindakan = Ui.kolom("Tindakan", Perawatan::getJenisPerawatan);
        TableColumn<Perawatan, LocalDate> kJatuhTempo = Ui.kolomTanggal("Jatuh Tempo", Perawatan::getJadwalPerawatanBerikutnya);
        TableColumn<Perawatan, String> kTerlambat = Ui.kolomBadge("Status",
                p -> "Terlambat " + ChronoUnit.DAYS.between(p.getJadwalPerawatanBerikutnya(), hariIni) + " hari",
                v -> Integer.parseInt(v.replaceAll("\\D", "")) > 90 ? "badge-merah" : "badge-oranye");
        kId.setPrefWidth(90);
        kRuang.setPrefWidth(95);
        kTindakan.setPrefWidth(210);
        kJatuhTempo.setPrefWidth(115);
        kTerlambat.setPrefWidth(170);
        tabelPerawatan.getColumns().addAll(Arrays.asList(kId, kNama, kRuang, kTindakan, kJatuhTempo, kTerlambat));
        Ui.lebarFleksibel(tabelPerawatan, kNama, 180);
        tabelPerawatan.setPlaceholder(new Label("Semua aset sudah dirawat sesuai jadwal"));
        tabelPerawatan.setMinHeight(150);

        VBox kartuTabel = new VBox(12, judulKartu("Jadwal Perawatan Terlewat",
                "Aset yang perlu segera ditindaklanjuti"), tabelPerawatan);
        kartuTabel.getStyleClass().add("kartu");
        VBox.setVgrow(tabelPerawatan, Priority.ALWAYS);
        VBox.setVgrow(kartuTabel, Priority.ALWAYS);

        getChildren().addAll(kepala, kartu, baris, kartuTabel);
    }

    private VBox judulKartu(String judul, String sub) {
        return new VBox(2, Ui.label(judul, "judul-bagian"), Ui.label(sub, "teks-redup"));
    }

    private HBox kartuStat(String judul, Label nilai, Label keterangan, String ikon, String warna) {
        StackPane wadah = new StackPane(Ikon.buat(ikon, 22, "ikon-" + warna));
        wadah.getStyleClass().addAll("wadah-ikon", "wadah-" + warna);
        VBox teks = new VBox(3, Ui.label(judul, "judul-stat"), nilai, keterangan);
        HBox k = new HBox(14, wadah, teks);
        k.setAlignment(Pos.CENTER_LEFT);
        k.getStyleClass().addAll("kartu", "stat");
        k.setPrefWidth(240);
        k.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(k, Priority.ALWAYS);
        return k;
    }

    private VBox buatKartuKomposisi() {
        donat.setLabelsVisible(false);
        donat.setLegendVisible(false);
        donat.setStartAngle(90);
        donat.setAnimated(false);
        donat.setMinSize(150, 150);
        donat.setPrefSize(150, 150);
        donat.setMaxSize(150, 150);
        Region lubang = new Region();
        lubang.getStyleClass().add("lubang-donat");
        lubang.setMaxSize(82, 82);
        VBox tengah = new VBox(-2, lblTotalDonat, Ui.label("aset", "teks-redup"));
        tengah.setAlignment(Pos.CENTER);
        StackPane grafik = new StackPane(donat, lubang, tengah);

        legenda.setAlignment(Pos.CENTER_LEFT);
        HBox isi = new HBox(28, grafik, legenda);
        isi.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(legenda, Priority.ALWAYS);

        VBox k = new VBox(10, judulKartu("Komposisi Aset", "Jumlah unit per jenis aset"), isi);
        k.getStyleClass().add("kartu");
        k.setPrefWidth(420);
        k.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(k, Priority.ALWAYS);
        return k;
    }

    private VBox buatKartuNilai() {
        VBox k = new VBox(14, judulKartu("Nilai Buku per Jenis", "Nilai aset hari ini setelah penyusutan"), barNilai);
        k.getStyleClass().add("kartu");
        k.setPrefWidth(420);
        k.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(k, Priority.ALWAYS);
        return k;
    }

    private static Region titik(int urutan) {
        Region r = new Region();
        r.getStyleClass().addAll("titik", "warna-" + urutan);
        return r;
    }

    // ---------------------------------------------------- diisi oleh controller

    public void setSapaan(String teks) {
        lblSapaan.setText(teks);
        lblTanggal.setText(Format.tanggal(LocalDate.now()));
    }

    public void setStatistik(int totalAset, double nilaiPerolehan, double nilaiBuku, int perluPerawatan) {
        lblTotalAset.setText(String.valueOf(totalAset));
        lblTotalDonat.setText(String.valueOf(totalAset));
        lblNilaiPerolehan.setText(Format.rupiahRingkas(nilaiPerolehan));
        lblNilaiBuku.setText(Format.rupiahRingkas(nilaiBuku));
        lblPerluPerawatan.setText(String.valueOf(perluPerawatan));
        double sisa = nilaiPerolehan == 0 ? 0 : nilaiBuku / nilaiPerolehan * 100;
        lblKetPenyusutan.setText(String.format("%.0f%% dari harga beli", sisa));
    }

    public void setDataPerJenis(Map<JenisAset, Integer> jumlah, Map<JenisAset, Double> nilaiBuku) {
        donat.getData().clear();
        legenda.getChildren().clear();
        barNilai.getChildren().clear();
        double maks = nilaiBuku.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
        int i = 0;
        for (JenisAset j : JenisAset.values()) {
            donat.getData().add(new PieChart.Data(j.toString(), jumlah.get(j)));

            HBox barisLegenda = new HBox(10, titik(i), Ui.label(j.toString(), "subjudul"), Ui.pengisi(),
                    Ui.label(jumlah.get(j) + " unit", "judul-stat"));
            barisLegenda.setAlignment(Pos.CENTER_LEFT);
            legenda.getChildren().add(barisLegenda);

            Region rel = new Region();
            rel.getStyleClass().add("rel-bar");
            Region isiBar = new Region();
            isiBar.getStyleClass().addAll("isi-bar", "warna-" + i);
            double rasio = maks == 0 ? 0 : nilaiBuku.get(j) / maks;
            isiBar.maxWidthProperty().bind(rel.widthProperty().multiply(rasio));
            isiBar.prefWidthProperty().bind(rel.widthProperty().multiply(rasio));
            StackPane bar = new StackPane(rel, isiBar);
            bar.setAlignment(Pos.CENTER_LEFT);
            HBox atas = new HBox(Ui.label(j.toString(), "subjudul"), Ui.pengisi(),
                    Ui.label(Format.rupiah(nilaiBuku.get(j)), "judul-stat"));
            barNilai.getChildren().add(new VBox(6, atas, bar));
            i++;
        }
    }

    public void setDataPerawatan(List<Perawatan> daftar) {
        tabelPerawatan.setItems(FXCollections.observableArrayList(daftar));
    }

    public Button getBtnMuatUlang() {
        return btnMuatUlang;
    }
}
