package inventarislab.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import inventarislab.exception.ValidasiException;
import inventarislab.util.Format;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import javafx.util.StringConverter;

/**
 * Helper tampilan yang dipakai semua view: stylesheet, tombol, dialog pesan,
 * baris form, sel tabel berformat, dan pembacaan nilai DatePicker/Spinner.
 */
public final class Ui {

    public static final String CSS = Ui.class.getResource("/inventarislab/view/style.css").toExternalForm();

    private Ui() {
    }

    /** Font terbaik yang tersedia: Segoe UI (Windows), Inter, SF/Helvetica (Mac), atau bawaan sistem. */
    private static final String FONT = pilihFont();

    private static String pilihFont() {
        List<String> tersedia = javafx.scene.text.Font.getFamilies();
        for (String f : new String[] {"Segoe UI", "Inter", "SF Pro Text", "Helvetica Neue", "Noto Sans"}) {
            if (tersedia.contains(f)) {
                return f;
            }
        }
        return null;
    }

    public static String gayaFont() {
        return FONT == null ? "" : "-fx-font-family: \"" + FONT + "\";";
    }

    public static void pasangCss(Scene scene) {
        scene.getStylesheets().add(CSS);
        scene.getRoot().setStyle(gayaFont());
    }

    public static Button tombol(String teks, String... kelas) {
        Button b = new Button(teks);
        b.getStyleClass().addAll(kelas);
        return b;
    }

    /** Tombol dengan ikon di sebelah kiri teks. */
    public static Button tombolIkon(String teks, String ikon, String... kelas) {
        Button b = tombol(teks, kelas);
        b.setGraphic(Ikon.buat(ikon, 14));
        return b;
    }

    /**
     * Logo Undip yang tetap halus di ukuran kecil. Gambar diperkecil saat dimuat
     * (smooth = true, 2x untuk layar HiDPI), bukan diperkecil mendadak oleh ImageView,
     * karena pengecilan besar di ImageView membuat tepi logo bergerigi.
     */
    public static javafx.scene.image.ImageView logoUndip(double tinggi) {
        javafx.scene.image.Image img = new javafx.scene.image.Image(
                Ui.class.getResourceAsStream("/inventarislab/view/undip.png"), 0, tinggi * 2, true, true);
        javafx.scene.image.ImageView v = new javafx.scene.image.ImageView(img);
        v.setFitHeight(tinggi);
        v.setPreserveRatio(true);
        v.setSmooth(true);
        return v;
    }

    public static Label label(String teks, String... kelas) {
        Label l = new Label(teks);
        l.getStyleClass().addAll(kelas);
        return l;
    }

    public static Region pengisi() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    // ------------------------------------------------------------ form grid

    public static GridPane gridForm() {
        GridPane g = new GridPane();
        g.setHgap(12);
        g.setVgap(10);
        ColumnConstraints kolLabel = new ColumnConstraints();
        kolLabel.setMinWidth(150);
        kolLabel.setHalignment(HPos.LEFT);
        ColumnConstraints kolIsi = new ColumnConstraints();
        kolIsi.setHgrow(Priority.ALWAYS);
        kolIsi.setFillWidth(true);
        g.getColumnConstraints().addAll(kolLabel, kolIsi);
        return g;
    }

    /** Grid form dua pasang kolom (label | isian | label | isian) untuk form yang lebar. */
    public static GridPane gridForm2() {
        GridPane g = new GridPane();
        g.setHgap(12);
        g.setVgap(12);
        for (int i = 0; i < 2; i++) {
            ColumnConstraints kolLabel = new ColumnConstraints();
            kolLabel.setMinWidth(i == 0 ? 120 : 110);
            ColumnConstraints kolIsi = new ColumnConstraints();
            kolIsi.setHgrow(Priority.ALWAYS);
            kolIsi.setPrefWidth(200);
            g.getColumnConstraints().addAll(kolLabel, kolIsi);
        }
        return g;
    }

    /** Satu baris berisi dua isian berdampingan. */
    public static void barisGanda(GridPane g, int y, String label1, Node isi1, String label2, Node isi2) {
        g.add(label(label1, "label-form"), 0, y);
        g.add(lebar(isi1), 1, y);
        g.add(label(label2, "label-form"), 2, y);
        g.add(lebar(isi2), 3, y);
    }

    /** Satu baris dengan isian selebar form. */
    public static void barisPenuh(GridPane g, int y, String label, Node isi) {
        g.add(label(label, "label-form"), 0, y);
        g.add(lebar(isi), 1, y, 3, 1);
    }

    private static Node lebar(Node n) {
        if (n instanceof Region) {
            ((Region) n).setMaxWidth(Double.MAX_VALUE);
        }
        return n;
    }

    public static void baris(GridPane g, int y, String label, Node isi) {
        g.add(label(label, "label-form"), 0, y);
        if (isi instanceof Region) {
            ((Region) isi).setMaxWidth(Double.MAX_VALUE);
        }
        g.add(isi, 1, y);
    }

    // ---------------------------------------------------- tanggal & spinner

    public static DatePicker datePicker(LocalDate awal) {
        DatePicker dp = new DatePicker(awal);
        dp.setPromptText("dd-mm-yyyy");
        dp.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate d) {
                return d == null ? "" : d.format(Format.POLA_TANGGAL);
            }

            @Override
            public LocalDate fromString(String s) {
                return s == null || s.trim().isEmpty() ? null : LocalDate.parse(s.trim(), Format.POLA_TANGGAL);
            }
        });
        return dp;
    }

    /** Ambil tanggal termasuk teks yang baru diketik (belum di-commit dengan Enter). */
    public static LocalDate tanggalDari(DatePicker dp, String namaField) throws ValidasiException {
        String teks = dp.getEditor().getText();
        try {
            LocalDate d = dp.getConverter().fromString(teks);
            dp.setValue(d);
            if (d == null) {
                throw new ValidasiException(namaField, namaField + " wajib diisi.");
            }
            return d;
        } catch (java.time.format.DateTimeParseException e) {
            throw new ValidasiException(namaField, namaField + " tidak valid. Gunakan format dd-mm-yyyy.");
        }
    }

    public static Spinner<Integer> spinner(int min, int maks, int awal) {
        Spinner<Integer> s = new Spinner<>(min, maks, awal);
        s.setEditable(true);
        return s;
    }

    /** Ambil nilai spinner termasuk angka yang baru diketik. */
    public static int nilaiSpinner(Spinner<Integer> s, String namaField) throws ValidasiException {
        try {
            Integer v = s.getValueFactory().getConverter().fromString(s.getEditor().getText().trim());
            s.getValueFactory().setValue(v);
            return v;
        } catch (NumberFormatException e) {
            throw new ValidasiException(namaField, namaField + " harus berupa angka bulat.");
        }
    }

    public static void tandaiError(Control c) {
        if (c != null && !c.getStyleClass().contains("error")) {
            c.getStyleClass().add("error");
        }
    }

    public static void bersihkanError(Control... daftar) {
        for (Control c : daftar) {
            c.getStyleClass().remove("error");
        }
    }

    // ------------------------------------------------------- sel tabel

    /** Kolom ini mengisi sisa lebar tabel; kolom lain memakai lebar tetap. */
    public static <S> void lebarFleksibel(TableView<S> tabel, TableColumn<S, ?> fleksibel, double minimum) {
        fleksibel.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> {
            double lain = 0;
            for (TableColumn<S, ?> k : tabel.getColumns()) {
                if (k != fleksibel) {
                    lain += k.getWidth();
                }
            }
            return Math.max(minimum, tabel.getWidth() - lain - 22);
        }, tabel.widthProperty()));
    }

    /** Kolom angka Double yang ditampilkan sebagai Rupiah rata kanan. */
    public static <S> TableColumn<S, Double> kolomRupiah(String judul, Function<S, Double> nilai) {
        TableColumn<S, Double> kol = new TableColumn<>(judul);
        kol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(nilai.apply(c.getValue())));
        kol.setCellFactory(k -> new TableCell<S, Double>() {
            {
                getStyleClass().add("sel-angka");
            }

            @Override
            protected void updateItem(Double v, boolean kosong) {
                super.updateItem(v, kosong);
                setText(kosong || v == null ? null : Format.rupiah(v));
            }
        });
        return kol;
    }

    /** Kolom LocalDate: data tetap LocalDate (urutan kronologis), tampil dd-MM-yyyy. */
    public static <S> TableColumn<S, LocalDate> kolomTanggal(String judul, Function<S, LocalDate> nilai) {
        TableColumn<S, LocalDate> kol = new TableColumn<>(judul);
        kol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(nilai.apply(c.getValue())));
        kol.setCellFactory(k -> new TableCell<S, LocalDate>() {
            @Override
            protected void updateItem(LocalDate v, boolean kosong) {
                super.updateItem(v, kosong);
                setText(kosong || v == null ? null : Format.tanggal(v));
            }
        });
        return kol;
    }

    public static <S, T> TableColumn<S, T> kolom(String judul, Function<S, T> nilai) {
        TableColumn<S, T> kol = new TableColumn<>(judul);
        kol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(nilai.apply(c.getValue())));
        kol.setCellFactory(k -> new TableCell<S, T>() {
            @Override
            protected void updateItem(T v, boolean kosong) {
                super.updateItem(v, kosong);
                String teks = kosong || v == null ? null : v.toString();
                setText(teks);
                // teks panjang yang terpotong tetap bisa dibaca lewat tooltip
                setTooltip(teks == null || teks.length() < 18 ? null : new javafx.scene.control.Tooltip(teks));
            }
        });
        return kol;
    }

    /** Kolom teks yang warnanya mengikuti nilai (mis. kondisi Baik/Rusak). */
    public static <S> TableColumn<S, String> kolomTeksBerwarna(String judul, Function<S, String> nilai,
                                                                Function<String, String> kelasCss) {
        TableColumn<S, String> kol = kolom(judul, nilai);
        kol.setCellFactory(k -> new TableCell<S, String>() {
            private String kelasLama;

            @Override
            protected void updateItem(String v, boolean kosong) {
                super.updateItem(v, kosong);
                if (kelasLama != null) {
                    getStyleClass().remove(kelasLama);
                    kelasLama = null;
                }
                setText(kosong ? null : v);
                if (!kosong && v != null) {
                    kelasLama = kelasCss.apply(v);
                    getStyleClass().add(kelasLama);
                }
            }
        });
        return kol;
    }

    /** Kolom teks yang ditampilkan sebagai label status (badge) berwarna. */
    public static <S> TableColumn<S, String> kolomBadge(String judul, Function<S, String> nilai,
                                                         Function<String, String> kelasBadge) {
        TableColumn<S, String> kol = kolom(judul, nilai);
        kol.setCellFactory(k -> new TableCell<S, String>() {
            @Override
            protected void updateItem(String v, boolean kosong) {
                super.updateItem(v, kosong);
                setText(null);
                if (kosong || v == null) {
                    setGraphic(null);
                } else {
                    Label b = label(v, "badge", kelasBadge.apply(v));
                    b.setMinWidth(Region.USE_PREF_SIZE); // badge tidak boleh terpotong "..."
                    setGraphic(b);
                }
            }
        });
        return kol;
    }

    // ------------------------------------------------------- dialog pesan

    private static Alert alert(AlertType tipe, Window induk, String judul, String header, String pesan,
                               ButtonType... tombol) {
        Alert a = new Alert(tipe, pesan, tombol);
        a.setTitle(judul);
        a.setHeaderText(header);
        if (induk != null) {
            a.initOwner(induk);
        }
        a.getDialogPane().getStylesheets().add(CSS);
        a.getDialogPane().setStyle(gayaFont());
        a.setGraphic(null);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.getDialogPane().setMinWidth(420);
        return a;
    }

    private static final ButtonType OK = new ButtonType("OK", ButtonData.OK_DONE);

    public static void info(Window induk, String pesan) {
        alert(AlertType.INFORMATION, induk, "Informasi", "Berhasil", pesan, OK).showAndWait();
    }

    public static void error(Window induk, String pesan) {
        alert(AlertType.ERROR, induk, "Terjadi Kesalahan", "Data belum dapat diproses", pesan, OK).showAndWait();
    }

    public static void peringatan(Window induk, String pesan) {
        alert(AlertType.WARNING, induk, "Perhatian", "Perhatian", pesan, OK).showAndWait();
    }

    /** Dialog konfirmasi Ya/Tidak; mengembalikan true hanya bila "Ya" dipilih. */
    public static boolean konfirmasi(Window induk, String judul, String header, String pesan) {
        ButtonType ya = new ButtonType("Ya", ButtonData.YES);
        ButtonType tidak = new ButtonType("Tidak", ButtonData.NO);
        Alert a = alert(AlertType.CONFIRMATION, induk, judul, header, pesan, ya, tidak);
        Optional<ButtonType> hasil = a.showAndWait();
        return hasil.isPresent() && hasil.get() == ya;
    }

    /** Dialog informasi berisi tabel label : nilai (dipakai tombol Detail). */
    public static void detail(Window induk, String judul, String header, List<String[]> baris) {
        GridPane g = new GridPane();
        g.setHgap(16);
        g.setVgap(6);
        g.setPadding(new Insets(10, 20, 10, 20));
        for (int i = 0; i < baris.size(); i++) {
            String[] b = baris.get(i);
            if (b.length == 1) {
                Label sub = label(b[0], "judul-bagian");
                GridPane.setColumnSpan(sub, 2);
                GridPane.setMargin(sub, new Insets(8, 0, 0, 0));
                g.add(sub, 0, i);
            } else {
                g.add(label(b[0], "label-form"), 0, i);
                Label v = new Label(b[1]);
                if (b.length > 2) {
                    v.getStyleClass().add(b[2]);
                }
                g.add(v, 1, i);
            }
        }
        Alert a = alert(AlertType.INFORMATION, induk, judul, header, null, OK);
        a.getDialogPane().setContent(g);
        a.showAndWait();
    }
}
